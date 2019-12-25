package ru.artfect.wynnlang.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import ru.artfect.wynnlang.Reference;
import ru.artfect.wynnlang.WynnLang;

public class RuCommand implements ICommand {
    @Override
    public String getName() {
        return "ru";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ru";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 0) {
            List<String> list = Arrays.asList(args);
            String message = String.join(" ", list);
            Reference.ruChat.sendMessage("m:" + message);
        } else {
        	Reference.ruChat.defaultChat = !Reference.ruChat.defaultChat;
            WynnLang.sendMessage("§rЧат по умолчанию " + (Reference.ruChat.defaultChat ? "§aвключен" : "§cвыключен") + "§r");
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return true;
    }

    @Override
    public List<String> getAliases() {
        List l = new ArrayList<String>();
        l.add("Ru");
        l.add("RU");
        return l;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return new ArrayList<String>();
    }
}