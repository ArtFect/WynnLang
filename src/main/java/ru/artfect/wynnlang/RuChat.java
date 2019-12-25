package ru.artfect.wynnlang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RuChat extends Thread {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    public static boolean defaultChat = false;
    public static boolean enabled = true;
    public static int online = 0;
    private static ArrayList<String> muted = new ArrayList<String>();
    
    public RuChat(){
    	String[] chatMuted = Config.getStringArray("Chat", "Muted", new String[0]);
        muted.addAll(Arrays.asList(chatMuted));
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public static void startTimer(){
        Multithreading.schedule(() -> {
            if (!Reference.ruChat.isAlive() && Reference.ruChat.enabled && Reference.onWynncraft) {
                Reference.ruChat = new RuChat();
                Reference.ruChat.start();
            }
        }, 30, 30, TimeUnit.SECONDS);
    }
    
    public static void mutePlayer(String playerName){
        if (!muted.contains(playerName)) {
        	muted.add(playerName);
            WynnLang.sendMessage("§rВы §cбольше не будете§r получать сообщения от игрока " + playerName);
        } else {
            muted.remove(playerName);
            WynnLang.sendMessage("§rВы §aснова можете§r получать сообщения от игрока " + playerName);
        }
        String[] arr = new String[muted.size()];
        arr = muted.toArray(arr);
        Config.setStringArray("Chat", "Muted", new String[0], arr);
    }

    public void run() {
        try {
            socket = new Socket(Reference.SERVER, 25500);
            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                sendMessage("n:" + Minecraft.getMinecraft().getSession().getProfile().getName());
                while (true) {
                    String incomingMessage = in.readLine();
                    if (incomingMessage != null) {
                        if (incomingMessage.startsWith("o:")) { //Online packet; Example o:10
                            online = Integer.parseInt(incomingMessage.replace("o:", "")); //10
                        } else if (incomingMessage.startsWith("p:") && Minecraft.getMinecraft().player != null) {//Message packet; Example p:Fiw m:Hello
                            String packet = incomingMessage.replace("p:", "");
                            String[] a = packet.split(" m:");
                            String name = a[0]; //Fiw
                            String message = a[1]; //Hello
                            if (!muted.contains(name)) {
                            	Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(Reference.CHAT_PREFIX + " §6" + name + ": §r" + message));
                            }
                        }
                    }
                    incomingMessage = null;
                }
            } finally {
                closeSocket();
            }
        } catch (Exception e) {
        	WynnLang.sendMessage("§rСвязь с чатом оборвана. Произошла ошибка");
        }
    }
    
    public void sendMessage(String message) {
        if (this.isAlive() && out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        } else {
            WynnLang.sendMessage("§cПроизошла ошибка, сообщение не отправлено. Попробуйте еще раз");
        }
    }
    
    public void closeSocket() throws IOException {
        if (this.isAlive()) {
            out.flush();
            out.close();
            in.close();
            socket.close();
        }
    }
    
    @SubscribeEvent
    public void sendToDefaultChat(ClientChatEvent e) throws Exception {
        if (!e.getMessage().startsWith("/") && defaultChat) {
        	e.setCanceled(true);
        	Reference.ruChat.sendMessage("m:" + e.getMessage());
        }
    }
}