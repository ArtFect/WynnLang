package ru.artfect.wynnlang;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class EventsHandler {
	private Container lastGUI = null;
	
	@SubscribeEvent
    public void onMessage(ClientChatReceivedEvent e){
    	if(e.getType() == ChatType.GAME_INFO || !WynnLang.onWynncraft || !WynnLang.enabled) return;
    	String message = e.getMessage().getFormattedText();

        Matcher questMsg = WynnLang.questText.matcher(message);
    	System.out.println(message);
        if(questMsg.matches()){
        	String npcName = WynnLang.format(questMsg.group(1));
        	String format = WynnLang.format(questMsg.group(2));
        	HashMap<String, String> map = WynnLang.dialogs.get(npcName);
        	if(map == null) return;
            String replace = map.get(format);
        	if(replace == null) return;
        	replace = replace.replace("<playername>", WynnLang.playername);
        	e.setMessage(new TextComponentString(message.replace(questMsg.group(2), "§a" + replace)));
        } else {
        	String format = WynnLang.format(message);
            String replace = WynnLang.msgs.get(format);
        	if(replace != null){
            	e.setMessage(new TextComponentString(replace));
        	} else {
        		String unform = e.getMessage().getUnformattedText();
        		Set<Pattern> pats = WynnLang.regex.keySet();
        		for(Pattern pat : pats){
        			Matcher mat = pat.matcher(unform);
        	        if(mat.matches()){
        	        	String repl = WynnLang.regex.get(pat);
        	        	for(int gr = 1; gr != mat.groupCount() + 1; gr++){
        	            	repl = repl.replace("(r" + gr + ")", mat.group(gr));
        	        	}
                    	e.setMessage(new TextComponentString(repl));
         	        }
        		}
        	}
        }
    }
	
    @SubscribeEvent
    public void joinServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
    	if(!WynnLang.mc.isSingleplayer() && WynnLang.mc.getCurrentServerData() != null && Objects.requireNonNull(WynnLang.mc.getCurrentServerData()).serverIP.toLowerCase().contains("wynncraft")) {
    		WynnLang.onWynncraft = true;
    		
        	if (WynnLang.needUpdate) {
                Multithreading.runAsync(() -> {
                    while (Minecraft.getMinecraft().player == null) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException x) {
                        	
                        }
                    }
            		ITextComponent msg = new TextComponentString("§2Доступна новая версия " + WynnLang.newVer + " для мода Wynnlang. Нажмите на данное сообщение для скачивания");
            		msg.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/UpdateWL"));
            		Minecraft.getMinecraft().player.sendMessage(msg);
                });
            }
    	}
    }

	@SubscribeEvent
    public void onDisc(FMLNetworkEvent.ClientDisconnectionFromServerEvent e){
    	WynnLang.onWynncraft = false;
    }
	
	
	@SubscribeEvent
    public void onNewGUI(GuiScreenEvent.DrawScreenEvent.Pre e) {
    	if(!WynnLang.onWynncraft || !WynnLang.enabled) return;
    	if(e.getGui() instanceof GuiContainer){
    		if(WynnLang.mc.player != null){
    			GuiContainer c = (GuiContainer) e.getGui();
    			if(c.inventorySlots.equals(lastGUI)) return;
    			List<Slot> a = c.inventorySlots.inventorySlots;
    			for(int i = 0; i != a.size(); i++){
    				Slot slot = a.get(i);
    				if(!slot.getHasStack()) continue;
        			Items.translateItem(slot.getStack());
    			}
    		}
    	}
	}
}