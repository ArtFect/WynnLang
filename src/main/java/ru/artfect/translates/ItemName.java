package ru.artfect.translates;

import com.google.common.collect.BiMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.apache.http.util.TextUtils;
import ru.artfect.wynnlang.StringUtil;

@AllArgsConstructor
@NoArgsConstructor
public class ItemName implements Translatable, Flipped {
    private ItemStack item;
    @Getter
    private static final String name = "ITEM_NAME";

    @Override
    public void translate() {
        if (!TextUtils.isEmpty(item.getDisplayName())) {
            String nameReplace = StringUtil.handleString(this, item.getDisplayName());
            if (nameReplace != null)
                item.setStackDisplayName(nameReplace);
        }
    }

    public void reverse(BiMap<String, String> translated) {
        Minecraft.getMinecraft().player.openContainer.getInventory().forEach(
                item -> {
                    String nameReplace = translated.get(item.getDisplayName());
                    if (nameReplace != null)
                        item.setStackDisplayName(nameReplace);
                }
        );
    }
}
