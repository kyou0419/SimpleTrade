package net.azisaba.simpletrade.listener;

import lombok.RequiredArgsConstructor;
import net.azisaba.simpletrade.SimpleTrade;
import net.azisaba.simpletrade.util.TradeInfo;
import net.azisaba.simpletrade.utils.Chat;
import net.azisaba.simpletrade.utils.ItemDB;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class InventoryClickListener implements Listener {

    private final SimpleTrade plugin;

    @EventHandler
    public void onClickInventory(InventoryClickEvent e) {
        // プレイヤーではない場合return
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();

        // トレードを行っていない場合はreturn
        TradeInfo info = plugin.getTradeInfoContainer().getTradeInfo(p);
        if (info == null) {
            return;
        }

        // 取引用のインベントリを開いていない場合はreturn
        if (!isSameInventory(info.getInventory(p), p.getOpenInventory().getTopInventory())) {
            return;
        }

        // アイテムをクリックしていない場合はreturn
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        // イベントをキャンセルする
        e.setCancelled(true);

        // クリックしたインベントリがプレイヤーのインベントリの場合
        if (e.getClickedInventory() == p.getInventory()) {
            // アイテムを追加する
            ItemStack addItem = e.getCurrentItem().clone();
            if (e.getAction() == InventoryAction.PICKUP_HALF) {
                addItem.setAmount(1);
            }
            boolean success = info.addItem(p, addItem);
            // 失敗していたらreturn
            if (!success) {
                p.sendMessage(Chat.f("&cアイテムがいっぱいです！"));
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 0.5f);
                return;
            }
            // クリックしたアイテムを削除
            if (e.getAction() == InventoryAction.PICKUP_HALF) {
                e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
            } else {
                p.getInventory().setItem(e.getSlot(), null);
            }

            // 音を鳴らす
            info.playSound();

            // クリックしたインベントリが取引用インベントリの場合
        } else if (e.getClickedInventory().getName().equals(info.getInventory(p).getName())) {
            // スロットの取得
            int clickedSlot = e.getSlot();
            // スロットが最後の1列の場合
            if (e.getClickedInventory().getSize() - 9 <= clickedSlot) {
                // クリックしたアイテムがConfirm用アイテムだった場合
                if (ItemDB.getConfirmItem().equals(e.getCurrentItem())
                        || ItemDB.getWaitConfirmItem().equals(e.getCurrentItem())) {
                    info.toggleConfirm(p);
                    return;
                }
                return;
            }
            // 左側ではない場合return
            for (int i = 1; i <= 4; i++) {
                if (ItemDB.getSeparateItem().isSimilar(info.getInventory(p).getItem(clickedSlot + i))) {
                    // アイテムを戻す
                    info.returnItem(p, clickedSlot);
                    break;
                }
            }
        }
    }

    private boolean isSameInventory(Inventory inv1, Inventory inv2) {
        if (inv1 == null || inv2 == null) {
            return false;
        }
        if (inv1.getSize() != inv2.getSize()) {
            return false;
        }
        return inv1.getTitle().equals(inv2.getTitle());
    }
}
