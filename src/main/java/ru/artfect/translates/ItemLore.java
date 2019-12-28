package ru.artfect.translates;

import com.google.common.collect.BiMap;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import ru.artfect.wynnlang.StringUtil;

public class ItemLore extends TranslateType {
    private ItemStack item;

    public ItemLore(ItemStack item) {
        this.item = item;
    }

    public ItemLore() {

    }

    @Override
    public void translate() {
        if (!item.hasTagCompound()) {
            return;
        }
        NBTTagCompound nbt = item.getTagCompound();
        if (nbt == null) {
            return;
        }
        NBTTagCompound disp = nbt.getCompoundTag("display");
        NBTTagList list = new NBTTagList();
        NBTTagList lore = disp.getTagList("Lore", Constants.NBT.TAG_STRING);
        for (int j = 0; j < lore.tagCount(); j++) {
            String replace = StringUtil.handleString(this, lore.getStringTagAt(j));
            if (replace != null) {
                lore.set(j, new NBTTagString(replace));
            }
        }
    }

    public String getName() {
        return "ITEM_LORE";
    }

    public void reverse(BiMap<String, String> translated) {
        NonNullList<ItemStack> con = Minecraft.getMinecraft().player.openContainer.getInventory();
        for (ItemStack item : con) {
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
                String str = lore.getStringTagAt(j);
                String loreReplace = translated.get(str);
                if (loreReplace != null) {
                    lore.set(j, new NBTTagString(loreReplace));
                }
            }
        }
    }
}
