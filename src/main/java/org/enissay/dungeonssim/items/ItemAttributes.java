package org.enissay.dungeonssim.items;

import com.google.common.collect.Maps;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public interface ItemAttributes {

    default Map<String, String> getAttributes() {
        return Maps.newHashMap();
    }

    default double doAbility(final Player player, final Action action, final double damage, final PlayerInteractEvent event) {
        return 0;
    }

    default Map<Enchantment, Integer> getEnchantments() {
        return null;
    }

    default PotionEffectType getPotionEffect() {
        return null;
    }

    default String getCustomHead() {
        return null;
    }
}
