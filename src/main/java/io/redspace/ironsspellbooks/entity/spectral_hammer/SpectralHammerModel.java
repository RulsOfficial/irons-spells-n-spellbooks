package io.redspace.ironsspellbooks.entity.spectral_hammer;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SpectralHammerModel extends AnimatedGeoModel<SpectralHammer> {
    public static final ResourceLocation modelResource = new ResourceLocation(IronsSpellbooks.MODID, "geo/spectral_hammer.geo.json");
    public static final ResourceLocation textureResource = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/spectral_hammer/spectral_hammer.png");
    public static final ResourceLocation animationResource = new ResourceLocation(IronsSpellbooks.MODID, "animations/spectral_hammer.animation.json");

    @Override
    public ResourceLocation getModelResource(SpectralHammer object) {
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureResource(SpectralHammer object) {
        return textureResource;
    }

    @Override
    public ResourceLocation getAnimationResource(SpectralHammer animatable) {
        return animationResource;
    }
}
