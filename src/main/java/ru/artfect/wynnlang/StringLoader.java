package ru.artfect.wynnlang;

import com.google.common.collect.HashBiMap;
import net.minecraftforge.fml.common.Loader;
import ru.artfect.translates.*;
import ru.artfect.wynnlang.translate.ReverseTranslation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class StringLoader {
    public static void load() throws InstantiationException, IllegalAccessException {
        loadType(Chat.class);
        loadType(Entity.class);
        loadType(ItemLore.class);
        loadType(ItemName.class);
        loadType(Playerlist.class);
        loadType(Title.class);
        loadType(InventoryName.class);
        loadType(BossBar.class);
        loadType(Scoreboard.class);
    }

    private static void loadType(Class<? extends TranslateType> type) throws IllegalAccessException, InstantiationException {
        Log.loadLogFile(type);
        loadList(type);
    }

    private static void loadList(Class<? extends TranslateType> type) throws InstantiationException, IllegalAccessException {
        try {
            WynnLang.common.put(type, new HashMap<>());
            WynnLang.regex.put(type, new HashMap<>());
            ReverseTranslation.translated.put(type, HashBiMap.create());
            String name = type.newInstance().getName();
            BufferedReader br = new BufferedReader(new InputStreamReader(WynnLang.class.getResourceAsStream("/" + name + "/list.txt"), StandardCharsets.UTF_8));
            String line;
            HashMap map = WynnLang.common.get(type);
            while ((line = br.readLine()) != null) {
                loadFile(name + "/" + line, map, false);
            }
            if(!Loader.isModLoaded("wynntils")){
                loadFile(name + "/wynntils/regex.txt", WynnLang.regex.get(type), true);
                loadFile(name + "/wynntils/common.txt", WynnLang.common.get(type), false);
            }
            loadFile(name + "/regex.txt", WynnLang.regex.get(type), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadFile(String fileName, Map map, boolean regex) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(WynnLang.class.getResourceAsStream("/" + fileName), StandardCharsets.UTF_8));
        String line;
        while ((line = br.readLine()) != null) {
            String[] text = line.split("@");
            if (text.length == 2) {
                if (text[1].equals(" ")) {
                    map.put(regex ? Pattern.compile(text[0]) : text[0], "");
                } else {
                    map.put(regex ? Pattern.compile(text[0]) : text[0], text[1]);
                }
            }
        }
    }
}
