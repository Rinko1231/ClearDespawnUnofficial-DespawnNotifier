package com.rinko1231.despawnnotifier.mixin;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemEntity.class)
public class ItemEntityMixin implements IEntityWithComplexSpawn {
    @Shadow
    public int age;

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.age);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.age = additionalData.readInt();
    }
}
