package fzzyhmstrs.emi_loot.client;

import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.block.Block;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class ClientWorldGenLootTable extends AbstractTextKeyParsingClientLootTable<ClientWorldGenLootTable> {
    public static final ClientWorldGenLootTable INSTANCE = new ClientWorldGenLootTable();

    @Override
    List<Pair<Integer, Text>> getSpecialTextKeyList(World world, Block block) {
        return null;
    }

    @Override
    Pair<Identifier, Identifier> getBufId(PacketByteBuf buf) {
        return null;
    }

    @Override
    ClientWorldGenLootTable simpleTableToReturn(Pair<Identifier, Identifier> ids, PacketByteBuf buf) {
        return null;
    }

    @Override
    ClientWorldGenLootTable emptyTableToReturn() {
        return null;
    }

    @Override
    ClientWorldGenLootTable filledTableToReturn(Pair<Identifier, Identifier> ids, Map<List<TextKey>, ClientRawPool> itemMap) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Identifier getId() {
        return null;
    }
}
