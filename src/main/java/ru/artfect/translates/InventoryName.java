package ru.artfect.translates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;
import ru.artfect.wynnlang.StringUtil;

@AllArgsConstructor
@NoArgsConstructor
public class InventoryName implements TranslatablePacket<SPacketOpenWindow> {
    private SPacketOpenWindow p;
    @Getter
    private static final String name = "INVENTORY_NAME";
    public SPacketOpenWindow translatePacket() {
        String str = p.getWindowTitle().getUnformattedText();
        String replace = StringUtil.handleString(this, str);
        if (replace != null) {
            return new SPacketOpenWindow(p.getWindowId(), p.getGuiId(), new TextComponentString(replace), p.getSlotCount(), p.getEntityId());
        }
        return p;
    }
}
