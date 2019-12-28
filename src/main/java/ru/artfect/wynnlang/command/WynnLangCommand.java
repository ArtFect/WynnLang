package ru.artfect.wynnlang.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import ru.artfect.wynnlang.*;
import ru.artfect.wynnlang.translate.ReverseTranslation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WynnLangCommand implements ICommand {
    private UpdateManager updateManger;

    public WynnLangCommand(UpdateManager updateManager) {
        this.updateManger = updateManager;
    }

    @Override
    public String getName() {
        return "WynnLang";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/WynnLang";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 0) {
            switch (args[0]) {
                case "toggle":
                    Reference.modEnabled = !Reference.modEnabled;
                    WynnLang.sendMessage("§rМод " + (Reference.modEnabled ? "§aвключен" : "§cвыключен") + "§r");
                    ReverseTranslation.reverse();
                    Config.setBoolean("Options", "Enabled", true, Reference.modEnabled);
                    break;
                case "log":
                    Log.enabled = !Log.enabled;
                    WynnLang.sendMessage("§rОтправка " + (Log.enabled ? "§aвключена" : "§cвыключена") + "§r");
                    Config.setBoolean("Options", "Logging", true, Log.enabled);
                    break;
                case "update":
                    updateManger.update();
                    break;
                case "chat":
                    RuChat.enabled = !RuChat.enabled;
                    if (RuChat.enabled) {
                        Reference.ruChat = new RuChat();
                        Reference.ruChat.start();
                    } else {
                        try {
                            Reference.ruChat.closeSocket();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    WynnLang.sendMessage("§rWynnLang чат " + (RuChat.enabled ? "§aвключен" : "§cвыключен") + "§r");
                    Config.setBoolean("Chat", "Enabled", true, RuChat.enabled);
                    break;
                case "mute":
                    if (args.length == 2) {
                        RuChat.mutePlayer(args[1]);
                    } else {
                        WynnLang.sendMessage("§cУкажите от какого игрока вы не хотите получать сообщения");
                    }
                    break;
                case "info":
                    String online = Reference.ruChat.isAlive() ? String.valueOf(RuChat.online) : "неизвестно";
                    String chatConnection = Reference.ruChat.isAlive() ? "Связь с сервером чата §aустановлена" : "Связь с сервером чата §cотсутствует";
                    String playersOnline = "§rКоличество онлайн игроков: §6" + online;
                    WynnLang.sendMessage("Инфо: \n" + chatConnection + "\n" + playersOnline);
                    break;
                default:
                    sendHelpMessage();
                    break;
            }
        } else {
            sendHelpMessage();
        }
    }

    private static void sendHelpMessage() {
        WynnLang.sendMessage("Помощь\n - /wl toggle - включить/отключить мод" +
                "\n - /wl log - включить/отключить отправку непереведенных строк" +
                "\n - /wl chat - Включение/отключение общего мод чата" +
                "\n - /wl mute [ник] - Мут сообщений от отдельного игрока" +
                "\n - /wl info - Просмотр информации о моде" +
                "\n - /ru [сообщение] - Отправить сообщение в русский чат" +
                "\n - /ru - Включить чат по умолчанию. Все набранные вами сообщения отправляются сразу в русский чат.");
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
        return false;
    }

    @Override
    public List<String> getAliases() {
        List l = new ArrayList<String>();
        l.add("wynnlang");
        l.add("wl");
        l.add("WL");
        l.add("Wl");
        return l;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        List l = new ArrayList<String>();
        l.add("toggle");
        l.add("log");
        l.add("mute");
        l.add("chat");
        l.add("info");
        return l;
    }
}