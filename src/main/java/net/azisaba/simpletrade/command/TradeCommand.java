package net.azisaba.simpletrade.command;

import lombok.RequiredArgsConstructor;
import net.azisaba.simpletrade.SimpleTrade;
import net.azisaba.simpletrade.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public class TradeCommand implements CommandExecutor {

    private final SimpleTrade plugin;

    private final HashMap<UUID, Long> lastExecuted = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.f("{0}&cこのコマンドはプレイヤーのみ有効です！", SimpleTrade.getPrefix()));
            return true;
        }

        Player p = (Player) sender;//pにコマンドを打ったプレイヤーをぶち込んでる

        // コマンドのクールダウンに引っかかっている場合は無視
        if (System.currentTimeMillis() - lastExecuted.getOrDefault(p.getUniqueId(), 0L) < 3000L) {
            return true;
        }

        // 引数が足りない場合
        if (args.length <= 0) {
            // 使い方を表示してreturn
            p.sendMessage(Chat.f("{0}&c使い方: &e/{1} <Player>", SimpleTrade.getPrefix(), label));//{0}ってなんやねん
            return true;
        }

        // ターゲット取得
        Player target = Bukkit.getPlayerExact(args[0]);

        // プレイヤーが見つからない場合
        if (target == null) {
            // メッセージを表示してreturn
            p.sendMessage(Chat.f("{0}&7{1} &eというプレイヤーが見つかりません！", SimpleTrade.getPrefix(), args[0]));
            return true;
        }
        // 同じプレイヤーの場合
        if (p == target) {
            p.sendMessage(Chat.f("{0}&c自分自身にリクエストを送ることはできません！", SimpleTrade.getPrefix()));
            return true;
        }

        lastExecuted.put(p.getUniqueId(), System.currentTimeMillis());//クールタイムのための記録っぽい

        // リクエストを送信する
        plugin.getRequestCollector().sendRequest(p, target);//苦難を乗り越えて無事リクエストを発出
        return true;
    }
}
