package fzzyhmstrs.emi_loot.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootClient;
import fzzyhmstrs.emi_loot.client.*;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class EmiClientPlugin implements EmiPlugin {
    private static final Identifier LOOT_ID = new Identifier(EMILoot.MOD_ID,"chest_loot");
    private static final Identifier BLOCK_ID = new Identifier(EMILoot.MOD_ID,"block_drops");
    private static final Identifier MOB_ID = new Identifier(EMILoot.MOD_ID,"mob_drops");
    private static final Identifier GAMEPLAY_ID = new Identifier(EMILoot.MOD_ID,"gameplay_drops");
    private static final Identifier WORLGEN_ID = new Identifier(EMILoot.MOD_ID,"worldgen");
    public static final EmiRecipeCategory LOOT_CATEGORY = new EmiRecipeCategory(LOOT_ID, EmiStack.of(Blocks.CHEST.asItem()), new LootSimplifiedRenderer(0,0));
    public static final EmiRecipeCategory BLOCK_CATEGORY = new EmiRecipeCategory(BLOCK_ID, EmiStack.of(Blocks.DIAMOND_ORE.asItem()), new LootSimplifiedRenderer(16,0));
    public static final EmiRecipeCategory MOB_CATEGORY = new EmiRecipeCategory(MOB_ID, EmiStack.of(Blocks.ZOMBIE_HEAD.asItem()), new LootSimplifiedRenderer(0,16));
    public static final EmiRecipeCategory GAMEPLAY_CATEGORY = new EmiRecipeCategory(GAMEPLAY_ID, EmiStack.of(Items.FISHING_ROD), new LootSimplifiedRenderer(16,16));
    public static final EmiRecipeCategory WORLDGEN_CATEGORY = new EmiRecipeCategory(WORLGEN_ID, EmiStack.of(Blocks.GRASS_BLOCK), null);

    @Override
    public void register(EmiRegistry registry) {

        registry.addCategory(LOOT_CATEGORY);
        registry.addCategory(BLOCK_CATEGORY);
        registry.addCategory(MOB_CATEGORY);
        registry.addCategory(GAMEPLAY_CATEGORY);
        registry.addCategory(WORLDGEN_CATEGORY);

        EMILootClient.tables.getLoots().forEach(lootReceiver -> {
            if (!lootReceiver.isEmpty()) {
                if (lootReceiver instanceof ClientChestLootTable) {
                    registry.addRecipe(new ChestLootRecipe((ClientChestLootTable) lootReceiver));
                }
                if (lootReceiver instanceof ClientBlockLootTable) {
                    registry.addRecipe(new BlockLootRecipe((ClientBlockLootTable) lootReceiver));
                }
                if (lootReceiver instanceof ClientMobLootTable) {
                    registry.addRecipe(new MobLootRecipe((ClientMobLootTable) lootReceiver));
                }
                if (lootReceiver instanceof ClientGameplayLootTable) {
                    registry.addRecipe(new GameplayLootRecipe((ClientGameplayLootTable) lootReceiver));
                }
                if (lootReceiver instanceof ClientWorldGenLootTable w) {
                    registry.addRecipe(new WorldGenRecipe(w));
                }
            }
        });
    }
}
