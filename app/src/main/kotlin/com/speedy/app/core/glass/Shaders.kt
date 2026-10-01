package com.speedy.app.core.glass

import org.intellij.lang.annotations.Language

/**
 * AGSL Shaders for Liquid Glass optical pipeline on Android 13+ (API 33+).
 */
object Shaders {

    @Language("AGSL")
    const val LENS_SHADER_SOURCE = """
        uniform shader content;
        uniform float2 size;
        uniform float4 cornerRadii;
        uniform float refractionHeight;
        uniform float refractionAmount;
        uniform float depthEffect;
        uniform float chromaticAberration;

        float sdRoundedBox(float2 p, float2 b, float4 r) {
            r.xy = (p.x > 0.0) ? r.yz : r.xw;
            r.x  = (p.y > 0.0) ? r.y  : r.x;
            float2 q = abs(p) - b + r.x;
            return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
        }

        half4 main(float2 fragCoord) {
            float2 center = size * 0.5;
            float2 halfSize = center;
            float2 p = fragCoord - center;

            float dist = sdRoundedBox(p, halfSize, cornerRadii);
            float rimFactor = clamp(-dist / max(refractionHeight, 1.0), 0.0, 1.0);
            float2 normal = (length(p) > 0.001) ? normalize(p) : float2(0.0, 0.0);

            float displacement = sin((1.0 - rimFactor) * 1.5707963) * refractionAmount;
            if (depthEffect > 0.5) {
                displacement *= (1.0 - 0.3 * (length(p) / length(halfSize)));
            }

            float2 offset = -normal * displacement;

            if (chromaticAberration > 0.5) {
                float2 offsetR = offset * 0.92;
                float2 offsetG = offset * 1.00;
                float2 offsetB = offset * 1.08;

                half r = content.eval(fragCoord + offsetR).r;
                half g = content.eval(fragCoord + offsetG).g;
                half b = content.eval(fragCoord + offsetB).b;
                half a = content.eval(fragCoord + offset).a;

                return half4(r, g, b, a);
            } else {
                return content.eval(fragCoord + offset);
            }
        }
    """

    @Language("AGSL")
    const val HIGHLIGHT_SHADER_SOURCE = """
        uniform float2 size;
        uniform float4 cornerRadii;
        uniform float angle;
        uniform float falloff;
        uniform half4 highlightColor;

        float sdRoundedBox(float2 p, float2 b, float4 r) {
            r.xy = (p.x > 0.0) ? r.yz : r.xw;
            r.x  = (p.y > 0.0) ? r.y  : r.x;
            float2 q = abs(p) - b + r.x;
            return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
        }

        half4 main(float2 fragCoord) {
            float2 center = size * 0.5;
            float2 p = fragCoord - center;
            float dist = sdRoundedBox(p, center, cornerRadii);

            if (dist > 0.0 || dist < -6.0) {
                return half4(0.0);
            }

            float rad = radians(angle);
            float2 lightDir = float2(cos(rad), sin(rad));
            float2 normal = normalize(p);

            float lightIntensity = max(dot(normal, -lightDir), 0.0);
            float edgeFactor = 1.0 - abs(dist + 1.5) / 1.5;
            edgeFactor = clamp(edgeFactor, 0.0, 1.0);
            edgeFactor = pow(edgeFactor, falloff);

            float finalAlpha = highlightColor.a * lightIntensity * edgeFactor;
            return half4(highlightColor.rgb * finalAlpha, finalAlpha);
        }
    """
}
