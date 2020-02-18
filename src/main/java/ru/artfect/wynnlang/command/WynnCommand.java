package ru.artfect.wynnlang.command;

import ru.artfect.wynnlang.RuChat;

/**
 * @author func 18.02.2020
 */
public interface WynnCommand {

    RuChat RU_CHAT = new RuChat();

    void execute(String... args);
}
