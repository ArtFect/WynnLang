package ru.artfect.wynnlang;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.http.client.ClientProtocolException;
import ru.artfect.wynnlang.translate.MessageHandler;

import java.io.IOException;

public class Network {
    @SubscribeEvent
    public void joinServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        ServerData s = Minecraft.getMinecraft().getCurrentServerData();
        if (s == null) {
            return;
        }

        String ip = s.serverIP.toLowerCase();
        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.isSingleplayer() && mc.getCurrentServerData() != null && (ip.contains("wynncraft.com") || ip.contains("wynncraft.org") || ip.contains("wynncraft.net"))) {
            Reference.onWynncraft = true;
            e.getManager().channel().pipeline().addBefore("fml:packet_handler", "wynnlang:packet_handler", new MessageHandler());

            if (!Reference.ruChat.isAlive() && Reference.ruChat.enabled) {
            	Reference.ruChat = new RuChat();
                Reference.ruChat.start();
            }
        }
    }

    @SubscribeEvent
    public void onDisc(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) throws ClientProtocolException, IOException, InstantiationException, IllegalAccessException {
    	Log.saveAndSend();
        Reference.ruChat.closeSocket();
        Reference.onWynncraft = false;
    }
}
