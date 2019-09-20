package ru.artfect.wynnlang;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;

import org.apache.http.client.ClientProtocolException;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import ru.artfect.wynnlang.WynnLang.MessageType;

public class EventsHandler {
	@SubscribeEvent
	public void onMessage(ClientChatReceivedEvent e) {
		if (e.getType() == ChatType.GAME_INFO || !WynnLang.onWynncraft || !WynnLang.enabled)
			return;
		ITextComponent rawMsg = e.getMessage();
		String message = rawMsg.getFormattedText();
		String replace = WynnLang.findReplace(MessageType.CHAT_NEW, message.replace("§r", ""));
		if (replace != null) {
			if (!replace.isEmpty())
				e.setMessage(new TextComponentString(replace));
			return;
		} else {
			Log.addString(MessageType.CHAT, message);
		}

		Matcher questMsg = WynnLang.questText.matcher(message);
		if (questMsg.matches()) {
			String npcName = WynnLang.format(questMsg.group(1));
			String format = WynnLang.format(questMsg.group(2));
			replace = WynnLang.findReplace(MessageType.QUEST, format);
			if (replace != null) {
				e.setMessage(new TextComponentString(message.replace(questMsg.group(2), "§a" + replace)));
			}
		} else {
			String format = WynnLang.format(message);
			replace = WynnLang.common.get(MessageType.CHAT).get(format);
			if (replace != null) {
				e.setMessage(new TextComponentString(replace));
			} else {
				replace = WynnLang.replaceRegex(WynnLang.regex.get(MessageType.CHAT),
						e.getMessage().getUnformattedText());
				if (replace != null) {
					e.setMessage(new TextComponentString(replace));
				}
			}
		}
	}

	@SubscribeEvent
	public void joinServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
		String ip = Objects.requireNonNull(WynnLang.mc.getCurrentServerData()).serverIP.toLowerCase();
		if (!WynnLang.mc.isSingleplayer() && WynnLang.mc.getCurrentServerData() != null
				&& (ip.contains("wynncraft.com") || ip.contains("wynncraft.org") || ip.contains("wynncraft.net"))) {
			WynnLang.onWynncraft = true;
			e.getManager().channel().pipeline().addBefore("fml:packet_handler", "wynnlang:packet_handler",
					new PacketHandler());

			if (WynnLang.needUpdate) {
				Multithreading.runAsync(() -> {
					while (Minecraft.getMinecraft().player == null) {
						try {
							Thread.sleep(100L);
						} catch (InterruptedException x) {

						}
					}
					ITextComponent msg = new TextComponentString("§2Доступна новая версия §6§l" + WynnLang.newVer
							+ "§2 для мода §6§lWynnlang§2. Нажмите на данное сообщение для скачивания");
					msg.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/WynnLang update"));
					Minecraft.getMinecraft().player.sendMessage(msg);
				});
			}
		}
	}

	@SubscribeEvent
	public void onDisc(FMLNetworkEvent.ClientDisconnectionFromServerEvent e)
			throws ClientProtocolException, IOException {
		WynnLang.onWynncraft = false;
		Log.saveAndSend();
	}
}