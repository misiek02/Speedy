package com.speedy.app.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Outline
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.speedy.app.MainActivity
import com.speedy.app.core.location.SpeedTracker
import com.speedy.app.core.settings.AppSettings
import com.speedy.app.core.settings.AppStrings
import com.speedy.app.core.settings.AppThemeStyle
import com.speedy.app.ui.components.SpeedBubble
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Foreground Service hosting the floating speed bubble popup.
 *
 * Runs continuously over other applications, allowing the user to reposition
 * the Liquid Glass speed indicator anywhere on the screen.
 */
class SpeedOverlayService : LifecycleService(), SavedStateRegistryOwner, ViewModelStoreOwner {

    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val appViewModelStore = ViewModelStore()

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore

    private var windowManager: WindowManager? = null
    private var draggableContainer: DraggableOverlayLayout? = null

    companion object {
        const val CHANNEL_ID = "speedy_overlay_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP_SERVICE = "com.speedy.app.ACTION_STOP_SERVICE"

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

        fun start(context: Context) {
            val intent = Intent(context, SpeedOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, SpeedOverlayService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        savedStateRegistryController.performRestore(Bundle())
        super.onCreate()

        _isRunning.value = true
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("0", "KM/H"))

        SpeedTracker.startTracking(this)

        if (Settings.canDrawOverlays(this)) {
            setupOverlayWindow()
        } else {
            stopSelf()
            return
        }

        // Observe speed updates to refresh notification
        CoroutineScope(Dispatchers.Main).launch {
            SpeedTracker.speedData.collect { data ->
                val notification = buildNotification(data.displaySpeedInt, data.displayUnit.uppercase())
                val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(NOTIFICATION_ID, notification)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            stopSelf()
            return START_NOT_STICKY
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setupOverlayWindow() {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager = wm

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 80
            y = 260
        }

        val container = DraggableOverlayLayout(
            context = this,
            windowManager = wm,
            layoutParams = params,
            onBubbleClick = { openMainActivity() }
        ).apply {
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setOval(0, 0, view.width, view.height)
                }
            }
        }
        draggableContainer = container

        // Attach Lifecycle and SavedState owners to support ComposeView in WindowManager
        container.setViewTreeLifecycleOwner(this)
        container.setViewTreeSavedStateRegistryOwner(this)
        container.setViewTreeViewModelStoreOwner(this)

        val composeView = ComposeView(this).apply {
            setContent {
                val speedData by SpeedTracker.speedData.collectAsState()
                val themeStyle by AppSettings.themeStyle.collectAsState()
                SpeedBubble(
                    speedData = speedData,
                    isMaterial3 = (themeStyle == AppThemeStyle.MATERIAL3)
                )
            }
        }

        container.addView(composeView)

        try {
            wm.addView(container, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        try {
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            pendingIntent.send()
        } catch (e: Exception) {
            try {
                startActivity(intent)
            } catch (err: Exception) {
                err.printStackTrace()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Speedy Floating Speedometer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live GPS speed overlay above apps"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(speed: String, unit: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        val stopIntent = Intent(this, SpeedOverlayService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val lang = AppSettings.language.value
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Speedy: $speed $unit")
            .setContentText(AppStrings.notificationText(lang))
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(openAppIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, AppStrings.closeAction(lang), stopPendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        _isRunning.value = false

        draggableContainer?.let { container ->
            try {
                windowManager?.removeView(container)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        draggableContainer = null
        appViewModelStore.clear()
    }
}

/**
 * Custom FrameLayout intercepting touch events to allow smooth 1:1 dragging
 * of the overlay window across the entire Android display without child view interference.
 */
class DraggableOverlayLayout(
    context: Context,
    private val windowManager: WindowManager,
    private val layoutParams: WindowManager.LayoutParams,
    private val onBubbleClick: () -> Unit
) : FrameLayout(context) {

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var downTime = 0L

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // Intercept all touches so child ComposeView never blocks dragging
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = layoutParams.x
                initialY = layoutParams.y
                initialTouchX = ev.rawX
                initialTouchY = ev.rawY
                isDragging = false
                downTime = System.currentTimeMillis()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (ev.rawX - initialTouchX).toInt()
                val dy = (ev.rawY - initialTouchY).toInt()

                if (abs(dx) > touchSlop || abs(dy) > touchSlop) {
                    isDragging = true
                }

                layoutParams.x = initialX + dx
                layoutParams.y = initialY + dy
                android.util.Log.d("SpeedyDrag", "rawX=${ev.rawX}, initTouchX=$initialTouchX, dx=$dx, initX=$initialX, newX=${layoutParams.x}")

                try {
                    windowManager.updateViewLayout(this, layoutParams)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val elapsed = System.currentTimeMillis() - downTime
                val dx = abs(ev.rawX - initialTouchX)
                val dy = abs(ev.rawY - initialTouchY)

                if (!isDragging && elapsed < 350 && dx < touchSlop && dy < touchSlop) {
                    onBubbleClick()
                }
                isDragging = false
                return true
            }
        }
        return super.onTouchEvent(ev)
    }
}
