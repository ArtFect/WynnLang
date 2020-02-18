package ru.artfect.wynnlang.command;

import ru.artfect.wynnlang.WynnLang;

/**
 * @author func 18.02.2020
 */
public class MuteCommand implements WynnCommand {
    @Override
    public void execute(String... args) {
        if (args.length == 2)
            RU_CHAT.mutePlayer(args[1]);
        else
            WynnLang.sendMessage("§cУкажите от какого игрока вы не хотите получать сообщения");
    }
}
