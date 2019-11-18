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

import net.minecraft.util.text.TextComponentString;

public class RuChat extends Thread {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    public static boolean defaultChat = false;
    public static boolean enabled = true;
    public static int online = 0;
    public static ArrayList<String> muted = new ArrayList<String>();

    public void run() {
        try {
            socket = new Socket(WynnLang.SERVER, 25500);
            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                sendMessage("n:" + WynnLang.playerName);
                while (true) {
                    String incomingMessage = in.readLine();
                    if (incomingMessage != null) {
                        if (incomingMessage.startsWith("o:")) { //Online packet; Example o:10
                            online = Integer.parseInt(incomingMessage.replace("o:", "")); //10
                        } else if (incomingMessage.startsWith("p:") && WynnLang.mc.player != null) {//Message packet; Example p:Fiw m:Hello
                            String packet = incomingMessage.replace("p:", "");
                            String[] a = packet.split(" m:");
                            String name = a[0]; //Fiw
                            String message = a[1]; //Hello
                            if (!muted.contains(name)) {
                            	WynnLang.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(WynnLang.CHAT_PREFIX + " §6" + name + ": §r" + message));
                            }
                        }
                    }
                    incomingMessage = null;
                }
            } finally {
                closeSocket();
            }
        } catch (Exception e) {
        	
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
}