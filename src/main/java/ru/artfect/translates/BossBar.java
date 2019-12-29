package ru.artfect.translates;

import com.google.common.collect.BiMap;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfoServer;
import ru.artfect.wynnlang.StringUtil;

public class BossBar extends TranslateType {
    private SPacketUpdateBossInfo p;

    public BossBar(SPacketUpdateBossInfo p) {
        this.p = p;
    }

    public BossBar() {

    }

    public Object translatePacket() {
        if(p.getName() != null){
            String str = p.getName().getUnformattedText();
            String replace = StringUtil.handleString(this, str);
            if (replace != null) {
                return new SPacketUpdateBossInfo(p.getOperation(), new BossInfoServer(new TextComponentString(replace), p.getColor(), p.getOverlay()));
            }
        }
        return p;
    }

    public String getName() {
        return "BOSSBAR";
    }

    public void reverse(BiMap<String, String> translated) {

    }
}