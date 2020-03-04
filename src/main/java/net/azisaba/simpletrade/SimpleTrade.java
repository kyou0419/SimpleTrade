package net.azisaba.simpletrade;

import lombok.Getter;
import net.azisaba.simpletrade.command.TradeCommand;
import net.azisaba.simpletrade.listener.InventoryClickListener;
import net.azisaba.simpletrade.listener.InventoryCloseListener;
import net.azisaba.simpletrade.util.TradeInfoContainer;
import net.azisaba.simpletrade.util.TradeRequestCollector;
import net.azisaba.simpletrade.utils.Chat;
import net.azisaba.simpletrade.utils.ItemDB;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleTrade extends JavaPlugin {

    @Getter
    private static final String prefix = Chat.f("&6[&dTrade&6] &r");
    @Getter
    private static SimpleTrade plugin;
    @Getter
    private TradeInfoContainer tradeInfoContainer;
    @Getter
    private TradeRequestCollector requestCollector;

    @Override
    public void onEnable() {
        SimpleTrade.plugin = this;

        tradeInfoContainer = new TradeInfoContainer(this);
        requestCollector = new TradeRequestCollector(this);

        ItemDB.init();

        // コマンドの登録
        Bukkit.getPluginCommand("trade").setExecutor(new TradeCommand(this));

        // Listenerの登録
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(this), this);

        Bukkit.getLogger().info(getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        // 取引中なら中断する
        tradeInfoContainer.onDisable();

        Bukkit.getLogger().info(getName() + " disabled");
    }
}
