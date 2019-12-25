package ru.artfect.wynnlang;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;

public class Config {
    private static Configuration config;
    
    public static void loadConfigFromFile(){
    	try {
            config = new Configuration(new File(Minecraft.getMinecraft().mcDataDir + "/config/WynnLang/config.cfg"));
            config.load();
            Log.enabled = config.get("Options", "Logging", true).getBoolean();
            Reference.modEnabled = config.get("Options", "Enabled", true).getBoolean();
            Reference.ruChat.enabled = config.get("Chat", "Enabled", true).getBoolean();
        } catch (Exception e) {
            System.out.println("Error loading config, returning to default variables.");
        } finally {
            config.save();
        }
    }
    
    public static void setStringArray(String category, String name, String[] defaultValue, String[] value){
        config.get(category, name, defaultValue).set(value);
        config.save();
    }
    
    public static void setBoolean(String category, String name, boolean defaultValue, boolean value){
        config.get(category, name, defaultValue).set(value);
        config.save();
    }
    
    public static String[] getStringArray(String category, String name, String[] defaultValue){
    	return config.get(category, name, defaultValue).getStringList();
    }
}
