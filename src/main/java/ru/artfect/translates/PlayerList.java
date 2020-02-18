package ru.artfect.translates;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerListItem.AddPlayerData;
import net.minecraft.util.text.TextComponentString;
import ru.artfect.wynnlang.StringUtil;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerList implements Translatable {
    private SPacketPlayerListItem packet;
    @Getter
    private static final String name = "PLAYERLIST";

    public void translate() {
        List<AddPlayerData> playerList = packet.getEntries();
        if (playerList == null)
            return;
        for (int i = 0; i != playerList.size(); i++) {
            AddPlayerData data = playerList.get(i);
            if (data.getDisplayName() != null) {
                String str = data.getDisplayName().getFormattedText();
                if (!str.isEmpty()) {
                    String replace = StringUtil.findReplace(this, str);
                    if (replace != null && !replace.isEmpty()) {
                        playerList.set(i, packet.new AddPlayerData(data.getProfile(), data.getPing(), data.getGameMode(), new TextComponentString(replace)));
                    }
                }
            }
        }
    }
}
