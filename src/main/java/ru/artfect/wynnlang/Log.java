package ru.artfect.wynnlang;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Log {
    public static boolean enabled = true;
    private static final String LOG_PATH = Minecraft.getMinecraft().mcDataDir + "/config/WynnLang/Logs/";
    
    private static HashMap<Class <? extends TranslateType>, List<String>> oldStr = new HashMap<>();
    private static HashMap<Class <? extends TranslateType>, List<String>> newStr = new HashMap<>();

    public Log() throws IOException {
        initLogs();

        Multithreading.schedule(() -> {
            try {
                saveAndSend();
            } catch (IOException | InstantiationException | IllegalAccessException ignored) {

            }
        }, 1, 1, TimeUnit.MINUTES);
    }
    
    public static void initLogs() throws IOException{
        Path lpath = Paths.get(LOG_PATH);
        if (!Files.exists(lpath)) {
            Files.createDirectories(lpath);
        }
    }

    public static void saveAndSend() throws ClientProtocolException, IOException, InstantiationException, IllegalAccessException {
        if (!Reference.onWynncraft) {
            return;
        }
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("UUID", Minecraft.getMinecraft().getSession().getProfile().getId().toString()));
        params.add(new BasicNameValuePair("ver", Reference.VERSION));

        for (Class<? extends TranslateType> tClass : newStr.keySet()) {
            List<String> strList = newStr.get(tClass);
        	TranslateType type = tClass.newInstance();
            if (strList.isEmpty()) {
                continue;
            }
            params.add(new BasicNameValuePair(type.getName(), new Gson().toJson(strList)));
            try {
                Files.write(Paths.get(LOG_PATH + type.getName() + ".txt"), strList, StandardCharsets.UTF_8,
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

    public static void loadLogFile(Class<? extends TranslateType> tClass) {
        try {
            Path p = Paths.get(LOG_PATH + tClass.getName() + ".txt");
            if (p.toFile().exists()) {
                oldStr.put(tClass, Files.readAllLines(p));
            } else {
                oldStr.put(tClass, new ArrayList<String>());
                p.toFile().createNewFile();
            }
            newStr.put(tClass, new ArrayList<String>());
        } catch (IOException ignored) {

        }
    }

    public static void addString(Class<? extends TranslateType> type, String str) {
        if (enabled && !oldStr.get(type).contains(str)) {
            oldStr.get(type).add(str);
            newStr.get(type).add(str);
        }
    }
}
