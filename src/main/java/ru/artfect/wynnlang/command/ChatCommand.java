package ru.artfect.wynnlang.command;

import ru.artfect.wynnlang.Config;
import ru.artfect.wynnlang.Reference;
import ru.artfect.wynnlang.RuChat;
import ru.artfect.wynnlang.WynnLang;

import java.io.IOException;

/**
 * @author func 18.02.2020
 */
public class ChatCommand implements WynnCommand {

    @Override
    public void execute(String... args) {
        RU_CHAT.setEnabled(!RU_CHAT.isEnabled());
        if (RU_CHAT.isEnabled()) {
            Reference.ruChat = new RuChat();
            Reference.ruChat.start();
        } else {
            try {
                Reference.ruChat.closeSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        WynnLang.sendMessage("§rWynnLang чат " + (RU_CHAT.isEnabled() ? "§aвключен" : "§cвыключен") + "§r");
        Config.setBoolean("Chat", "Enabled", true, RU_CHAT.isEnabled());
    }
}
