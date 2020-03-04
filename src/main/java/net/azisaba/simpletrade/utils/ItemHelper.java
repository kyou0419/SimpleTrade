package net.azisaba.simpletrade.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author siloneco forked from amata1219 version: 1.0.0
 */
public class ItemHelper {

    public static ItemStack create(Material type) {
        return new ItemStack(type);
    }

    public static ItemStack create(Material type, String title, String... lore) {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(title);
        if (lore.length > 0)
            meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, int data, String displayName, String... lore) {
        ItemStack item = new ItemStack(material, data);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(displayName != null ? displayName : "");

        if (lore == null || lore.length == 0)
            meta.setLore(new ArrayList<>());
        else
            meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);
        return item;
    }

    public static void addHideEnchant(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }
}