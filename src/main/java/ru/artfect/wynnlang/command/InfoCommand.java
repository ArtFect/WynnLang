package ru.artfect.wynnlang.command;

import ru.artfect.wynnlang.Reference;
import ru.artfect.wynnlang.WynnLang;

/**
 * @author func 18.02.2020
 */
public class InfoCommand implements WynnCommand {
    @Override
    public void execute(String... args) {
        WynnLang.sendMessage("" +
                "Инфо: \n" +
                (Reference.ruChat.isAlive() ? "Связь с сервером чата §aустановлена\n" : "Связь с сервером чата §cотсутствует\n") +
                ("§rКоличество онлайн игроков: §6" + (Reference.ruChat.isAlive() ? String.valueOf(RU_CHAT.isEnabled()) : "неизвестно"))
        );
    }
}
