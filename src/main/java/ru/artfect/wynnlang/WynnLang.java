package ru.artfect.wynnlang;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ru.artfect.wynnlang.command.WynnLangCommand;

@Mod(modid = WynnLang.MOD_ID, name = WynnLang.NAME, version = WynnLang.VERSION)
public class WynnLang {
    public static final String VERSION = "1.6";
    public static final String MOD_ID = "wynnlang";
    public static final String NAME = "WynnLang";

    public static String downloadLink = "";
    public static String newVer = "";
    public static boolean needUpdate = false;
    public static boolean onWynncraft = false;

    public static boolean logging = true;
    public static boolean enabled = true;

    public static Minecraft mc;

    public static HashMap<MessageType, HashMap<String, String>> common = new HashMap<MessageType, HashMap<String, String>>();
    public static HashMap<MessageType, HashMap<Pattern, String>> regex = new HashMap<MessageType, HashMap<Pattern, String>>();

    public static Pattern questText = Pattern.compile("§7\\[\\d+\\/\\d+\\](?:§r§2 | §r§2)(.*):(?:§r§a | §r)(.*)§r");
    public static String playername = "";
    public static String uuid = "";

    public static Configuration config;

    public enum MessageType {
        CHAT, // TODO: Перенести в CHAT_NEW
        QUEST, // TODO: Перенести в CHAT_NEW
        ITEM_LORE, ITEM_NAME, ENTITY_NAME, TITLE, CHAT_NEW
    }

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventsHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException {
        mc = Minecraft.getMinecraft();
        playername = mc.getMinecraft().getSession().getProfile().getName();
        uuid = mc.getMinecraft().getSession().getProfile().getId().toString();
        ClientCommandHandler.instance.registerCommand(new WynnLangCommand());
        loadConfig();

        EnumSet.allOf(MessageType.class).forEach(type -> loadList(type));
        if (!Loader.isModLoaded("wynntils")) {
            loadFile("ITEM_LORE/wynntils.txt", common.get(MessageType.ITEM_LORE), 4);
            loadFile("CHAT_NEW/wynntilsRegex.txt", regex.get(MessageType.CHAT_NEW), 3);
        }

        Log.init();
        WynnLang.checkUpdate();
    }

    public void loadConfig() {
        try {
            config = new Configuration(new File(mc.mcDataDir + "/config/WynnLang/config.cfg"));
            config.load();
            boolean logging = config.get("Options", "Logging", true).getBoolean();
            boolean enabled = config.get("Options", "Enabled", true).getBoolean();
            this.logging = logging;
            this.enabled = enabled;
        } catch (Exception e) {
            System.out.println("Error loading config, returning to default variables.");
        } finally {
            config.save();
        }
    }

    private void loadList(MessageType type) {
        common.put(type, new HashMap<String, String>());
        regex.put(type, new HashMap<Pattern, String>());
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(getClass().getResourceAsStream("/" + type.name() + "/list.txt"), "UTF8"));
            String line;
            int loadt = 4;
            if (type == MessageType.CHAT) {
                loadt = 2;
            } else if (type == MessageType.QUEST) {
                loadt = 1;
            }
            HashMap map = common.get(type);
            while ((line = br.readLine()) != null) {
                loadFile(type.name() + "/" + line, map, loadt);
            }
            if (type != MessageType.QUEST && type != MessageType.CHAT) {
                loadFile(type.name() + "/regex.txt", regex.get(type), 3);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String format(String s) {
        return s.replaceAll("§.", "").replaceAll("[^a-zA-Z0-9-+]", "").toLowerCase();
    }

    public static String replaceRegex(Map<Pattern, String> map, String str) {
        Set<Pattern> pats = map.keySet();
        for (Pattern pat : pats) {
            Matcher mat = pat.matcher(str);
            if (mat.find()) {
                String repl = map.get(pat);
                for (int gr = 0; gr != mat.groupCount() + 1; gr++) {
                    repl = repl.replace("(r" + gr + ")", mat.group(gr));
                }
                return repl;
            }
        }
        return null;
    }

    public static String findReplace(MessageType type, String str) {
        String replace = common.get(type).get(str);
        if (replace == null) {
            replace = replaceRegex(regex.get(type), str);
        }
        return replace;
    }

    public void loadFile(String fileName, Map map, int loadType) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/" + fileName), "UTF8"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] text = line.split("@");
            if (text.length == 2) {
                if (loadType == 1) {
                    int ind = text[0].indexOf(": ") + 2;
                    String dialog = format(text[0].substring(ind).replace("playername", playername));
                    map.put(dialog, text[1].replace("<playername>", playername));
                } else if (loadType == 2) {
                    map.put(format(text[0]), text[1]);
                } else if (loadType == 3) {
                    map.put(Pattern.compile(text[0]), text[1].equals(" ") ? "" : text[1]);
                } else if (loadType == 4) {
                    map.put(text[0], text[1].equals(" ") ? "" : text[1]);
                }
            }
        }
    }

    public static void checkUpdate() {
        Multithreading.runAsync(() -> {
            try {
                URLConnection st = new URL("https://api.github.com/repos/ArtFect/WynnLang/releases/latest")
                        .openConnection();
                st.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                st.setConnectTimeout(16000);
                st.setReadTimeout(16000);
                JsonObject json = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();
                String version = json.get("tag_name").getAsString();
                if (!version.equals(VERSION)) {
                    newVer = version;
                    downloadLink = json.get("assets").getAsJsonArray().get(0).getAsJsonObject()
                            .get("browser_download_url").getAsString();
                    needUpdate = true;
                }
            } catch (IOException e) {

            }
        });
    }
}