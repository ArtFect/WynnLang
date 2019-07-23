package ru.artfect.wynnlang;

import java.util.ArrayList;
import java.util.List;
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
		if(WynnLang.enabled){
			WynnLang.enabled = false;
			sender.sendMessage(new TextComponentString("§6§lWynnLang: §4§lМод выключен"));
		} else {
			WynnLang.enabled = true;
			sender.sendMessage(new TextComponentString("§6§lWynnLang: §2§lМод включен"));
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
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return null;
	}

    
}