package ru.artfect.translates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import ru.artfect.wynnlang.StringUtil;

@NoArgsConstructor
@AllArgsConstructor
public class Scoreboard implements TranslatablePacket<SPacketUpdateScore> {
    private SPacketUpdateScore packet;
    @Getter
    private static final String name= "SCOREBOARD";

    public SPacketUpdateScore translatePacket() {
        String str = packet.getPlayerName();
        String replace = StringUtil.handleString(this, str);
        if (replace != null && packet.getScoreAction() == SPacketUpdateScore.Action.CHANGE) {
            net.minecraft.scoreboard.Scoreboard sb = Minecraft.getMinecraft().player.getWorldScoreboard();
            Score score = new Score(sb, new ScoreObjective(sb, packet.getObjectiveName(), IScoreCriteria.DUMMY), replace);
            score.setScorePoints(packet.getScoreValue());
            return new SPacketUpdateScore(score);
        }
        return packet;
    }
}