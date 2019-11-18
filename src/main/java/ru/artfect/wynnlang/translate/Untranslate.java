package ru.artfect.wynnlang.translate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import ru.artfect.wynnlang.Multithreading;
import ru.artfect.wynnlang.RuChat;
import ru.artfect.wynnlang.WynnLang;
import ru.artfect.wynnlang.WynnLang.MessageType;

public class Untranslate {
    public static HashMap<MessageType, HashMap<String, String>> untranslate = new HashMap<MessageType, HashMap<String, String>>();
    public static HashMap<MessageType, HashMap<String, String>> translate = new HashMap<MessageType, HashMap<String, String>>();
    public static boolean enabled = false;
    public static Field chatLinesF;
    
    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent e) {
        if (e.phase != Phase.START || !WynnLang.onWynncraft || !WynnLang.enabled) {
            return;
        }

        boolean pressed = false;
        int key = WynnLang.keyBindings[0].getKeyCode();
        if(key < 0){
        	pressed = Mouse.isButtonDown(key + 100);
        } else {
        	pressed = Keyboard.isKeyDown(key);
        }

        if (pressed) {
            if (!enabled) {
                enabled = true;
                translate(untranslate);
            }
        } else {
            if (enabled) {
                enabled = false;
                translate(translate);
            }
        }
    }
    
    @SubscribeEvent
    public void key(GuiScreenEvent.KeyboardInputEvent.Post e){
    	
    }
    
    public static void translate(HashMap<MessageType, HashMap<String, String>> map){
        chat(map);
        entity(map);
        item(map);
    }
    
    private static void entity(HashMap<MessageType, HashMap<String, String>> map) {
    	HashMap<String, String> nameMap = map.get(MessageType.ENTITY_NAME);
        List<Entity> entites = WynnLang.mc.player.world.loadedEntityList;
        for (Entity en : entites) {
        	String str = en.getDisplayName().getFormattedText().replaceAll("§r", "");
            String replace = nameMap.get(str);
            if (replace != null) {
                en.setCustomNameTag(replace);
            }
        }
    }

    private static void chat(HashMap<MessageType, HashMap<String, String>> map) {
		try {
	    	HashMap<String, String> messageMap = map.get(MessageType.CHAT_NEW);
	    	GuiNewChat chat = WynnLang.mc.ingameGUI.getChatGUI();
			List<ChatLine> chatLines = (List<ChatLine>) chatLinesF.get(chat);
	        for(int i = 0; i != chatLines.size(); i++){
	        	ChatLine cl = chatLines.get(i);
	        	String str = cl.getChatComponent().getFormattedText().replaceAll("§r", "");
	            String replace = messageMap.get(str);
	            if (replace != null) {
	            	chatLines.set(i, new ChatLine(cl.getUpdatedCounter(), new TextComponentString(replace), cl.getChatLineID()));
	            }
	        }
	        chat.refreshChat();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			WynnLang.sendMessage("§4Ошибка");
		}
    }

    private static void item(HashMap<MessageType, HashMap<String, String>> map) {
    	HashMap<String, String> nameMap = map.get(MessageType.ITEM_NAME);
    	HashMap<String, String> loreMap = map.get(MessageType.ITEM_LORE);
        NonNullList<ItemStack> con = WynnLang.mc.player.openContainer.getInventory();
        for (ItemStack item : con) {
        	String strName = item.getDisplayName();
            String nameReplace = nameMap.get(strName);
            if (nameReplace != null) {
            	item.setStackDisplayName(nameReplace);
            }

            if (!item.hasTagCompound()) {
                continue;
            }
            NBTTagCompound nbt = item.getTagCompound();
            if (nbt == null) {
                continue;
            }
            NBTTagCompound disp = nbt.getCompoundTag("display");
            if (disp == null) {
                continue;
            }
            NBTTagList list = new NBTTagList();
            NBTTagList lore = disp.getTagList("Lore", Constants.NBT.TAG_STRING);
            for (int j = 0; j < lore.tagCount(); j++) {
            	String strLore = lore.getStringTagAt(j);
                String loreReplace = loreMap.get(strLore);
                if (loreReplace != null) {
                	lore.set(j, new NBTTagString(loreReplace));
                }
            }
        }
    }
}
