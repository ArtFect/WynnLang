package ru.artfect.translates;

import com.google.common.collect.BiMap;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;
import ru.artfect.wynnlang.StringUtil;

public class InventoryName extends TranslateType {
    private SPacketOpenWindow p;

    public InventoryName(SPacketOpenWindow p) {
        this.p = p;
    }

    public InventoryName() {

    }

    public Object translatePacket() {
        String str = p.getWindowTitle().getUnformattedText();
        String replace = StringUtil.handleString(this, str);
        if (replace != null) {
            return new SPacketOpenWindow(p.getWindowId(), p.getGuiId(), new TextComponentString(replace), p.getSlotCount(), p.getEntityId());
        }
        return p;
    }

    public String getName() {
        return "INVENTORY_NAME";
    }

    public void reverse(BiMap<String, String> translated) {

    }
}
