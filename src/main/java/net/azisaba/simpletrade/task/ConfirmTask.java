package net.azisaba.simpletrade.task;

import lombok.RequiredArgsConstructor;
import net.azisaba.simpletrade.utils.ItemDB;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class ConfirmTask extends BukkitRunnable {

    private final Inventory inv;
    private int count = 5;

    @Override
    public void run() {
        ItemStack item = ItemDB.getConfirmWarningItem();
        item.setAmount(count);
        inv.setItem(48, item);

        if (count <= 0) {
            inv.setItem(48, ItemDB.getConfirmItem());
            this.cancel();
            return;
        }
        count--;
    }
}
