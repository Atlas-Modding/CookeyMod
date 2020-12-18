package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    // Default constructor to satisfy compiler :P
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
}
