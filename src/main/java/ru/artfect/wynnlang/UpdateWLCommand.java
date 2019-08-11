package ru.artfect.wynnlang;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class UpdateWLCommand implements ICommand {
	private static boolean updating = false;
	
	@Override
	public String getName() {
		return "UpdateWL";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/UpdateWL";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!WynnLang.needUpdate){
			sender.sendMessage(new TextComponentString("§cОбновление не требуется"));
		} else if(!updating){
			updating = true;
			sender.sendMessage(new TextComponentString("§aНовая версия скачивается..."));
            Multithreading.runAsync(() -> {
    			try {
    				FileUtils.copyURLToFile(new URL(WynnLang.downloadLink),  new File("./Mods/WynnLang.jar"), 16000, 60000);
    			} catch (IOException e) {
    				updating = false;
    				sender.sendMessage(new TextComponentString("§cНе удалось скачать обновление"));
    			}
    			WynnLang.needUpdate = false;
    			updating = false;
    			sender.sendMessage(new TextComponentString("§aНовая версия скачана. Пожалуйста перезагрузите Minecraft для применения обновления"));
            });
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
		return new ArrayList<String>();
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return null;
	}

    
}