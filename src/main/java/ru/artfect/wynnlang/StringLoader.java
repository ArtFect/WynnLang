package ru.artfect.wynnlang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.HashBiMap;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Loader;
import ru.artfect.translates.Chat;
import ru.artfect.translates.Entity;
import ru.artfect.translates.ItemLore;
import ru.artfect.translates.ItemName;
import ru.artfect.translates.Playerlist;
import ru.artfect.translates.Title;
import ru.artfect.translates.TranslateType;
import ru.artfect.wynnlang.translate.ReverseTranslation;

public class StringLoader {
	public static void load() throws InstantiationException, IllegalAccessException{
		loadList(Chat.class);
		loadList(Entity.class);
		loadList(ItemLore.class);
		loadList(ItemName.class);
		loadList(Playerlist.class);
		loadList(Title.class);
	}
	
    private static void loadList(Class<? extends TranslateType> type) throws InstantiationException, IllegalAccessException {
        try {
        	WynnLang.common.put(type, new HashMap<String, String>());
        	WynnLang.regex.put(type, new HashMap<Pattern, String>());
        	ReverseTranslation.translated.put(type, HashBiMap.create());
        	String name = type.newInstance().getName();
            BufferedReader br = new BufferedReader(new InputStreamReader(WynnLang.class.getResourceAsStream("/" + name + "/list.txt"), "UTF8"));
            String line;
            HashMap map = WynnLang.common.get(type);
            while ((line = br.readLine()) != null) {
                loadFile(name + "/" + line, map, false);
            }
            loadFile(name + "/regex.txt", WynnLang.regex.get(type), true);
            //if(!Loader.isModLoaded("wynntils")){
            //	loadFile(type.getName() + "/wynntilsRegex.txt", WynnLang.regex.get(type), true);
            //	loadFile(type.getName() + "/wynntils.txt", WynnLang.common.get(type), false);
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void loadFile(String fileName, Map map, boolean regex) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(WynnLang.class.getResourceAsStream("/" + fileName), "UTF8"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] text = line.split("@");
            if (text.length == 2) {
            	if(text[1].equals(" ")){
            		map.put(regex ? Pattern.compile(text[0]) : text[0] , "");
            	} else {
            		map.put(regex ? Pattern.compile(text[0]) : text[0] , text[1]);
            	}
            }
        }
    }
}
