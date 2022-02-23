package com.doo.xenchant.enchantment;

import com.doo.xenchant.mixin.interfaces.ServerLivingApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;

/**
 * Climber
 */
public class Climber extends BaseEnchantment {

    public static final String NAME = "climber";

    public Climber() {
        super(NAME, Rarity.COMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public void register() {
        super.register();

        ServerLivingApi.TAIL_TICK.register(living -> {
            if (living.getY() < 80 || living.age % SECOND != 0) {
                return;
            }

            ItemStack feet = living.getEquippedStack(EquipmentSlot.FEET);
            if (feet.isEmpty() || level(feet) < 1) {
                return;
            }

            living.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, (int) (SECOND * 1.5), 2));
        });
    }
}
