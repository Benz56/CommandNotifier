package com.benzoft.commandnotifier;

import com.benzoft.commandnotifier.persistence.MessagesFile;
import com.benzoft.commandnotifier.persistence.persistenceobjects.LogEntry;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class LogContainer {

    private static final int PARTITION_SIZE = 8;
    private final List<List<LogEntry>> partitionedLog;
    private Player player;
    private int currentPage = 0;


    public LogContainer(final List<LogEntry> entries) {
        partitionedLog = Lists.partition(entries, PARTITION_SIZE);
    }

    public void showEntries(final int page) {
        try {
            final List<LogEntry> logEntries = partitionedLog.get(page);
            MessagesFile.getInstance().getLogEntryHeader().replaceAll("%page%", page + 1).replaceAll("%pages%", partitionedLog.size()).send(player);
            final int longestUsername = Collections.max(logEntries, Comparator.comparingInt(o -> o.getUsername().length())).getUsername().length();
            logEntries.forEach(logEntry -> MessagesFile.getInstance().getLogEntryFormat().replaceAll("%timestamp%", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(logEntry.getTimestamp())).replaceAll("%player%", String.format("%-" + longestUsername + "s", logEntry.getUsername())).replaceAll("%command%", logEntry.getExecutedCommand()).send(player));
            MessagesFile.getInstance().getLogEntryFooter().replaceAll("%page%", page + 1).replaceAll("%pages%", partitionedLog.size()).send(player);
            currentPage = page;
        } catch (final IndexOutOfBoundsException e) {
            MessagesFile.getInstance().getInvalidArguments().send(player);
        }
    }
}
