package com.benzoft.commandnotifier.persistence.persistenceobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class LogEntry {

    private final Timestamp timestamp;
    private final String username;
    private final String executedCommand;
}
