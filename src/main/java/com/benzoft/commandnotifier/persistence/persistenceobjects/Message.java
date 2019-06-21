package com.benzoft.commandnotifier.persistence.persistenceobjects;

import com.benzoft.commandnotifier.utils.MessageUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Message {

    private final List<String> messages = new ArrayList<>();

    public Message(final Object message) {
        messages.add((String) message);
    }

    private Message(final List<String> messages) {
        this.messages.addAll(messages);
    }

    public Message replaceAll(final String regex, final Object value) {
        return new Message(new ArrayList<>(messages).stream().map(entry -> entry.replaceAll(regex, String.valueOf(value))).collect(Collectors.toList()));
    }

    public void send() {
        send(null);
    }

    public void send(final Player player) {
        messages.forEach(entry -> MessageUtil.send(player, entry));
    }

    @Override
    public String toString() {
        return String.join("\n", messages);
    }
}
