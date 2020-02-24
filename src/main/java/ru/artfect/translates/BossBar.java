package ru.artfect.translates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfoServer;
import org.apache.http.util.TextUtils;
import ru.artfect.wynnlang.StringUtil;

@AllArgsConstructor
@NoArgsConstructor
public class BossBar implements TranslateType {

    private SPacketUpdateBossInfo packet;
    @Getter
    private static final String name = "BOSSBAR";

    public SPacketUpdateBossInfo translatePacket() {
        if (packet.getName() != null) {
            String replace = StringUtil.handleString(this, packet.getName().getUnformattedText());
            if (!TextUtils.isEmpty(replace)) {
                return new SPacketUpdateBossInfo(
                        packet.getOperation(),
                        new BossInfoServer(
                                new TextComponentString(replace),
                                packet.getColor(),
                                packet.getOverlay()
                        )
                );
            }
        }
        return packet;
    }
}