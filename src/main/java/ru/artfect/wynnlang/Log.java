package ru.artfect.wynnlang;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import ru.artfect.translates.TranslateType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Log {
    public static boolean enabled = true;
    private static final String LOG_PATH = Minecraft.getMinecraft().mcDataDir + "/config/WynnLang/Logs/";

    static private Map<TranslateType, List<String>> oldStr = Maps.newHashMap();
    static private Map<TranslateType, List<String>> newStr = Maps.newHashMap();

    public Log() throws IOException {
        initLogs();
        Multithreading.schedule(() -> {
            try {
                saveAndSend();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    static void initLogs() throws IOException {
        Path lpath = Paths.get(LOG_PATH);
        if (!Files.exists(lpath)) {
            Files.createDirectories(lpath);
        }
    }

    static void saveAndSend() throws IOException {
        if (!Reference.onWynncraft) {
            return;
        }
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("UUID", Minecraft.getMinecraft().getSession().getProfile().getId().toString()));
        params.add(new BasicNameValuePair("ver", Reference.VERSION));

        for (TranslateType tClass : newStr.keySet()) {
            List<String> strList = newStr.get(tClass);
            if (strList.isEmpty()) {
                continue;
            }
            params.add(new BasicNameValuePair(tClass.getName(), new Gson().toJson(strList)));
            try {
                Files.write(Paths.get(LOG_PATH + tClass.getName() + ".txt"), strList, StandardCharsets.UTF_8,
                        StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            } catch (IOException ignored) {

            }
            strList.clear();
        }

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://" + Reference.SERVER);
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        client.execute(httpPost);
        client.close();
    }

    static void loadLogFile(TranslateType tClass) {
        try {
            Path p = Paths.get(LOG_PATH + tClass.getName() + ".txt");
            if (p.toFile().exists()) {
                oldStr.put(tClass, Files.readAllLines(p));
            } else {
                oldStr.put(tClass, new ArrayList<>());
                p.toFile().createNewFile();
            }
            newStr.put(tClass, new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void addString(TranslateType type, String str) {
        if (enabled && !oldStr.get(type).contains(str)) {
            oldStr.get(type).add(str);
            newStr.get(type).add(str);
        }
    }
}
