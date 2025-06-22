package com.rinko1231.despawnnotifier.server;

import com.rinko1231.despawnnotifier.config.DespawnNotifierConfig;
import com.rinko1231.despawnnotifier.network.ItemEntitySyncPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * 服务器端ItemEntity同步管理器
 */
public class ItemEntitySyncManager {
    
    private static long lastSyncTime = 0;
    private static final long SYNC_INTERVAL = 1000;

    public static void tick(ServerLevel level) {
        long currentTime = System.currentTimeMillis();
        
        // 检查是否到了同步时间
        if (currentTime - lastSyncTime < SYNC_INTERVAL) {
            return;
        }
        
        lastSyncTime = currentTime;
        
        // 获取所有ItemEntity（使用getAllEntities遍历）
        level.getAllEntities().forEach(entity -> {
            if (entity instanceof ItemEntity itemEntity) {
                int remainingTime = itemEntity.lifespan - itemEntity.getAge();
                int warningStartTime = DespawnNotifierConfig.itemFlashStartTime.get() * 20;
                if (remainingTime <= warningStartTime && remainingTime > 0) {
                    // 创建同步数据包
                    ItemEntitySyncPacket packet = new ItemEntitySyncPacket(
                        itemEntity.getId(),
                        itemEntity.getAge(),
                        itemEntity.lifespan
                    );
                    PacketDistributor.sendToPlayersTrackingEntity(itemEntity, packet);
                }
            }
        });
    }
    
    /**
     * 当ItemEntity被创建时，立即同步给附近玩家
     * 这确保新物品能被立即检测到
     */
    public static void onItemEntitySpawned(ItemEntity entity) {
        // 立即发送同步数据包给附近玩家
        ItemEntitySyncPacket packet = new ItemEntitySyncPacket(
            entity.getId(),
            entity.getAge(),
            entity.lifespan
        );
        
        PacketDistributor.sendToPlayersTrackingEntity(entity, packet);
    }
    
    /**
     * 当ItemEntity被移除时，通知客户端清理缓存
     * （虽然客户端也有自动清理机制，但主动清理更及时）
     */
    public static void onItemEntityRemoved(ItemEntity entity) {
        // 发送一个特殊的数据包表示实体已被移除
        // 使用-1作为age和lifespan来表示移除
        ItemEntitySyncPacket packet = new ItemEntitySyncPacket(
            entity.getId(),
            -1,
            -1
        );
        
        PacketDistributor.sendToPlayersTrackingEntity(entity, packet);
    }
} 