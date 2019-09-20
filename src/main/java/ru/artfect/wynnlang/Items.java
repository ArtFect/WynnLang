package ru.artfect.wynnlang;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import ru.artfect.wynnlang.WynnLang.MessageType;

public class Items {
	public static void translateItem(ItemStack item) {
		if (!item.hasTagCompound())
			return;
		NBTTagCompound nbt = item.getTagCompound();
		if (nbt == null)
			return;
		NBTTagCompound disp = nbt.getCompoundTag("display");
		if (disp == null)
			return;
		String name = disp.getString("Name");
		String nameReplace = WynnLang.findReplace(MessageType.ITEM_NAME, name);
		if (nameReplace == null) {
			Log.addString(MessageType.ITEM_NAME, name);
		} else {
			if (!nameReplace.isEmpty())
				item.setStackDisplayName(nameReplace);
		}
		NBTTagList list = new NBTTagList();
		NBTTagList lore = disp.getTagList("Lore", Constants.NBT.TAG_STRING);
		for (int j = 0; j < lore.tagCount(); j++) {
			String replace = WynnLang.findReplace(MessageType.ITEM_LORE, lore.getStringTagAt(j));
			if (replace == null) {
				System.out.println(lore.getStringTagAt(j));
				Log.addString(MessageType.ITEM_LORE, lore.getStringTagAt(j));
			} else {
				if (!replace.isEmpty())
					lore.set(j, new NBTTagString(replace));
			}
		}
	}
}
