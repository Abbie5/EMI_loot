package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.block.Block;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.structure.rule.TagMatchRuleTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagMatchRuleTest.class)
public interface TagMatchRuleTestAccessor {
    @Accessor
    TagKey<Block> getTag();
}
