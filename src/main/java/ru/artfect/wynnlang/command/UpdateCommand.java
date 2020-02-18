package ru.artfect.wynnlang.command;

import ru.artfect.wynnlang.UpdateManager;

/**
 * @author func 18.02.2020
 */
public class UpdateCommand implements WynnCommand {

    private UpdateManager updateManager = new UpdateManager();

    @Override
    public void execute(String... args) {
        updateManager.update();
    }
}
