package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.block.Block;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockMatchRuleTest.class)
public interface BlockMatchRuleTestAccessor {
    @Accessor
    Block getBlock();
}
