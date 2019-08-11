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

public class Items {
	private static Pattern coords = Pattern.compile("(\\[[-+]?\\d+,[-+]?\\d+,[-+]?\\d+\\])");
	private static Pattern comLevel = Pattern.compile("(\\[Combat Lv\\. \\d+\\])");
	
	public static void translateItem(ItemStack item){
		if(!item.hasTagCompound()) return;
		NBTTagCompound nbt = item.getTagCompound();
		if (nbt == null) return;
		NBTTagCompound disp = nbt.getCompoundTag("display");
		if (disp == null) return;
		NBTTagList list = new NBTTagList();
		NBTTagList lore = disp.getTagList("Lore", Constants.NBT.TAG_STRING);
        String fulllore = "";
        if(lore.tagCount() < 6) return;
        for(int j = 5; j < lore.tagCount() - 2; j++){
       	 fulllore += (NBTTagString) lore.get(j);
        }
        if(fulllore.isEmpty()) return;
        fulllore = fulllore.replace("\"\"", "").replaceAll(" at ", "");
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
 	     String translate = WynnLang.books.get(fulllore);
 	     if(translate != null){
 	    	 while(lore.tagCount() > 5){
 	    		 lore.removeTag(lore.tagCount() - 1);
 	    	 }
 	    	 lore.appendTag(new NBTTagString("§7" + translate + " " + coord));
 	    	 lore.appendTag(new NBTTagString(""));
 	         lore.appendTag(new NBTTagString("§7Правый клик для отслеживания"));
 	     }
	}
}
