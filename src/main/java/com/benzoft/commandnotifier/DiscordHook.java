package com.benzoft.commandnotifier;

import com.benzoft.commandnotifier.persistence.ConfigFile;
import com.github.kevinsawicki.http.HttpRequest;
import org.bukkit.Bukkit;

public class DiscordHook {

    private final CommandNotifier commandNotifier;

    public DiscordHook(final CommandNotifier commandNotifier) {
        this.commandNotifier = commandNotifier;
    }

    public void sendCommand(final String command) {
        final String WebhookURL = ConfigFile.getInstance().getWebhookURL();
        if (WebhookURL.isEmpty()) return;
        Bukkit.getScheduler().runTaskAsynchronously(commandNotifier, () ->
                HttpRequest.post(WebhookURL)
                        .acceptJson()
                        .header("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11")
                        .send("{\"content\":\"" + command + "\"}").code());
    }
}
