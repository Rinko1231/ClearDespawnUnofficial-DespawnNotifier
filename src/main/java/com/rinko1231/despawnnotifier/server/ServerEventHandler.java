package com.rinko1231.despawnnotifier.server;

import com.rinko1231.despawnnotifier.DespawnNotifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * 服务器端事件处理器
 */
@EventBusSubscriber(modid = DespawnNotifier.MODID)
public class ServerEventHandler {
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            if (serverLevel.getGameTime() % 20 == 0) {
                ItemEntitySyncManager.tick(serverLevel);
            }
        }
    }
    
    /**
     * 处理实体进入世界事件
     */
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof ItemEntity itemEntity) {
            ItemEntitySyncManager.onItemEntitySpawned(itemEntity);
        }
    }
    
    /**
     * 处理实体离开世界
     */
    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof ItemEntity itemEntity) {
            ItemEntitySyncManager.onItemEntityRemoved(itemEntity);
        }
    }
} 