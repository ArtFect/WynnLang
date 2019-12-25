package ru.artfect.wynnlang.translate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import ru.artfect.translates.Chat;
import ru.artfect.translates.Entity;
import ru.artfect.translates.ItemLore;
import ru.artfect.translates.ItemName;
import ru.artfect.translates.Playerlist;
import ru.artfect.translates.Title;
import ru.artfect.translates.TranslateType;
import ru.artfect.wynnlang.Multithreading;
import ru.artfect.wynnlang.Reference;
import ru.artfect.wynnlang.RuChat;
import ru.artfect.wynnlang.StringUtil;
import ru.artfect.wynnlang.WynnLang;

public class ReverseTranslation {
    public static boolean enabled = false;
    public static Field chatLinesF;
    public static HashMap<Class<? extends TranslateType>, BiMap<String, String>> translated = new HashMap<>();
    
    public static void init() {
        chatLinesF = ReflectionHelper.findField(GuiNewChat.class, "chatLines", "field_146252_h");
        chatLinesF.setAccessible(true);
        MinecraftForge.EVENT_BUS.register(new ReverseTranslation());
    }
    
    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent e) {
        if (e.phase != Phase.START || !Reference.onWynncraft || !Reference.modEnabled) {
            return;
        }
        
        boolean pressed = false;
        int key = Reference.keyBindings[0].getKeyCode();
        if(key < 0){
        	pressed = Mouse.isButtonDown(key + 100);
        } else {
        	pressed = Keyboard.isKeyDown(key);
        }

        if (pressed) {
            if (!enabled) {
                enabled = true;
                reverse();
            }
        } else {
            if (enabled) {
                enabled = false;
                reverse();
            }
        }
    }
    
    public static void reverse(){
        try {
    	    for (Class<? extends TranslateType> tClass : translated.keySet()) {
    	    	tClass.newInstance().reverse(translated.get(tClass));
    	        translated.put(tClass, translated.get(tClass).inverse());
    	    }
		} catch (InstantiationException | IllegalAccessException e) {
			WynnLang.sendMessage("§4Не удалось восстановить оригинальные строки");
		}
    }
}
