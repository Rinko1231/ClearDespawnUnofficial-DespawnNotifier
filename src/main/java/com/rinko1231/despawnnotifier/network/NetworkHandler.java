package com.rinko1231.despawnnotifier.network;

import com.rinko1231.despawnnotifier.DespawnNotifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = DespawnNotifier.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
            ItemEntitySyncPacket.TYPE,
            ItemEntitySyncPacket.STREAM_CODEC,
            ClientPayloadHandler::handleItemEntitySyncOnMain
        );
    }
} 