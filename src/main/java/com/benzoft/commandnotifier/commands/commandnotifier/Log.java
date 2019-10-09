package com.benzoft.commandnotifier.commands.commandnotifier;

import com.benzoft.commandnotifier.CommandNotifier;
import com.benzoft.commandnotifier.LogContainer;
import com.benzoft.commandnotifier.PluginPermission;
import com.benzoft.commandnotifier.commands.AbstractSubCommand;
import com.benzoft.commandnotifier.persistence.MessagesFile;
import com.benzoft.commandnotifier.runtimedata.PlayerData;
import com.benzoft.commandnotifier.runtimedata.PlayerDataManager;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Log extends AbstractSubCommand {

    private static final Pattern UNIT_PATTERN = Pattern.compile("(seconds?|secs?|s|minutes?|mins?|m|hours?|h|days?|d)");
    private static final Pattern TIME_UNIT_PATTERN = Pattern.compile("^(\\d+" + UNIT_PATTERN + ")");
    private final CommandNotifier commandNotifier;
    private LogContainer consoleLogContainer;

    Log(final CommandNotifier commandNotifier, final String commandName, final PluginPermission permission, final boolean playerOnly, final String... aliases) {
        super(commandName, permission, playerOnly, aliases);
        this.commandNotifier = commandNotifier;
    }

    @Override
    public void onCommand(final Player player, final String[] args) {
        if (args.length < 2) {
            MessagesFile.getInstance().getInvalidArguments().send(player);
            return;
        }

        if (args.length == 3 && (Arrays.asList("page", "p").contains(args[1].toLowerCase()))) {
            try {
                final LogContainer logContainer = player == null ? consoleLogContainer : PlayerDataManager.getPlayerData(player, true).getLogContainer();
                if (Arrays.asList("next", "n").contains(args[2].toLowerCase())) {
                    logContainer.showEntries(logContainer.getCurrentPage() + 1);
                } else if (Arrays.asList("previous", "prev", "p").contains(args[2].toLowerCase())) {
                    logContainer.showEntries(logContainer.getCurrentPage() - 1);
                } else {
                    logContainer.showEntries(Integer.parseInt(args[2]) - 1);
                }
            } catch (final NumberFormatException | NullPointerException e) {
                MessagesFile.getInstance().getInvalidArguments().send(player);
            }
        } else {
            long specifiedTime = 0;
            boolean error = false;
            for (final String input : Arrays.copyOfRange(args, 1, args.length)) {
                if (TIME_UNIT_PATTERN.matcher(input).matches()) {
                    final int amount = Integer.parseInt(input.split(UNIT_PATTERN.toString())[0]);
                    specifiedTime += LogTimeUnit.stringToLogTimeUnit(input.substring(String.valueOf(amount).length())).getAsMillis(amount);
                } else error = true;
            }
            if (error) {
                MessagesFile.getInstance().getInvalidArguments().send(player);
                return;
            }
            commandNotifier.getLogDatabase().retrieveLogs(System.currentTimeMillis() - specifiedTime).whenComplete(logContainer -> {
                if (logContainer.getPartitionedLog().isEmpty()) {
                    MessagesFile.getInstance().getNoCommandsInSpecifiedTimespan().send(player);
                    return;
                }
                if (player != null) {
                    PlayerDataManager.getPlayerData(player, true).setLogContainer(logContainer);
                } else consoleLogContainer = logContainer;
                logContainer.setPlayer(player);
                logContainer.showEntries(0);
            });
        }
    }

    @Override
    public List<String> onTabComplete(final Player player, final String[] args) {
        final List<String> timeUnitSuggestions = new ArrayList<>(Arrays.asList("1day", "1hour", "1min", "1sec"));
        if (args.length == 1) {
            final PlayerData playerData = PlayerDataManager.getPlayerData(player, false);
            if (playerData != null && playerData.getLogContainer() != null) timeUnitSuggestions.addAll(Arrays.asList("page", "p"));
            return timeUnitSuggestions;
        } else if (args.length == 2 && Arrays.asList("page", "p").contains(args[0].toLowerCase())) {
            final PlayerData playerData = PlayerDataManager.getPlayerData(player, false);
            if (playerData != null && playerData.getLogContainer() != null)
                return IntStream.range(1, playerData.getLogContainer().getPartitionedLog().isEmpty() ? 2 : playerData.getLogContainer().getPartitionedLog().size() + 1).mapToObj(String::valueOf).collect(Collectors.toList());
        }
        return timeUnitSuggestions;
    }

    @Getter
    private enum LogTimeUnit {
        SECONDS(TimeUnit.SECONDS, "seconds", "second", "secs", "sec", "s"),
        MINUTES(TimeUnit.MINUTES, "minutes", "minute", "mins", "min", "m"),
        HOURS(TimeUnit.HOURS, "hours", "hour", "h"),
        DAYS(TimeUnit.DAYS, "days", "day", "d");

        private final TimeUnit timeUnit;
        private final Set<String> names;

        LogTimeUnit(final TimeUnit timeUnit, final String... names) {
            this.timeUnit = timeUnit;
            this.names = new HashSet<>(Arrays.asList(names));
        }

        private static LogTimeUnit stringToLogTimeUnit(final String input) {
            return Stream.of(values()).filter(logTimeUnit -> logTimeUnit.names.contains(input.toLowerCase())).findFirst().orElse(null);
        }

        public long getAsMillis(final int amount) {
            return timeUnit.toMillis(amount);
        }
    }
}
