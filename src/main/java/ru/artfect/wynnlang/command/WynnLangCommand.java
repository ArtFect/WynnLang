package ru.artfect.wynnlang.command;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import ru.artfect.wynnlang.Multithreading;
import ru.artfect.wynnlang.RuChat;
import ru.artfect.wynnlang.UpdateManager;
import ru.artfect.wynnlang.WynnLang;
import ru.artfect.wynnlang.translate.Untranslate;

public class WynnLangCommand implements ICommand {

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
                if (WynnLang.enabled) {
                    WynnLang.enabled = false;
                    WynnLang.sendMessage("§rМод §4выключен");
                    Untranslate.translate(Untranslate.untranslate);
                } else {
                    WynnLang.enabled = true;
                    WynnLang.sendMessage("§rМод §2включен");
                    Untranslate.translate(Untranslate.translate);
                }
                WynnLang.config.get("Options", "Enabled", true).set(WynnLang.enabled);
                WynnLang.config.save();
                break;
            case "log":
                if (WynnLang.logging) {
                    WynnLang.logging = false;
                    WynnLang.sendMessage("§rОтправка §cвыключена");
                } else {
                    WynnLang.logging = true;
                    WynnLang.sendMessage("§rОтправка §aвключена");
                }
                WynnLang.config.get("Options", "Logging", true).set(WynnLang.logging);
                WynnLang.config.save();
                break;
            case "update":
                if (!UpdateManager.needUpdate) {
                    WynnLang.sendMessage("§cОбновление не требуется");
                } else if (!UpdateManager.updating) {
                    UpdateManager.updating = true;
                    WynnLang.sendMessage("§aНовая версия скачивается...");
                    Multithreading.runAsync(() -> {
                        try {
                            FileUtils.copyURLToFile(new URL(UpdateManager.downloadLink), new File("./Mods/WynnLang.jar"), 16000, 60000);
                        } catch (IOException e) {
                            UpdateManager.updating = false;
                            WynnLang.sendMessage("§cНе удалось скачать обновление");
                        }
                        UpdateManager.needUpdate = false;
                        UpdateManager.updating = false;
                        WynnLang.sendMessage("§aНовая версия скачана. Пожалуйста перезагрузите Minecraft для применения обновления");
                    });
                }
                break;
            case "chat":
                if (WynnLang.ruChat.enabled) {
                    WynnLang.ruChat.enabled = false;
                    try {
                        WynnLang.ruChat.closeSocket();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    WynnLang.sendMessage("§rWynnLang чат §cвыключен");
                } else {
                    WynnLang.ruChat.enabled = true;
                    WynnLang.ruChat = new RuChat();
                    WynnLang.ruChat.start();
                    WynnLang.sendMessage("§rWynnLang чат §aвключен");
                }
                WynnLang.config.get("Chat", "Enabled", true).set(WynnLang.ruChat.enabled);
                WynnLang.config.save();
                break;
            case "mute":
                if (args.length == 2) {
                    if (!WynnLang.ruChat.muted.contains(args[1])) {
                        WynnLang.ruChat.muted.add(args[1]);
                        WynnLang.sendMessage("§rВы §cбольше не будете§r получать сообщения от игрока " + args[1]);
                    } else {
                        WynnLang.ruChat.muted.remove(args[1]);
                        WynnLang.sendMessage("§rВы §aснова можете§r получать сообщения от игрока " + args[1]);
                    }
                    String[] arr = new String[WynnLang.ruChat.muted.size()];
                    arr = WynnLang.ruChat.muted.toArray(arr);
                    WynnLang.config.get("Chat", "Muted", new String[0]).set(arr);
                    WynnLang.config.save();
                } else {
                    WynnLang.sendMessage("§cУкажите от какого игрока вы не хотите получать сообщения");
                }
                break;
            case "info":
                String online = WynnLang.ruChat.isAlive() ? String.valueOf(WynnLang.ruChat.online) : "неизвестно";
                String chatConnection = WynnLang.ruChat.isAlive() ? "Связь с сервером чата §aустановлена" : "Связь с сервером чата §cотсутствует";
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

    public static void sendHelpMessage() {
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