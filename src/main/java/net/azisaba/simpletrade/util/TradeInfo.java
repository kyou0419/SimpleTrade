package net.azisaba.simpletrade.util;

import com.google.common.base.Strings;
import lombok.Getter;
import net.azisaba.simpletrade.SimpleTrade;
import net.azisaba.simpletrade.task.ConfirmTask;
import net.azisaba.simpletrade.utils.Chat;
import net.azisaba.simpletrade.utils.ItemDB;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TradeInfo {

    // 取引をしているプレイヤー
    private final List<Player> players;
    // 取引インベントリ
    private final HashMap<Player, Inventory> invMap = new HashMap<>();
    // Confirm
    private final HashMap<Player, Boolean> confirmMap = new HashMap<>();
    // Confirm Timer
    private final HashMap<Player, BukkitTask> confirmTaskMap = new HashMap<>();

    @Getter
    private boolean finished = false;

    public TradeInfo(Player player1, Player player2) {
        players = Arrays.asList(player1, player2);
    }

    /**
     * 取引を開始します。インベントリを作成してプレイヤーに開かせる処理が行われます。
     */
    public void startTrade() {
        // インベントリ作成
        Inventory forPlayer0 = Bukkit.createInventory(null, 9 * 6, Chat.f("You{0}{1}", Strings.repeat(" ", 15), players.get(1).getName()));//String.Repeatで空白を15個作った後に取引相手のプレイヤーネームを出してる　例 miyabi0333               miyabi0332
        Inventory forPlayer1 = Bukkit.createInventory(null, 9 * 6, Chat.f("You{0}{1}", Strings.repeat(" ", 15), players.get(0).getName()));

        // アイテムを追加
        initInventories(forPlayer0, forPlayer1);

        // インベントリを開く
        players.get(0).openInventory(forPlayer0);
        players.get(1).openInventory(forPlayer1);

        // Mapに追加
        invMap.put(players.get(0), forPlayer0);
        invMap.put(players.get(1), forPlayer1);
    }

    public void tradeItems() {
        HashMap<Player, List<ItemStack>> itemsMap = new HashMap<>();

        for (int i = 0; i <= 1; i++) {
            Player p = players.get(i);
            Inventory inv = invMap.get(players.get(0));
            if (i == 0)
                inv = invMap.get(players.get(1));

            List<ItemStack> items = getTradingItems(inv);
            if (!items.isEmpty()) {
                itemsMap.put(p, items);
            }

            // 空きスロットが足りない場合
            if (getEmptySlots(p) < itemsMap.getOrDefault(p, new ArrayList<>()).size()) {
                finished = true;
                // 空きが足りないのでアイテムを変換する
                giveBackItems();

                // メッセージを送信する
                p.sendMessage(Chat.f("{0}&cインベントリの空きが足りないため取引できませんでした！", SimpleTrade.getPrefix()));
                players.forEach(p2 -> {
                    if (p != p2) {
                        // メッセージを表示する
                        p2.sendMessage(Chat.f("{0}&c相手のインベントリに空きがありません!", SimpleTrade.getPrefix()));
                    }
                    p2.playSound(p2.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                });
                return;
            }
        }

        finished = true;

        for (Player player : players) {
            List<ItemStack> items = itemsMap.getOrDefault(player, new ArrayList<>());
            player.getInventory().addItem(items.toArray(new ItemStack[0]));
            player.closeInventory();
            player.sendMessage(Chat.f("{0}&a取引が完了しました！", SimpleTrade.getPrefix()));
            for (int i = 0; i < 3; i++)
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2, 1.5f);
        }
    }

    public boolean addItem(Player p, ItemStack item) {
        int setSlot = 0;
        Inventory inv = invMap.get(p);
        boolean set = false;
        for (int i = 0; i < 40; i++) {
            // しきりに到達した場合は折り返す
            if (i != 0 && (i + 5) % 9 == 0) {
                i += 4;
                continue;
            }

            // アイテム取得
            ItemStack slotItem = inv.getItem(i);
            // アイテムがnullの場合
            if (slotItem == null || slotItem.getType() == Material.AIR) {
                // アイテムをセット
                inv.setItem(i, item);
                setSlot = i;
                // 終了
                set = true;
                break;
            } else if (slotItem.isSimilar(item) && slotItem.getAmount() + item.getAmount() <= slotItem.getMaxStackSize()) { // スタックできるアイテムで、個数的にもスタック可能な場合
                slotItem.setAmount(slotItem.getAmount() + item.getAmount());
                setSlot = i;
                set = true;
                break;
            }
        }

        if (!set) {
            return false;
        }

        // 相手のインベントリにも適用する
        List<Inventory> inventories = new ArrayList<>(invMap.values());
        Inventory inv2 = inventories.get(0);
        if (inv.equals(inv2)) {
            inv2 = inventories.get(1);
        }
        // setSlotに5を足せば良い
        inv2.setItem(setSlot + 5, inv.getItem(setSlot));

        for (Player player : players) {
            confirm(player, false);
        }
        return true;
    }

    public void returnItem(Player p, int slot) {
        boolean isLeftSide = false;
        Inventory inv = getInventory(p);
        Inventory inv2 = new ArrayList<>(invMap.values()).get(0);
        if (inv == inv2) {
            inv2 = new ArrayList<>(invMap.values()).get(1);
        }

        for (int i = 0; i <= 4; i++) {
            if (ItemDB.getSeparateItem().isSimilar(inv.getItem(slot + i))) {
                isLeftSide = true;
                break;
            }
        }

        if (!isLeftSide) {
            return;
        }

        ItemStack item = inv.getItem(slot);
        p.getInventory().addItem(item);
        inv.setItem(slot, null);
        inv2.setItem(slot + 5, null);

        int before = slot;
        for (int i = slot + 1; i < 40; i++) {
            // しきりに到達した場合は折り返す
            if (i != 0 && (i + 5) % 9 == 0) {
                i += 4;
                continue;
            }

            // アイテム取得
            ItemStack slotItem = inv.getItem(i);
            inv.setItem(before, slotItem);
            inv2.setItem(before + 5, slotItem);
            inv.setItem(i, null);
            inv2.setItem(i + 5, null);
            before = i;
        }

        playSound();

        for (Player player : players) {
            confirm(player, false);
        }
    }

    public Inventory getInventory(Player p) {
        return invMap.getOrDefault(p, null);
    }

    public void toggleConfirm(Player p) {
        if (confirmMap.getOrDefault(p, false)) {
            confirm(p, false);
        } else {
            confirm(p, true);
        }
    }

    public void confirm(Player p, boolean value) {
        if (!players.contains(p)) {
            throw new IllegalArgumentException("This player is not trading on this TradeInfo.");
        }

        Inventory inv = invMap.getOrDefault(p, null);
        Inventory inv2 = invMap.get(players.get(0));
        if (inv == inv2) {
            inv2 = invMap.get(players.get(1));
        }

        // confirm状態にする
        confirmMap.put(p, value);

        if (value) {
            inv.setItem(48, ItemDB.getWaitConfirmItem());
            inv2.setItem(50, ItemDB.getConfirmedByOpponent());

            if (confirmMap.getOrDefault(players.get(0), false) && confirmMap.getOrDefault(players.get(1), false)) {
                tradeItems();
                return;
            }
        } else {
            inv2.setItem(50, ItemDB.getOpponentNotConfirmed());

            if (confirmTaskMap.containsKey(p))
                confirmTaskMap.get(p).cancel();
            confirmTaskMap.put(p, new ConfirmTask(invMap.get(p)).runTaskTimer(SimpleTrade.getPlugin(), 0, 20));
        }

        playSound();
    }

    public void playSound() {
        // 2人ともに音を鳴らす
        players.forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1));
    }

    public void giveBackItems() {
        // 各プレイヤーへの処理
        players.forEach(p -> {
            // インベントリが開かれていた場合は閉じる
            if (p.getOpenInventory().getTopInventory() == null) {
                return;
            }
            if (p.getOpenInventory().getTopInventory().equals(invMap.get(p))) {
                p.closeInventory();
            }

            // インベントリ取得
            Inventory inv = invMap.get(p);

            // アイテムを返す
            List<ItemStack> items = getTradingItems(inv);
            for (ItemStack item : items) {
                p.getInventory().addItem(item);
            }
        });
    }

    public void close() {
        // 既に閉じられている場合はreturn
        if (isFinished()) {
            return;
        }

        // 破棄
        finished = true;

        // アイテム返却
        giveBackItems();

        // 各プレイヤーへの処理
        players.forEach(p -> {
            // 音を鳴らしてメッセージを表示する
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 0.5f);
            p.sendMessage(Chat.f("{0}&c取引がキャンセルされました", SimpleTrade.getPrefix()));
        });

        // タスクを消す
        confirmTaskMap.values().forEach(BukkitTask::cancel);
    }

    private List<ItemStack> getTradingItems(Inventory inv) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            // しきりに到達した場合は折り返す
            if (i != 0 && (i + 5) % 9 == 0) {
                i += 4;
                continue;
            }

            // アイテム取得
            ItemStack slotItem = inv.getItem(i);
            // アイテムがnullの場合 continue
            if (slotItem == null || slotItem.getType() == Material.AIR) {
                continue;
            }

            // アイテム追加
            items.add(slotItem);
        }

        return items;
    }

    /**
     * 空のインベントリに必要なアイテムを追加します
     */
    private void initInventories(Inventory... inventories) {//...は可変長引数と呼ばれるものでなんでも送れるようにしている？
        if (inventories.length <= 0) {
            return;
        }//バグインベントリ対策　無視していい

        for (Inventory inv : inventories) {//inventoriesの要素分繰り返す
            for (int i = 0; i < inv.getSize(); i++) {
                if ((i + 5) % 9 == 0) {
                    inv.setItem(i, ItemDB.getSeparateItem());
                    i += 8;
                }//縦一列にセパレートアイテム(真ん中のステンドグラス)を設置する、1$10$にするにはforをそもそも削除してinv.setItem(置きたいところ, ItemDB.置くもの)
            }

            inv.setItem(48, ItemDB.getConfirmItem());
            inv.setItem(50, ItemDB.getOpponentNotConfirmed());
        }//これforいる？？？
    }

    private int getEmptySlots(Player p) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack item = p.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                count++;
            }
        }

        return count;//きっとこれは残り枠があるかないかの判定だ相手側の所を読み込んだりしないんだろうか？
    }
}
