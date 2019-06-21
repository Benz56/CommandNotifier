package com.benzoft.commandnotifier.runtimedata;


import com.benzoft.commandnotifier.LogContainer;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PlayerData {

    private final UUID uniqueId;
    private LogContainer logContainer;
    private int currentLogPage;

    PlayerData(final UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}
