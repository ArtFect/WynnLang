package ru.artfect.wynnlang;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import ru.artfect.wynnlang.translate.PacketHandler;

public class Network {
    @SubscribeEvent
    public void joinServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        ServerData s = WynnLang.mc.getCurrentServerData();
        if (s == null) {
            return;
        }

        String ip = s.serverIP.toLowerCase();
        if (!WynnLang.mc.isSingleplayer() && WynnLang.mc.getCurrentServerData() != null && (ip.contains("wynncraft.com") || ip.contains("wynncraft.org") || ip.contains("wynncraft.net"))) {
            WynnLang.onWynncraft = true;
            e.getManager().channel().pipeline().addBefore("fml:packet_handler", "wynnlang:packet_handler", new PacketHandler());

            if (!WynnLang.ruChat.isAlive() && WynnLang.ruChat.enabled) {
            	WynnLang.ruChat = new RuChat();
                WynnLang.ruChat.start();
            }

            if (UpdateManager.needUpdate) {
                Multithreading.runAsync(() -> {
                    while (Minecraft.getMinecraft().player == null) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException x) {

                        }
                    }
                    ITextComponent msg = new TextComponentString("§2Доступна новая версия §6§l" + UpdateManager.newVer + "§2 для мода §6§lWynnlang§2. Нажмите на данное сообщение для скачивания");
                    msg.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/WynnLang update"));
                    Minecraft.getMinecraft().player.sendMessage(msg);
                });
            }
        }
    }

    @SubscribeEvent
    public void onDisc(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) throws ClientProtocolException, IOException {
        Log.saveAndSend();
        WynnLang.ruChat.closeSocket();
        WynnLang.onWynncraft = false;
    }
}
