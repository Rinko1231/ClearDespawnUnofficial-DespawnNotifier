package com.rinko1231.despawnnotifier.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 用于同步ItemEntity年龄信息
 */
public record ItemEntitySyncPacket(int entityId, int age, int lifespan) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<ItemEntitySyncPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("despawnnotifier", "item_entity_sync"));

    public static final StreamCodec<FriendlyByteBuf, ItemEntitySyncPacket> STREAM_CODEC = StreamCodec.ofMember(
        ItemEntitySyncPacket::write,
        ItemEntitySyncPacket::read
    );

    private void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.age);
        buf.writeVarInt(this.lifespan);
    }

    private static ItemEntitySyncPacket read(FriendlyByteBuf buf) {
        int entityId = buf.readVarInt();
        int age = buf.readVarInt();
        int lifespan = buf.readVarInt();
        return new ItemEntitySyncPacket(entityId, age, lifespan);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
} 