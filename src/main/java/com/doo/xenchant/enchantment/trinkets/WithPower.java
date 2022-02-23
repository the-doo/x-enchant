package com.doo.xenchant.enchantment.trinkets;

import com.doo.xenchant.events.EntityDamageApi;

import java.util.Optional;

/**
 * With Power
 */
public class WithPower extends Trinkets {

    public static final String NAME = "with_power";

    public WithPower() {
        super(NAME);
    }

    @Override
    public void register() {
        super.register();

        EntityDamageApi.MULTIPLIER.register(((attacker, target, map) -> {
            if (!map.containsKey(this)) {
                return 0;
            }

            return Optional.ofNullable(map.get(this)).orElse(0) * .2F;
        }));
    }
}
