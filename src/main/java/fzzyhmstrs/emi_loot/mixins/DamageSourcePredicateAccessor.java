package fzzyhmstrs.emi_loot.mixins;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(DamageSourcePredicate.class)
public interface DamageSourcePredicateAccessor {

    @Accessor(value = "tagPredicates")
    List<TagPredicate<DamageType>> getTagPredicates();
    @Accessor(value = "directEntity")
    EntityPredicate getDirectEntity();
    @Accessor(value = "sourceEntity")
    EntityPredicate getSourceEntity();

}
