package ru.artfect.wynnlang.command;

import ru.artfect.wynnlang.Config;
import ru.artfect.wynnlang.Log;
import ru.artfect.wynnlang.WynnLang;

/**
 * @author func 18.02.2020
 */
public class LogCommand implements WynnCommand {
    @Override
    public void execute(String... args) {
        Log.enabled = !Log.enabled;
        WynnLang.sendMessage("§rОтправка " + (Log.enabled ? "§aвключена" : "§cвыключена") + "§r");
        Config.setBoolean("Options", "Logging", true, Log.enabled);
    }
}
