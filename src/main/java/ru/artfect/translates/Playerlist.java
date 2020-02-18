package ru.artfect.translates;


import com.google.common.collect.BiMap;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerListItem.AddPlayerData;
import net.minecraft.util.text.TextComponentString;
import ru.artfect.wynnlang.StringUtil;

import java.util.List;

public class Playerlist extends TranslateType {
    private SPacketPlayerListItem packet;

    public Playerlist(SPacketPlayerListItem packet) {
        this.packet = packet;
    }

    public Playerlist() {

    }

    public void translate() {
        List<AddPlayerData> playerlist = packet.getEntries();
        if (playerlist == null)
            return;
        for (int i = 0; i != playerlist.size(); i++) {
            AddPlayerData data = playerlist.get(i);
            if (data.getDisplayName() != null) {
                String str = data.getDisplayName().getFormattedText();
                if (!str.isEmpty()) {
                    String replace = StringUtil.findReplace(this.getClass(), str);
                    if (replace != null && !replace.isEmpty()) {
                        playerlist.set(i, packet.new AddPlayerData(data.getProfile(), data.getPing(), data.getGameMode(), new TextComponentString(replace)));
                    }
                }
            }
        }
    }

    public String getName() {
        return "PLAYERLIST";
    }

    public void reverse(BiMap<String, String> translated) {

    }
}
