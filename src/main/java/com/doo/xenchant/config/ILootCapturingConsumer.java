package com.doo.xenchant.config;

import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Function;

public interface ILootCapturingConsumer {
    void release(final Function<List<ItemStack>, List<ItemStack>> captureModifier);

}