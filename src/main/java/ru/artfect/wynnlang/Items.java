package ru.artfect.wynnlang;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import ru.artfect.wynnlang.WynnLang.MessageType;

public class Items {
	private static Pattern coords = Pattern.compile("(\\[[-+]?\\d+,[-+]?\\d+,[-+]?\\d+\\])");
	private static Pattern comLevel = Pattern.compile("(\\[Combat Lv\\. \\d+\\])");
	
	public static void translateBook(ItemStack item){
		if(!item.hasTagCompound()) return;
		NBTTagCompound nbt = item.getTagCompound();
		if (nbt == null) return;
		NBTTagCompound disp = nbt.getCompoundTag("display");
		if (disp == null) return;
		NBTTagList lore = disp.getTagList("Lore", Constants.NBT.TAG_STRING);
        if(lore.tagCount() < 6) return;
        String fulllore = "";
        for(int j = 5; j < lore.tagCount() - 2; j++){
        	fulllore += lore.getStringTagAt(j);
        }
        if(fulllore.isEmpty()) return;
        fulllore = fulllore.replaceAll(" at ", "");
        String coord = "";
        Matcher m = coords.matcher(fulllore);
 	     if(m.find()) {
 	       coord =  m.group(1);
 	     }
 	     fulllore = fulllore.replace(coord, "");
 	   
 	     Matcher m1 = comLevel.matcher(fulllore);
 	     if(m1.find()) {
 	       String s1 = m1.group(1);
 	       fulllore = fulllore.replace(s1, "");
 	     }
 	     fulllore = WynnLang.format(fulllore);
 	     String replace = WynnLang.findReplace(MessageType.BOOK, fulllore);
 	     if(replace != null){
 	    	 while(lore.tagCount() > 5){
 	    		 lore.removeTag(lore.tagCount() - 1);
 	    	 }
 	    	 lore.appendTag(new NBTTagString("§7" + replace + " " + coord));
 	    	 lore.appendTag(new NBTTagString(""));
 	         lore.appendTag(new NBTTagString("§7Правый клик для отслеживания"));
 	     }
	}
	
	public static void translateItem(ItemStack item){
		if(!item.hasTagCompound()) return;
		NBTTagCompound nbt = item.getTagCompound();
		if (nbt == null) return;
		NBTTagCompound disp = nbt.getCompoundTag("display");
		if (disp == null) return;
		String name = disp.getString("Name");
		String nameReplace = WynnLang.findReplace(MessageType.ITEM_NAME, name);
		if(nameReplace == null){
			Log.addString(MessageType.ITEM_NAME, name);
		} else {
    		if(!nameReplace.isEmpty()) item.setStackDisplayName(nameReplace);
		}
		NBTTagList list = new NBTTagList();
		NBTTagList lore = disp.getTagList("Lore", Constants.NBT.TAG_STRING);
        for(int j = 0; j < lore.tagCount(); j++){
        	String replace = WynnLang.findReplace(MessageType.ITEM_LORE, lore.getStringTagAt(j));
        	if(replace == null){
        		Log.addString(MessageType.ITEM_LORE, lore.getStringTagAt(j));
        	} else {
        		if(!replace.isEmpty()) lore.set(j, new NBTTagString(replace));
        	}
        }
	}
}
