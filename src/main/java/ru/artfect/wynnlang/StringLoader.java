package ru.artfect.wynnlang;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import net.minecraftforge.fml.common.Loader;
import org.apache.http.util.TextUtils;
import ru.artfect.translates.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

public class StringLoader {
    public static void load() {
        loadType(new Chat());
        loadType(new Entity());
        loadType(new ItemLore());
        loadType(new ItemName());
        loadType(new PlayerList());
        loadType(new Title());
        loadType(new InventoryName());
        loadType(new BossBar());
        loadType(new Scoreboard()); // Тут нужен енум

        if(Loader.isModLoaded("wynnexp")){
            WynnLang.common.get(ItemName.class).remove("§dQuest Book");
            Map<String, String> loreMap = WynnLang.common.get(ItemLore.class);
            loreMap.remove("§a\u2714§7 Class Req: Mage/Dark Wizard");
            loreMap.remove("§a\u2714§7 Class Req: Warrior/Knight");
            loreMap.remove("§a\u2714§7 Class Req: Archer/Hunter");
            loreMap.remove("§a\u2714§7 Class Req: Shaman/Skyseer");
        }
    }

    private static void loadType(TranslateType type) {
        Log.loadLogFile(type);
        loadList(type);
    }

    private static void loadList(TranslateType type) {
        try {
            WynnLang.common.put(type, Maps.newHashMap());
            WynnLang.regex.put(type, Maps.newHashMap());
            Reference.reverseTranslation.getTranslated().put((Flipped) type, HashBiMap.create());
            String name = type.getName();
            BufferedReader br = new BufferedReader(new InputStreamReader(WynnLang.class.getResourceAsStream("/" + name + "/list.txt"), StandardCharsets.UTF_8));
            String line;
            Map<String, String> map = WynnLang.common.get(type);
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
        while (!TextUtils.isEmpty(line = br.readLine())) {
            String[] text = line.split("@");
            if (text.length == 2)
                map.put(regex ? Pattern.compile(text[0]) : text[0], text[1].equals(" ") ? "" : text[1]);
        }
    }
}
