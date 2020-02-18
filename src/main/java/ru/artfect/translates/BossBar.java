package ru.artfect.translates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfoServer;
import ru.artfect.wynnlang.StringUtil;

@AllArgsConstructor
@NoArgsConstructor
public class BossBar implements TranslateType {

    private SPacketUpdateBossInfo packet;
    @Getter
    private static final String name = "BOSSBAR";

    public SPacketUpdateBossInfo translatePacket() {
        if (packet.getName() != null) {
            String str = packet.getName().getUnformattedText();
            String replace = StringUtil.handleString(this, str);
            if (replace != null) {
                return new SPacketUpdateBossInfo(packet.getOperation(), new BossInfoServer(new TextComponentString(replace), packet.getColor(), packet.getOverlay()));
            }
        }
        return packet;
    }
}