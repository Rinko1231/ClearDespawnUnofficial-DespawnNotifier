package com.rinko1231.despawnnotifier.mixin;

import com.rinko1231.despawnnotifier.DespawnNotifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin implements IEntityWithComplexSpawn{
    @Shadow
    public int age;

    @Shadow
    public int lifespan;


    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
    public void modifyLife(EntityType<ItemEntity> entityType, Level level, CallbackInfo ci) {
        if (DespawnNotifier.isDev()) {
            //DEBUG
            this.lifespan = 1000;
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;DDD)V", at = @At("TAIL"))
    public void modifyLifeServer(Level level, double posX, double posY, double posZ, ItemStack itemStack, double deltaX, double deltaY, double deltaZ, CallbackInfo ci) {
        if (DespawnNotifier.isDev()) {
            //DEBUG
            this.lifespan = 1000;
        }
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.age);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.age = additionalData.readInt();
    }
}
