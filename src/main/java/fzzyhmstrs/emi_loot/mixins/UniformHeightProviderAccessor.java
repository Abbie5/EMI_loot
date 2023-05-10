package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(UniformHeightProvider.class)
public interface UniformHeightProviderAccessor {
    @Accessor
    YOffset getMinOffset();

    @Accessor
    YOffset getMaxOffset();
}
