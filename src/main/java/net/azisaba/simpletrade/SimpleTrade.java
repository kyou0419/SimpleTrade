package net.azisaba.simpletrade;

import lombok.Getter;
import net.azisaba.simpletrade.command.TradeAdminCommand;
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

    @Getter//getter setterを自動で作成する何か　これを付けるとclass名.変数名で値を取得できる。 lombok君がやってるっぽい？
    private static final String prefix = Chat.f("&6[&dTrade&6] &r");//chat.fはカラーコードをChatColorを使わないで表現する方法
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
        Bukkit.getPluginCommand("tradeadmin").setExecutor(new TradeAdminCommand(this));

        // Listenerの登録
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(this), this);

        Bukkit.getLogger().info(getName() + " enabled.");//SimpleTrade enabledってコンソールで表示されるだけ
    }

    @Override
    public void onDisable() {
        // 取引中なら中断する
        tradeInfoContainer.onDisable();//どういうことなの。。

        Bukkit.getLogger().info(getName() + " disabled");//SimpleTrade disabled!
    }
}
