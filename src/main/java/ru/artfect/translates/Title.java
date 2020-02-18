package ru.artfect.translates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.TextComponentString;
import ru.artfect.wynnlang.StringUtil;

@AllArgsConstructor
@NoArgsConstructor
public class Title implements TranslatablePacket<SPacketTitle> {
    private SPacketTitle title;
    @Getter
    private static final String name = "TITLE";

    public SPacketTitle translatePacket() {
        if(title.getMessage() != null){
            String str = title.getMessage().getFormattedText().replace("§r", "");
            String replace = StringUtil.handleString(this, str);
            if (replace != null) {
                return new SPacketTitle(title.getType(), new TextComponentString(replace), title.getFadeInTime(), title.getDisplayTime(), title.getFadeOutTime());
            }
        }
        return title;
    }
}
