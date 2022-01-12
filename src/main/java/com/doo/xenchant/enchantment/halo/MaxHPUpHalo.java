package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

/**
 * 最大生命值提升光环
 */
public class MaxHPUpHalo extends HaloEnchantment {

    public static final String NAME = "max_hp_up";

    public MaxHPUpHalo() {
        super(NAME, true);
        ATTRIBUTES.add(EntityAttributes.GENERIC_MAX_HEALTH);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    protected boolean needTick() {
        return Enchant.option.maxHPHalo;
    }

    @Override
    public void onTarget(PlayerEntity player, Integer level, List<LivingEntity> targets) {
        StatusEffectInstance instance = new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 25, level);
        targets.forEach(e -> e.addStatusEffect(instance));
    }
}
