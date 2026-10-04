package kz.qozha.mixin;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Makes the world permanently foggy (grey, thick). Water/lava fog is left alone. */
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Inject(method = "applyFog", at = @At("RETURN"), cancellable = true)
    private static void qozha$thickFog(Camera camera, BackgroundRenderer.FogType fogType, Vector4f color,
                                       float viewDistance, boolean thickenFog, float tickDelta,
                                       CallbackInfoReturnable<Fog> cir) {
        if (camera.getSubmersionType() != CameraSubmersionType.NONE) return;
        Fog original = cir.getReturnValue();

        float start = 2.0f;     // fog begins this many blocks away
        float end = 45.0f;      // fully foggy at this distance (raise = thinner fog)
        cir.setReturnValue(new Fog(start, Math.min(original.end(), end), FogShape.SPHERE,
            0.58f, 0.60f, 0.64f, original.alpha()));   // grey colour (r, g, b)
    }
}
