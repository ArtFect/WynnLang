package ru.artfect.translates;

import com.google.common.collect.BiMap;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import ru.artfect.wynnlang.StringUtil;

public class ItemName extends TranslateType {
    private ItemStack item;

    public ItemName(ItemStack item) {
        this.item = item;
    }

    public ItemName() {

    }

    @Override
    public void translate() {
        if(item.getDisplayName() != null){
            String nameReplace = StringUtil.handleString(this, item.getDisplayName());
            if (nameReplace != null) {
                item.setStackDisplayName(nameReplace);
            }
        }
    }

    public String getName() {
        return "ITEM_NAME";
    }

    public void reverse(BiMap<String, String> translated) {
        NonNullList<ItemStack> con = Minecraft.getMinecraft().player.openContainer.getInventory();
        for (ItemStack item : con) {
            String str = item.getDisplayName();
            String nameReplace = translated.get(str);
            if (nameReplace != null) {
                item.setStackDisplayName(nameReplace);
            }
        }
    }
}
