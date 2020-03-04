package net.azisaba.simpletrade.utils;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemDB {

    @Getter
    private static ItemStack separateItem = null;
    @Getter
    private static ItemStack confirmWarningItem = null;
    @Getter
    private static ItemStack confirmItem = null;
    @Getter
    private static ItemStack waitConfirmItem = null;
    @Getter
    private static ItemStack opponentNotConfirmed = null;
    @Getter
    private static ItemStack confirmedByOpponent = null;

    public static void init() {
        separateItem = ItemHelper.createItem(Material.STAINED_GLASS_PANE, 15, "", Chat.f("&7⇐あなたのアイテム"), Chat.f("&7相手のアイテム⇒"));
        confirmWarningItem = ItemHelper.createItem(Material.STAINED_CLAY, 6, Chat.f("&e最終確認をしてください！"), Chat.f("&7お互いが承認しあった場合この操作は&c取り消せません&7！"));
        confirmItem = ItemHelper.createItem(Material.STAINED_CLAY, 4, Chat.f("&e取引を完了する"), Chat.f("&eクリックで相手のアイテムを承認します"));
        waitConfirmItem = ItemHelper.createItem(Material.STAINED_CLAY, 5, Chat.f("&a相手のアイテムを承認中"), Chat.f("&e相手の承認を待っています"));
        confirmedByOpponent = ItemHelper.createItem(Material.STAINED_CLAY, 5, Chat.f("&a相手から承認されています！"), Chat.f("&e承認を押して取引を完了します"));
        opponentNotConfirmed = ItemHelper.createItem(Material.STAINED_CLAY, 4, Chat.f("&e承認されていません！"));
    }
}
