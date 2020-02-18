package ru.artfect.wynnlang.command;

import ru.artfect.wynnlang.Config;
import ru.artfect.wynnlang.Reference;
import ru.artfect.wynnlang.WynnLang;

/**
 * @author func 18.02.2020
 */
public class ToggleCommand implements WynnCommand {
    @Override
    public void execute(String... args) {
        Reference.modEnabled = !Reference.modEnabled;
        WynnLang.sendMessage("§rМод " + (Reference.modEnabled ? "§aвключен" : "§cвыключен") + "§r");
        Reference.reverseTranslation.reverse();
        Config.setBoolean("Options", "Enabled", true, Reference.modEnabled);
    }
}
