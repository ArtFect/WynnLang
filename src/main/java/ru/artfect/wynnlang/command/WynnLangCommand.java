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
import net.minecraft.util.text.TextComponentString;
import ru.artfect.wynnlang.Multithreading;
import ru.artfect.wynnlang.WynnLang;

public class WynnLangCommand implements ICommand {
    private static boolean updating = false;

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
        if (args.length == 1) {
            if (args[0].equals("toggle")) {
                if (WynnLang.enabled) {
                    WynnLang.enabled = false;
                    sender.sendMessage(new TextComponentString("§6§lWynnLang: §4§lМод выключен"));
                } else {
                    WynnLang.enabled = true;
                    sender.sendMessage(new TextComponentString("§6§lWynnLang: §2§lМод включен"));
                }
                WynnLang.config.get("Options", "Enabled", true).set(WynnLang.enabled);
                WynnLang.config.save();
            } else if (args[0].equals("log")) {
                if (WynnLang.logging) {
                    WynnLang.logging = false;
                    sender.sendMessage(new TextComponentString("§6§lWynnLang: §4§lОтправка выключена"));
                } else {
                    WynnLang.logging = true;
                    sender.sendMessage(new TextComponentString("§6§lWynnLang: §2§lОтправка включена"));
                }
                WynnLang.config.get("Options", "Logging", true).set(WynnLang.logging);
                WynnLang.config.save();
            } else if (args[0].equals("update")) {
                if (!WynnLang.needUpdate) {
                    sender.sendMessage(new TextComponentString("§cОбновление не требуется"));
                } else if (!updating) {
                    updating = true;
                    sender.sendMessage(new TextComponentString("§aНовая версия скачивается..."));
                    Multithreading.runAsync(() -> {
                        try {
                            FileUtils.copyURLToFile(new URL(WynnLang.downloadLink), new File("./Mods/WynnLang.jar"),
                                    16000, 60000);
                        } catch (IOException e) {
                            updating = false;
                            sender.sendMessage(new TextComponentString("§cНе удалось скачать обновление"));
                        }
                        WynnLang.needUpdate = false;
                        updating = false;
                        sender.sendMessage(new TextComponentString(
                                "§aНовая версия скачана. Пожалуйста перезагрузите Minecraft для применения обновления"));
                    });
                }
            }
        } else {
            sender.sendMessage(new TextComponentString(
                    "§6§lWynnLang:\n - /WynnLang toggle - включить/отключить мод\n - /WynnLang log - включить/отключить отправку непереведенных строк"));
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
        return false;
    }

    @Override
    public List<String> getAliases() {
        List l = new ArrayList<String>();
        l.add("wynnlang");
        return l;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
            BlockPos targetPos) {
        List l = new ArrayList<String>();
        l.add("toggle");
        l.add("log");
        return l;
    }
}