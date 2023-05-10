package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.DamageSourcePredicateAccessor;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.text.Text;

import java.util.List;

public class DamageSourcePredicateParser {

    public static Text parseDamageSourcePredicate(DamageSourcePredicate predicate){

        List<TagPredicate<DamageType>> predicates = ((DamageSourcePredicateAccessor)predicate).getTagPredicates();

        if (predicates.contains(TagPredicate.expected(DamageTypeTags.IS_PROJECTILE))){
            return LText.translatable("emi_loot.damage_predicate.projectile_true");
        } else if (predicates.contains(TagPredicate.unexpected(DamageTypeTags.IS_PROJECTILE))) {
            return LText.translatable("emi_loot.damage_predicate.projectile_false");
        }

        if (predicates.contains(TagPredicate.expected(DamageTypeTags.IS_EXPLOSION))){
            return LText.translatable("emi_loot.damage_predicate.explosion_true");
        } else if (predicates.contains(TagPredicate.unexpected(DamageTypeTags.IS_EXPLOSION))) {
            return LText.translatable("emi_loot.damage_predicate.explosion_false");
        }

        if (predicates.contains(TagPredicate.expected(DamageTypeTags.BYPASSES_ARMOR))){
            return LText.translatable("emi_loot.damage_predicate.bypass_armor_true");
        } else if (predicates.contains(TagPredicate.unexpected(DamageTypeTags.BYPASSES_ARMOR))) {
            return LText.translatable("emi_loot.damage_predicate.bypass_armor_false");
        }

        if (predicates.contains(TagPredicate.expected(DamageTypeTags.BYPASSES_INVULNERABILITY))){
            return LText.translatable("emi_loot.damage_predicate.no_invulnerable_true");
        } else if (predicates.contains(TagPredicate.unexpected(DamageTypeTags.BYPASSES_INVULNERABILITY))) {
            return LText.translatable("emi_loot.damage_predicate.no_invulnerable_false");
        }

        if (predicates.contains(TagPredicate.expected(DamageTypeTags.BYPASSES_ENCHANTMENTS))){
            return LText.translatable("emi_loot.damage_predicate.unblockable_true");
        } else if (predicates.contains(TagPredicate.unexpected(DamageTypeTags.BYPASSES_ENCHANTMENTS))){
            return LText.translatable("emi_loot.damage_predicate.unblockable_false");
        }

        if (predicates.contains(TagPredicate.expected(DamageTypeTags.IS_FIRE))){
            return LText.translatable("emi_loot.damage_predicate.fire_true");
        } else if (predicates.contains(TagPredicate.unexpected(DamageTypeTags.IS_FIRE))){
            return LText.translatable("emi_loot.damage_predicate.fire_false");
        }

        // TODO
        if (false){
            return LText.translatable("emi_loot.damage_predicate.magic_true");
        } else if (false){
            return LText.translatable("emi_loot.damage_predicate.magic_false");
        }

        if (predicates.contains(TagPredicate.expected(DamageTypeTags.IS_LIGHTNING))){
            return LText.translatable("emi_loot.damage_predicate.lightning_true");
        } else if (predicates.contains(TagPredicate.unexpected(DamageTypeTags.IS_LIGHTNING))){
            return LText.translatable("emi_loot.damage_predicate.lightning_false");
        }

        EntityPredicate directPredicate = ((DamageSourcePredicateAccessor)predicate).getDirectEntity();
        if (!directPredicate.equals(EntityPredicate.ANY)){
            return EntityPredicateParser.parseEntityPredicate(directPredicate);
        }

        EntityPredicate sourcePredicate = ((DamageSourcePredicateAccessor)predicate).getSourceEntity();
        if (!sourcePredicate.equals(EntityPredicate.ANY)){
            return EntityPredicateParser.parseEntityPredicate(sourcePredicate);
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable damage source predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

}
