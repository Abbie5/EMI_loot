package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.parser.LootTableParser;

import java.util.List;

public class WorldGenLootBuilder extends AbstractLootPoolBuilder {
    public WorldGenLootBuilder(float rollWeight) {
        super(rollWeight);
    }

    @Override
    public void addItem(LootTableParser.ItemEntryResult result) {

    }

    @Override
    public void build() {

    }

    @Override
    public List<LootTableParser.ItemEntryResult> revert() {
        return null;
    }
}
