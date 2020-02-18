package ru.artfect.wynnlang.command;

import lombok.Getter;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import ru.artfect.wynnlang.Reference;
import ru.artfect.wynnlang.WynnLang;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class WynnLangCommand implements ICommand {

    @Getter
    private static final String name = Reference.NAME;
    private String usage;

    public WynnLangCommand() {
        this.usage = "/WynnLang";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return usage;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        AtomicBoolean needHelp = new AtomicBoolean(true);
        if (args.length != 0) {
            Stream.of(Commands.values())
                    .filter(command -> command.getKey().equalsIgnoreCase(args[0]))
                    .findAny()
                    .ifPresent(commands -> {
                                commands.getCommand().execute(args);
                                needHelp.set(false);
                            }
                    );
        }
        if (needHelp.get()) {
            WynnLang.sendMessage("Помощь\n - /wl toggle - включить/отключить мод" +
                    "\n - /wl log - включить/отключить отправку непереведенных строк" +
                    "\n - /wl chat - Включение/отключение общего мод чата" +
                    "\n - /wl mute [ник] - Мут сообщений от отдельного игрока" +
                    "\n - /wl info - Просмотр информации о моде" +
                    "\n - /ru [сообщение] - Отправить сообщение в русский чат" +
                    "\n - /ru - Включить чат по умолчанию. Все набранные вами сообщения отправляются сразу в русский чат.");
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
        return Arrays.asList("wynnlang", "wl", "WL", "Wl");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return Arrays.asList("toggle", "log", "mute", "chat", "info");
    }
}