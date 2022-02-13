package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

/**
 * 附魔基类
 */
public abstract class BaseEnchantment extends Enchantment {

    private static final Map<String, BaseEnchantment> ID_MAP = new HashMap<>();

    private static final Formatting[] RATE_COLOR = {Formatting.GRAY, Formatting.BLUE, Formatting.YELLOW, Formatting.GOLD};

    private final Identifier id;


    protected BaseEnchantment(String name, Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
        this.id = new Identifier(Enchant.ID, name);

        // Don't replace if exist
        if (Registry.ENCHANTMENT.containsId(id)) {
            return;
        }

        ID_MAP.put(id.toString(), Registry.register(Registry.ENCHANTMENT, id, this));
        BaseEnchantmentFactory.register(this);
    }

    public static boolean isBase(String id) {
        return id != null && id.contains(Enchant.ID);
    }

    public static <T extends BaseEnchantment> T get(Class<T> clazz) {
        return BaseEnchantmentFactory.getInstance(clazz);
    }

    @SuppressWarnings("all")
    public static <T extends BaseEnchantment> T get(String id) {
        return (T) ID_MAP.get(id);
    }

    @SuppressWarnings("all")
    public static <T extends BaseEnchantment> T get(Identifier id) {
        return (T) ID_MAP.get(id);
    }

    @SuppressWarnings("all")
    public static <T extends BaseEnchantment> T get(NbtCompound tag) {
        return (T) ID_MAP.get(EnchantmentHelper.getIdFromNbt(tag));
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public Text getName(int level) {
        return super.getName(level).shallowCopy().formatted(RATE_COLOR[getRarity().ordinal()]);
    }

    /**
     * It's final
     *
     * @see BaseEnchantment#getAttackDamage(net.minecraft.item.ItemStack, int, net.minecraft.entity.EntityGroup)
     */
    @Override
    public final float getAttackDamage(int level, EntityGroup group) {
        return super.getAttackDamage(level, group);
    }

    public float getAttackDamage(ItemStack item, int level, EntityGroup group) {
        return super.getAttackDamage(level, group);
    }

    public int level(ItemStack item) {
        return EnchantmentHelper.getLevel(this, item);
    }

    /**
     * Can regis to any event or other things
     */
    public void register() {
    }

    /**
     * Add enchantment trigger callback
     */
    public final void tryTrigger(LivingEntity living, ItemStack stack, int level) {
        // 20 tick == 1s
        if (living.age % (second() * 20) == 0) {
            livingTick(living, stack, level);
        }
    }

    /**
     * default 1s
     */
    protected int second() {
        return 1;
    }

    /**
     * enchantment on tick ending
     */
    protected void livingTick(LivingEntity living, ItemStack stack, int level) {
    }

    /**
     * Addition Damage on hit, is effect on armor
     * <p>
     * 1 -> amount + 1
     * 0.5 -> amount + 0.5
     */
    public float getAdditionDamage(LivingEntity attacker, LivingEntity target, ItemStack stack, int level) {
        return 0;
    }

    /**
     * Multi total Damage on hit, is effect on armor
     * <p>
     * 1 -> amount * (1 + 1)
     * 0.5 -> amount * (1 + 0.5)
     */
    public float getMultiTotalDamage(LivingEntity attacker, LivingEntity target, ItemStack stack, int level) {
        return 0;
    }

    /**
     * Addition Damage on hit, real damage, after armor effect
     * <p>
     * 1 -> amount + 1
     * 0.5 -> amount + 0.5
     */
    public float getRealAdditionDamage(LivingEntity attacker, LivingEntity target, ItemStack stack, int level) {
        return 0;
    }

    /**
     * Multi total Damage on hit, real damage, after armor effect
     * <p>
     * 1 -> amount * (1 + 1)
     * 0.5 -> amount * (1 + 0.5)
     */
    public float getRealMultiTotalDamage(LivingEntity attacker, LivingEntity target, ItemStack stack, int level) {
        return 0;
    }

    /**
     * damage callback
     */
    public void damageCallback(LivingEntity attacker, LivingEntity target, ItemStack stack, int level, float amount) {

    }

    @SuppressWarnings("all")
    private static class BaseEnchantmentFactory {

        private static final Map<Class<? extends BaseEnchantment>, BaseEnchantment> CACHE = new HashMap<>();

        public static <T extends BaseEnchantment> T getInstance(Class<T> clazz) {
            if (CACHE.containsKey(clazz)) {
                return (T) CACHE.get(clazz);
            }

            BaseEnchantment e = null;
            try {
                e = clazz.newInstance();
            } catch (Exception ignore) {
                Config.LOGGER.warn("error to load enchantment {}", clazz.getName());
            }

            return (T) e;
        }

        public static <T extends BaseEnchantment> void register(T t) {
            CACHE.put(t.getClass(), t);
        }
    }
}
