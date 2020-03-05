package net.azisaba.simpletrade.command;

import lombok.RequiredArgsConstructor;
import net.azisaba.simpletrade.SimpleTrade;
import net.azisaba.simpletrade.utils.Args;
import net.azisaba.simpletrade.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class TradeAdminCommand implements CommandExecutor {

    private final SimpleTrade plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length <= 0) {
            sender.sendMessage(Chat.f("{0}&c使い方: {1}", SimpleTrade.getPrefix(), cmd.getUsage().replace("<label>", label)));
            return true;
        }

        if (Args.check(args, 0, "info")) {
            sender.sendMessage(Chat.f("{0}&e現在トレードを行っているプレイヤー数: &a{1}人", SimpleTrade.getPrefix(), plugin.getTradeInfoContainer().getTrades()));
            return true;
        }

        sender.sendMessage(Chat.f("{0}&c使い方: {1}", SimpleTrade.getPrefix(), cmd.getUsage().replace("<label>", label)));
        return true;
    }
}
