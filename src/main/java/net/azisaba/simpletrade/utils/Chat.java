package net.azisaba.simpletrade.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.text.MessageFormat;

@UtilityClass
public class Chat {

    // メッセージをフォーマットして、&で色をつける
    public String f(String text, Object... args) {
        return MessageFormat.format(ChatColor.translateAlternateColorCodes('&', text), args);
    }

    // 色を消す
    public String r(String text) {
        return ChatColor.stripColor(text);
    }
}