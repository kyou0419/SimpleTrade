package net.azisaba.simpletrade.task;

import lombok.RequiredArgsConstructor;
import net.azisaba.simpletrade.SimpleTrade;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@RequiredArgsConstructor
public class RequestExpireTask extends BukkitRunnable {

    private final SimpleTrade plugin;

    private final UUID from;
    private final UUID to;

    private final String fromName;
    private final String toName;

    @Override
    public void run() {
        plugin.getRequestCollector().expire(from, to, fromName, toName);
    }
}
