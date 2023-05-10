package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WorldGenLootSender implements LootSender<WorldGenLootBuilder> {
    private final String idToSend;
    public static Identifier WORLDGEN_SENDER = new Identifier("e_l", "w_s");
    final List<WorldGenLootBuilder> builderList = new LinkedList<>();
    boolean isEmpty = true;

    public WorldGenLootSender(Identifier id) {
        this.idToSend = LootSender.getIdToSend(id);
    }
    @Override
    public String getId() {
        return idToSend;
    }

    @Override
    public void send(ServerPlayerEntity player) {
        if (isEmpty) {
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("avoiding empty worlgen: " + idToSend);
            return;
        }

        PacketByteBuf buf = PacketByteBufs.create();
        //start with the loot pool ID and the number of builders to write check a few special conditions to send compressed shortcut packets
        buf.writeString(idToSend);
        //pre-build the builders to do empty checks
        if (builderList.size() == 1 && builderList.get(0).isSimple){
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("sending simple worldgen: " + idToSend);
            buf.writeByte(-1);
            Block block = ((BlockItem) builderList.get(0).simpleStack.getItem()).getBlock();
            buf.writeRegistryValue(Registries.BLOCK,block);
            ServerPlayNetworking.send(player,WORLDGEN_SENDER, buf);
            return;
        } else if (builderList.isEmpty()){
            return;
        }

        buf.writeByte(builderList.size());
//        builderList.forEach((builder)->{
//
//            //write size of the builders condition set
//            buf.writeByte(builder.conditions.size());
//            //write the textkey of each of those conditions
//            builder.conditions.forEach((lootConditionResult -> lootConditionResult.text().toBuf(buf)));
//
//            //write size of the builders function set
//            buf.writeByte(builder.functions.size());
//            //write the textkey of the functions
//            builder.functions.forEach((lootFunctionResult)-> lootFunctionResult.text().toBuf(buf));
//            //write the size of the builtMap of individual chest pools
//            Map<List<TextKey>,ChestLootPoolBuilder> lootPoolBuilderMap = builder.builtMap;
//            buf.writeByte(lootPoolBuilderMap.size());
//            lootPoolBuilderMap.forEach((key,chestBuilder)->{
//
//                //for each functional condition, write the size then list of condition textKeys
//                buf.writeByte(key.size());
//                key.forEach((textKey)->textKey.toBuf(buf));
//
//                //for each functional condition, write the size of the actual itemstacks
//                Map<ItemStack,Float> keyPoolMap = lootPoolBuilderMap.getOrDefault(key,new ChestLootPoolBuilder(1f)).builtMap;
//                buf.writeByte(keyPoolMap.size());
//
//                //for each itemstack, write the stack and weight
//                keyPoolMap.forEach((stack,weight)->{
//                    buf.writeItemStack(stack);
//                    buf.writeFloat(weight);
//                });
//            });
//
//        });
        ServerPlayNetworking.send(player,WORLDGEN_SENDER, buf);
    }

    @Override
    public void addBuilder(WorldGenLootBuilder builder) {
        builderList.add(builder);
    }

    @Override
    public List<WorldGenLootBuilder> getBuilders() {
        return builderList;
    }

    @Override
    public void build() {
        builderList.forEach((builder) -> {
            builder.build();
            if (!builder.isEmpty) {
                isEmpty = false;
            }
        });
    }
}
