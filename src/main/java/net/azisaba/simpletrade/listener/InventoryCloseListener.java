package net.azisaba.simpletrade.listener;

import lombok.RequiredArgsConstructor;
import net.azisaba.simpletrade.SimpleTrade;
import net.azisaba.simpletrade.util.TradeInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

@RequiredArgsConstructor
public class InventoryCloseListener implements Listener {

    private final SimpleTrade plugin;

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {
        // プレイヤーではない場合return
        if (!(e.getPlayer() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getPlayer();

        // 取引中のプレイヤーだった場合は取引をキャンセルさせる
        TradeInfo info = plugin.getTradeInfoContainer().getTradeInfo(p);
        if (info != null) {
            info.close();
        }
    }
}
