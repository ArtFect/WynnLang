package ru.artfect.wynnlang.command;

import lombok.Getter;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import ru.artfect.wynnlang.Reference;
import ru.artfect.wynnlang.WynnLang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class RuCommand implements ICommand {

    private String name = "ru";
    private String usage = "/ru";

    @Override
    public String getUsage(ICommandSender sender) {
        return usage;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length != 0)
            Reference.ruChat.sendMessage("m:" + String.join(" ", Arrays.asList(args)));
        else {
            WynnCommand.RU_CHAT.setDefaultChat(!WynnCommand.RU_CHAT.isDefaultChat());
            WynnLang.sendMessage("§rЧат по умолчанию " + (WynnCommand.RU_CHAT.isDefaultChat() ? "§aвключен" : "§cвыключен") + "§r");
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public int compareTo(ICommand command) {
        return 0;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("Ru", "RU");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return new ArrayList<>();
    }
}