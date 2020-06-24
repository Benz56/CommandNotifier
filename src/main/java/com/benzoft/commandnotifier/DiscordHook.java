package com.benzoft.commandnotifier;

import com.benzoft.commandnotifier.persistence.ConfigFile;
import com.benzoft.commandnotifier.tasks.AsyncTask;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordHook {

    public void sendCommand(final String command) {
        final String webhookURL = ConfigFile.getInstance().getWebhookURL();
        if (!webhookURL.isEmpty()) {
            AsyncTask.supplyAsync(() -> {
                try {
                    final HttpsURLConnection connection = (HttpsURLConnection) new URL(webhookURL).openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
                    connection.setDoOutput(true);
                    try (final OutputStream outputStream = connection.getOutputStream()) {
                        // Handle backslashes.
                        String preparedCommand = command.replaceAll("\\\\", "");
                        if (preparedCommand.endsWith(" *")) preparedCommand = preparedCommand.substring(0, preparedCommand.length() - 2) + "*";

                        outputStream.write(("{\"content\":\"" + preparedCommand + "\"}").getBytes(StandardCharsets.UTF_8));
                    }
                    connection.getInputStream();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }).complete();
        }
    }
}
