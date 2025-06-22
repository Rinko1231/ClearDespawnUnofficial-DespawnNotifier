package com.rinko1231.despawnnotifier.network;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端网络处理器
 */
public class ClientPayloadHandler {
    
    private static final ConcurrentHashMap<Integer, ItemEntityAgeData> ITEM_AGE_CACHE = new ConcurrentHashMap<>();
    
    /**
     * 处理来自服务器的ItemEntity同步数据包
     */
    public static void handleItemEntitySyncOnMain(final ItemEntitySyncPacket packet, final IPayloadContext context) {
        if (packet.age() == -1 && packet.lifespan() == -1) {
            ITEM_AGE_CACHE.remove(packet.entityId());
            return;
        }
        
        // 在主线程上缓存数据
        ITEM_AGE_CACHE.put(packet.entityId(), new ItemEntityAgeData(packet.age(), packet.lifespan(), System.currentTimeMillis()));
    }
    
    /**
     * 获取指定实体的年龄数据
     * @param entityId 实体ID
     * @return 年龄数据，如果没有缓存则返回null
     */
    public static ItemEntityAgeData getItemAgeData(int entityId) {
        ItemEntityAgeData data = ITEM_AGE_CACHE.get(entityId);
        if (data != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - data.timestamp() > 10000) {
                ITEM_AGE_CACHE.remove(entityId);
                return null;
            }
        }
        return data;
    }
    
    /**
     * 清理过期的缓存数据
     */
    public static void cleanupExpiredData() {
        long currentTime = System.currentTimeMillis();
        ITEM_AGE_CACHE.entrySet().removeIf(entry -> 
            currentTime - entry.getValue().timestamp() > 10000
        );
    }
    
    /**
     * 移除指定实体的缓存数据（当实体被移除时调用）
     */
    public static void removeItemAgeData(int entityId) {
        ITEM_AGE_CACHE.remove(entityId);
    }
    
    /**
     * 获取当前缓存的实体数量（用于调试）
     */
    public static int getCachedEntityCount() {
        return ITEM_AGE_CACHE.size();
    }
    
    /**
     * ItemEntity年龄数据记录
     */
    public record ItemEntityAgeData(int age, int lifespan, long timestamp) {
        
        /**
         * 获取当前估算的年龄（考虑时间流逝）
         */
        public int getCurrentAge() {
            long timePassed = (System.currentTimeMillis() - timestamp) / 50; // 转换为tick
            return age + (int) timePassed;
        }
        
        /**
         * 获取剩余生命时间（tick）
         */
        public int getRemainingLife() {
            return Math.max(0, lifespan - getCurrentAge());
        }
        
        /**
         * 检查数据是否仍然有效（未过期）
         */
        public boolean isValid() {
            return (System.currentTimeMillis() - timestamp) <= 10000;
        }
    }
} 