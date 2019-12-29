package ru.artfect.translates;

import com.google.common.collect.BiMap;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.TextComponentString;
import ru.artfect.wynnlang.StringUtil;

public class Title extends TranslateType {
    private SPacketTitle title;

    public Title(SPacketTitle title) {
        this.title = title;
    }

    public Title() {

    }

    public Object translatePacket() {
        if(title.getMessage() != null){
            String str = title.getMessage().getFormattedText().replace("§r", "");
            String replace = StringUtil.handleString(this, str);
            if (replace != null) {
                return new SPacketTitle(title.getType(), new TextComponentString(replace), title.getFadeInTime(), title.getDisplayTime(), title.getFadeOutTime());
            }
        }
        return title;
    }

    public String getName() {
        return "TITLE";
    }

    public void reverse(BiMap<String, String> translated) {

    }
}
