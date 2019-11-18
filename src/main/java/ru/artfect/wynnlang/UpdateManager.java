package ru.artfect.wynnlang;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UpdateManager {
    public static String downloadLink = "";
    public static String newVer = "";
    public static boolean needUpdate = false;
    public static boolean updating = false;

    public static void checkUpdate() {
        Multithreading.runAsync(() -> {
            try {
                URLConnection st = new URL("https://api.github.com/repos/ArtFect/WynnLang/releases/latest").openConnection();
                st.setConnectTimeout(16000);
                st.setReadTimeout(16000);
                JsonObject json = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();
                String version = json.get("tag_name").getAsString();
                if (!version.equals(WynnLang.VERSION)) {
                    newVer = version;
                    downloadLink = json.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
                    needUpdate = true;
                }
            } catch (IOException e) {

            }
        });
    }
}
