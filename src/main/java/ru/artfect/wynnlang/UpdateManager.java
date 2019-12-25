package ru.artfect.wynnlang;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;

public class UpdateManager {
    private static String downloadLink = "";
    private static String newVer = "";
    private static boolean updating = false;
    private static boolean needUpdate = false;

    public static void checkUpdate() {
        Multithreading.runAsync(() -> {
            try {
                URLConnection st = new URL("https://api.github.com/repos/ArtFect/WynnLang/releases/latest").openConnection();
                st.setConnectTimeout(16000);
                st.setReadTimeout(16000);
                JsonObject json = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();
                String version = json.get("tag_name").getAsString();
                if (!version.equals(Reference.VERSION)) {
                    newVer = version;
                    downloadLink = json.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
                    needUpdate = true;
                }
            } catch (IOException ignored) {

            }
        });
    }
    
    public static void update(){
        if (!needUpdate) {
            WynnLang.sendMessage("§cОбновление не требуется");
        } else if (!updating) {
            updating = true;
            WynnLang.sendMessage("§aНовая версия скачивается...");
            Multithreading.runAsync(() -> {
                try {
                    FileUtils.copyURLToFile(new URL(downloadLink), new File("./Mods/WynnLang.jar"), 16000, 60000);
                } catch (IOException e) {
                	updating = false;
                    WynnLang.sendMessage("§cНе удалось скачать обновление");
                }
                needUpdate = false;
                updating = false;
                WynnLang.sendMessage("§aНовая версия скачана. Пожалуйста перезагрузите Minecraft для применения обновления");
            });
        }
    }
    
    public static void sendUpdateMessage(){
        if (needUpdate) {
            Multithreading.runAsync(() -> {
                while (Minecraft.getMinecraft().player == null) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException ignored) {

                    }
                }
                ITextComponent msg = new TextComponentString("§2Доступна новая версия §6§l" + newVer + "§2 для мода §6§lWynnlang§2. Нажмите на данное сообщение для скачивания");
                msg.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/WynnLang update"));
                Minecraft.getMinecraft().player.sendMessage(msg);
            });
        }
    }
}
