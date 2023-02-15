package de.fanta.unbreakingfix;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;

public final class UnbreakingFix extends JavaPlugin implements Listener {

    private static Collection<Material> itemList;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        itemList = new ArrayList<>();

        for (String itemString : getConfig().getStringList("ItemList")) {
            Material item = Material.matchMaterial(itemString);
            if (item == null) {
                getLogger().severe(itemString + " could not be assigned to Material.");
                continue;
            }
            itemList.add(item);
        }

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent e) {
        if (e.getDamage() > 0 && itemList.contains(e.getItem().getType()) && e.getItem().containsEnchantment(Enchantment.DURABILITY)) {
            int unbreakingLevel = e.getItem().getEnchantmentLevel(Enchantment.DURABILITY);
            if (unbreakingLevel >= 1) {
                double chance = calculateDamage(unbreakingLevel);
                int damage = e.getDamage();

                for (int i = 0; i < e.getDamage(); ++i) {
                    if (Math.random() >= chance) {
                        --damage;
                    }
                }
                e.setDamage(damage);
            }
        }
    }

    private double calculateDamage(int level) {
        return 5.0D / (3.0D * level + 5.0D);
    }

}
