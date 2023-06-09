package net.azisaba.simpletrade.util;

import lombok.RequiredArgsConstructor;
import me.rayzr522.jsonmessage.JSONMessage;
import net.azisaba.simpletrade.SimpleTrade;
import net.azisaba.simpletrade.task.RequestExpireTask;
import net.azisaba.simpletrade.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * トレードリクエストを集め、処理するクラス
 *
 * @author siloneco
 */
@RequiredArgsConstructor
public class TradeRequestCollector {

    private final SimpleTrade plugin;

    // リクエストを入れておくHashMap
    private final HashMap<UUID, List<UUID>> requests = new HashMap<>();
    // リクエストを期限切れにするタスクのMap
    private final HashMap<UUID, HashMap<UUID, BukkitTask>> expireTaskMap = new HashMap<>();

    /**
     * リクエストを送信します。すでに相手からリクエストされていた場合、即座に取引が開始されます
     *
     * @param from リクエストを送ったプレイヤー
     * @param to   リクエストを受け取るプレイヤー
     */
    public void sendRequest(@NotNull Player from, @NotNull Player to) {
        // 既にリクエストを送っている場合メッセージを出す
        if (requests.getOrDefault(from.getUniqueId(), new ArrayList<>()).contains(to.getUniqueId())) {
            from.sendMessage(Chat.f("{0}&cこのプレイヤーには既にリクエストを送信しています！", SimpleTrade.getPrefix()));
            return;
        }
        // リクエストを送られていた場合は取引を開始する
        if (requests.getOrDefault(to.getUniqueId(), new ArrayList<>()).contains(from.getUniqueId())) {
            plugin.getTradeInfoContainer().create(from, to);

            // リクエストを初期化する
            List<UUID> uuidList = requests.getOrDefault(to.getUniqueId(), new ArrayList<>());
            if (uuidList.contains(from.getUniqueId())) {
                uuidList.remove(from.getUniqueId());
                requests.put(to.getUniqueId(), uuidList);
            }

            HashMap<UUID, BukkitTask> taskMap = expireTaskMap.getOrDefault(to.getUniqueId(), new HashMap<>());
            if (taskMap.containsKey(from.getUniqueId())) {
                taskMap.get(from.getUniqueId()).cancel();
            }
            return;
        }

        // リクエストに追加
        List<UUID> uuidList = requests.getOrDefault(from.getUniqueId(), new ArrayList<>());
        uuidList.add(to.getUniqueId());
        requests.put(from.getUniqueId(), uuidList);

        // メッセージを送信
        from.sendMessage(Chat.f("{0}&aトレードリクエストを送信しました！", SimpleTrade.getPrefix()));
        JSONMessage.create()
                .then(Chat.f("{0}&b{1} &eさんからのTradeリクエスト: ", SimpleTrade.getPrefix(), from.getName()))
                .then(Chat.f("&a[承認]"))
                .runCommand(Chat.f("/trade {0}", from.getName()))
                .send(to);

        // タスク実行
        BukkitTask task = new RequestExpireTask(SimpleTrade.getPlugin(), from.getUniqueId(), to.getUniqueId(), from.getName(), to.getName())
                .runTaskLater(SimpleTrade.getPlugin(), 400L);
        HashMap<UUID, BukkitTask> taskMap = expireTaskMap.getOrDefault(from.getUniqueId(), new HashMap<>());
        if (taskMap.containsKey(to.getUniqueId())) {
            taskMap.get(to.getUniqueId()).cancel();//これでインベントリの機能実装してるのではないか
        }
        taskMap.put(to.getUniqueId(), task);
        expireTaskMap.put(from.getUniqueId(), taskMap);
    }

    public void expire(UUID from, UUID to, String fromName, String toName) {
        if (!requests.containsKey(from)) {
            return;
        }
        List<UUID> uuidList = requests.get(from);
        if (!uuidList.contains(to)) {
            return;
        }
        uuidList.remove(to);
        requests.put(from, uuidList);

        // 期限切れのメッセージを送る
        Player pFrom = Bukkit.getPlayer(from);
        if (pFrom != null) {
            pFrom.sendMessage(Chat.f("{0}&b{1} &cへのTrade招待は期限切れになりました", SimpleTrade.getPrefix(), toName));
        }
        // 期限切れのメッセージを送る
        Player pTo = Bukkit.getPlayer(to);
        if (pTo != null) {
            pTo.sendMessage(Chat.f("{0}&b{1} &cからのTrade招待が期限切れになりました", SimpleTrade.getPrefix(), fromName));
        }
    }
}
