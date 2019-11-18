package ru.artfect.wynnlang;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import ru.artfect.wynnlang.command.RuCommand;
import ru.artfect.wynnlang.command.WynnLangCommand;
import ru.artfect.wynnlang.translate.Chat;
import ru.artfect.wynnlang.translate.Untranslate;

@Mod(modid = WynnLang.MOD_ID, name = WynnLang.NAME, version = WynnLang.VERSION)
public class WynnLang {
    public static final String VERSION = "2.0.3";
    public static final String MOD_ID = "wynnlang";
    public static final String NAME = "WynnLang";
    public static final String SERVER = "35.228.84.245";
    public static final String CHAT_PREFIX = "§a[WL]";

    public static boolean onWynncraft = false;

    public static boolean logging = true;
    public static boolean enabled = true;

    public static Minecraft mc;

    public static HashMap<MessageType, HashMap<String, String>> common = new HashMap<MessageType, HashMap<String, String>>();
    public static HashMap<MessageType, HashMap<Pattern, String>> regex = new HashMap<MessageType, HashMap<Pattern, String>>();

    public static Pattern questText = Pattern.compile("§7\\[\\d+\\/\\d+\\](?:§r§2 | §r§2)(.*):(?:§r§a | §r)(.*)§r");

    public static String playerName = "";
    public static String uuid = "";

    public static Configuration config;

    public static RuChat ruChat = new RuChat();

    public static KeyBinding[] keyBindings = new KeyBinding[1];

    public enum MessageType {
        CHAT, // TODO: Перенести в CHAT_NEW
        QUEST, // TODO: Перенести в CHAT_NEW
        ITEM_LORE, ITEM_NAME, ENTITY_NAME, TITLE, CHAT_NEW, INVENTORY_NAME, SCOREBOARD, BOSSBAR
    }

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        keyBindings[0] = new KeyBinding("Показ оригинальных строк", Keyboard.KEY_F8, "WynnLang");
        ClientRegistry.registerKeyBinding(keyBindings[0]);
    	
        MinecraftForge.EVENT_BUS.register(new Chat());
        MinecraftForge.EVENT_BUS.register(new Network());
        MinecraftForge.EVENT_BUS.register(new Untranslate());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException {
        mc = Minecraft.getMinecraft();

        playerName = mc.getMinecraft().getSession().getProfile().getName();
        uuid = mc.getMinecraft().getSession().getProfile().getId().toString();

        ClientCommandHandler.instance.registerCommand(new WynnLangCommand());
        ClientCommandHandler.instance.registerCommand(new RuCommand());
        loadConfig();

        EnumSet.allOf(MessageType.class).forEach(type -> loadList(type));
        
        if (!Loader.isModLoaded("wynntils")) {
            loadFile("ITEM_NAME/wynntils.txt", common.get(MessageType.ITEM_NAME), 4);
            loadFile("ITEM_LORE/wynntils.txt", common.get(MessageType.ITEM_LORE), 4);
            loadFile("ITEM_LORE/wynntilsRegex.txt", common.get(MessageType.ITEM_LORE), 3);
            loadFile("CHAT_NEW/wynntils.txt", common.get(MessageType.CHAT_NEW), 4);
            loadFile("CHAT_NEW/wynntilsRegex.txt", regex.get(MessageType.CHAT_NEW), 3);
        }

        Log.init();
        UpdateManager.checkUpdate();
        
        Untranslate.chatLinesF = ReflectionHelper.findField(GuiNewChat.class, "chatLines", "field_146252_h");
        Untranslate.chatLinesF.setAccessible(true);
        
        Multithreading.schedule(() -> {
            if (!ruChat.isAlive() && ruChat.enabled && onWynncraft) {
                ruChat = new RuChat();
                ruChat.start();
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    public void loadConfig() {
        try {
            config = new Configuration(new File(mc.mcDataDir + "/config/WynnLang/config.cfg"));
            config.load();
            boolean logging = config.get("Options", "Logging", true).getBoolean();
            boolean enabled = config.get("Options", "Enabled", true).getBoolean();
            boolean chatEnabled = config.get("Chat", "Enabled", true).getBoolean();
            String[] chatMuted = config.get("Chat", "Muted", new String[0]).getStringList();
            this.logging = logging;
            this.enabled = enabled;
            WynnLang.ruChat.enabled = chatEnabled;
            WynnLang.ruChat.muted.addAll(Arrays.asList(chatMuted));
        } catch (Exception e) {
            System.out.println("Error loading config, returning to default variables.");
        } finally {
            config.save();
        }
    }

    private void loadList(MessageType type) {
        Untranslate.untranslate.put(type, new HashMap<String, String>());
        Untranslate.translate.put(type, new HashMap<String, String>());
        common.put(type, new HashMap<String, String>());
        regex.put(type, new HashMap<Pattern, String>());
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + type.name() + "/list.txt"), "UTF8"));
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

    public static void sendMessage(String message) {
        mc.player.sendMessage(new TextComponentString(CHAT_PREFIX + " " + message));
    }

    public static String replaceRegex(Map<Pattern, String> map, String str) {
        Set<Pattern> pats = map.keySet();
        for (Pattern pat : pats) {
            Matcher mat = pat.matcher(str);
            if (mat.matches()) {
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
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + fileName), "UTF8"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] text = line.split("@");
            if (text.length == 2) {
                if (loadType == 1) {
                    int ind = text[0].indexOf(": ") + 2;
                    String dialog = format(text[0].substring(ind).replace("playername", playerName));
                    map.put(dialog, text[1].replace("<playername>", playerName));
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

    public static String handleString(MessageType type, String str) {
        String replace = WynnLang.findReplace(type, str);
        if (replace != null) {
            if (replace.isEmpty()) {
                return null;
            } else {//replace found
                Untranslate.untranslate.get(type).put(replace, str);
                Untranslate.translate.get(type).put(str, replace);
            	
            	//dont translate new string if alt is holded or wynnlang is disabled
                if(WynnLang.enabled && !Untranslate.enabled){
                    return replace;
                } else {
                	return null;
                }
            }
        } else {
            Log.addString(type, str);
            return null;
        }
    }
}