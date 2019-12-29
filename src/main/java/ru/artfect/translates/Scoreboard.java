package ru.artfect.translates;

import com.google.common.collect.BiMap;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.scoreboard.ScoreObjective;
import ru.artfect.wynnlang.StringUtil;

public class Scoreboard extends TranslateType {
    private SPacketUpdateScore p;

    public Scoreboard(SPacketUpdateScore p) {
        this.p = p;
    }

    public Scoreboard() {

    }

    public Object translatePacket() {
        String str = p.getPlayerName();
        String replace = StringUtil.handleString(this, str);
        if (replace != null) {
            return new SPacketUpdateScore(replace, new ScoreObjective(null, p.getObjectiveName(), null));
        }
        return p;
    }

    public String getName() {
        return "SCOREBOARD";
    }

    public void reverse(BiMap<String, String> translated) {

    }
}