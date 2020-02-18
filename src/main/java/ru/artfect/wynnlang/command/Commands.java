package ru.artfect.wynnlang.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author func 18.02.2020
 */
@AllArgsConstructor
@Getter
public enum Commands {

    CHAT("chat", new ChatCommand()),
    INFO("info", new InfoCommand()),
    LOG("log", new LogCommand()),
    MUTE("mute", new MuteCommand()),
    TOGGLE("toggle", new ToggleCommand()),
    UPDATE("update", new UpdateCommand()),;

    private String key;
    private WynnCommand command;
}
