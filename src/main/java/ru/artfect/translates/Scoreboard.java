package ru.artfect.translates;

import com.google.common.collect.BiMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.Score;
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
        if (replace != null && p.getScoreAction() == SPacketUpdateScore.Action.CHANGE) {
            net.minecraft.scoreboard.Scoreboard sb = Minecraft.getMinecraft().player.getWorldScoreboard();
            Score score = new Score(sb, new ScoreObjective(sb, p.getObjectiveName(), IScoreCriteria.DUMMY), replace);
            score.setScorePoints(p.getScoreValue());
            return new SPacketUpdateScore(score);
        }
        return p;
    }

    public String getName() {
        return "SCOREBOARD";
    }

    public void reverse(BiMap<String, String> translated) {

    }
}