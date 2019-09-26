package com.benzoft.commandnotifier;

import com.benzoft.commandnotifier.persistence.ConfigFile;
import org.bukkit.Bukkit;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordHook {

    private final CommandNotifier commandNotifier;

    DiscordHook(final CommandNotifier commandNotifier) {
        this.commandNotifier = commandNotifier;
    }

    public void sendCommand(final String command) {
        final String webhookURL = ConfigFile.getInstance().getWebhookURL();
        if (webhookURL.isEmpty()) return;

        Bukkit.getScheduler().runTaskAsynchronously(commandNotifier, () -> {
            try {
                final HttpsURLConnection connection = (HttpsURLConnection) new URL(webhookURL).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
                connection.setDoOutput(true);
                try (final OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(("{\"content\":\"" + command + "\"}").getBytes(StandardCharsets.UTF_8));
                }
                connection.getInputStream();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });
    }
}
