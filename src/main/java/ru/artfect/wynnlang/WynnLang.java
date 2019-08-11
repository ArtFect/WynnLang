package ru.artfect.wynnlang;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Mod(modid = WynnLang.MOD_ID, name = WynnLang.NAME, version = WynnLang.VERSION)
public class WynnLang{
	public static final String VERSION = "1.3";
	public static final String MOD_ID = "wynnlang";
	public static final String NAME = "WynnLang";
	
	public static String downloadLink = "";
	public static String newVer = "";
	public static boolean needUpdate = false;
	public static boolean onWynncraft = false;
	public static boolean enabled = true;
	
	public static Minecraft mc;
	
	public static Map<String, HashMap<String, String>> dialogs = new HashMap<String, HashMap<String, String>>();
	public static HashMap<String, String> books = new HashMap<>(); 
	public static HashMap<String, String> msgs = new HashMap<>(); 
	public static HashMap<Pattern, String> regex = new HashMap<>(); 
	
    public static Pattern questText = Pattern.compile("§7\\[\\d+\\/\\d+\\](?:§r§2 | §r§2)(.*):(?:§r§a | §r)(.*)§r");
    public static String playername = "";
    
	@EventHandler
    public void preinit(FMLPreInitializationEvent event){//
		MinecraftForge.EVENT_BUS.register(new EventsHandler());
    }
	
    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException{
    	mc = Minecraft.getMinecraft();
    	playername = mc.getMinecraft().getSession().getProfile().getName();
        ClientCommandHandler.instance.registerCommand(new WynnLangCommand());
        ClientCommandHandler.instance.registerCommand(new UpdateWLCommand());

    	BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/qlist.txt"), "UTF8"));
    	String line;
        while ((line = br.readLine()) != null) {
        	loadFile(line, dialogs, 1);
        }
        loadFile("Book/BaseBook.txt", books, 2);
    	loadFile("Msg/Msg.txt", msgs, 2);
    	loadFile("Msg/Regex.txt", regex, 3);
    	
    	WynnLang.checkUpdate();
    }
    
    public static String format(String s){
    	return s.replaceAll("§.", "").replaceAll("[^a-zA-Z0-9-+]", "").toLowerCase();
    }
    
    public void loadFile(String fileName, Map map, int type) throws IOException{
    	BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + fileName), "UTF8"));
    	String line;
        while ((line = br.readLine()) != null) {
        	String[] text = line.split("@");
            if(text.length == 2){
            	if(type == 1){
            		int ind = text[0].indexOf(": ") + 2;
            		String name = format(text[0].substring(0, ind - 2));
            		String dialog = format(text[0].substring(ind).replace("playername", playername));
                	HashMap map2 = (HashMap) map.get(name);
                	if(map2 == null){
                		Map<String, String> newMap = new HashMap<String, String>();
                		newMap.put(dialog, text[1]);
                		map.put(name, newMap);
                	} else {
                		map2.put(dialog, text[1]);
                	}
            	} else if(type == 2) {
                	map.put(format(text[0]), text[1]);
            	} else if(type == 3){
                	map.put(Pattern.compile(text[0]), text[1]);
            	}
            }
        }
    }

	public static void checkUpdate() {
        Multithreading.runAsync(() -> {
    		try {
    	        URLConnection st = new URL("https://api.github.com/repos/ArtFect/WynnLang/releases/latest").openConnection();
    	        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
    	        st.setConnectTimeout(16000);
    	        st.setReadTimeout(16000);
    	        JsonObject json = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();
    	        String version = json.get("tag_name").getAsString();
    	        if(!version.equals(VERSION)){
    	        	newVer = version;
    	            downloadLink = json.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
    	            needUpdate = true;
    	        }
    		} catch (IOException e) {
    			
    		}
        });
	}
}
