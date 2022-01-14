package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected ItemStack activeItemStack;

    @Shadow
    protected int itemUseTimeLeft;

    private int haloTick;

    @Shadow
    public abstract Iterable<ItemStack> getArmorItems();

    @Shadow
    public abstract AttributeContainer getAttributes();

    @Shadow
    public abstract boolean isDead();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tickT(CallbackInfo ci) {
        EnchantUtil.removedDirtyHalo(getAttributes());
        if (Enchant.option.halo && age - haloTick >= Enchant.option.haloInterval) {
            haloTick = age;
            EnchantUtil.halo((LivingEntity) (Object) this);
        }
    }

    @Inject(method = "tickActiveItemStack", at = @At(value = "HEAD"), cancellable = true)
    private void tickActiveItemStackH(CallbackInfo ci) {
        if (Enchant.option.quickShoot && EnchantUtil.isServerPlayer((LivingEntity) (Object) this) && activeItemStack.getItem() instanceof RangedWeaponItem) {
            this.itemUseTimeLeft -= EnchantUtil.quickShooting(activeItemStack);
        }
    }

    @ModifyVariable(method = "applyDamage", at = @At(value = "HEAD"), argsOnly = true)
    private float returnAmount(float amount, DamageSource source) {
        Entity entity = source.getAttacker();
        if (Enchant.option.weakness && entity instanceof LivingEntity) {
            return EnchantUtil.weakness((LivingEntity) entity, amount);
        }

        return amount;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"), method = "applyDamage")
    private void applyDamageT(DamageSource source, float amount, CallbackInfo ci) {
        Entity entity = source.getAttacker();
        if (Enchant.option.suckBlood && entity instanceof LivingEntity) {
            EnchantUtil.suckBlood((LivingEntity) entity, amount, entity.getBoundingBox().expand(1.0D, 0.25D, 1.0D));
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "applyDamage")
    private void setHealthT(DamageSource source, float amount, CallbackInfo ci) {
        if (Enchant.option.rebirth && isDead()) {
            EnchantUtil.rebirth((LivingEntity) (Object) this);
        }
    }

    @Inject(method = "canHaveStatusEffect", at = @At("HEAD"), cancellable = true)
    private void canHaveStatusEffectH(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity e = (LivingEntity) (Object) this;
        if (Enchant.option.magicImmune && e instanceof ServerPlayerEntity && EnchantUtil.magicImmune((ServerPlayerEntity) e, effect)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
