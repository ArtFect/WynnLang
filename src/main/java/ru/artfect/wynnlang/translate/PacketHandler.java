package ru.artfect.wynnlang.translate;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.EntityDataManager.DataEntry;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerListItem.AddPlayerData;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.text.TextComponentString;
import ru.artfect.wynnlang.WynnLang;
import ru.artfect.wynnlang.WynnLang.MessageType;

public class PacketHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        }
        if (msg instanceof SPacketWindowItems) {
            SPacketWindowItems p = (SPacketWindowItems) msg;
            List<ItemStack> items = p.getItemStacks();
            for (ItemStack item : items) {
                Items.translateItem(item);
            }
        } else if (msg instanceof SPacketSetSlot) {
            SPacketSetSlot p = (SPacketSetSlot) msg;
            Items.translateItem(p.getStack());
        } else if (msg instanceof SPacketEntityMetadata) {
            SPacketEntityMetadata p = (SPacketEntityMetadata) msg;
            List<DataEntry<?>> dataArray = p.getDataManagerEntries();
            if (dataArray != null) {
                for (int i = 0; i != dataArray.size(); i++) {
                    DataEntry<?> data = dataArray.get(i);
                    if (data.getKey().getId() == 2) {
                        String str = (String) data.getValue();
                        String replace = WynnLang.handleString(MessageType.ENTITY_NAME, str);
                        if (replace != null) {
                            dataArray.set(i, new DataEntry(data.getKey(), replace));
                        }
                    }
                }
            }
        } else if (msg instanceof SPacketTitle) {
            SPacketTitle p = (SPacketTitle) msg;
            if (p.getMessage() != null) {
                String str = p.getMessage().getFormattedText().replace("§r", "");
                String replace = WynnLang.handleString(MessageType.TITLE, str);
                if (replace != null) {
                    msg = new SPacketTitle(p.getType(), new TextComponentString(replace), p.getFadeInTime(), p.getDisplayTime(), p.getFadeOutTime());
                }
            }
        }/* else if (msg instanceof SPacketPlayerListItem) {
            SPacketPlayerListItem p = (SPacketPlayerListItem) msg;
            List<AddPlayerData> tablist = p.getEntries();
            if (tablist != null) {
                for (int i = 0; i != tablist.size(); i++) {
                    AddPlayerData data = tablist.get(i);
                    if (data.getDisplayName() != null) {
                        String str = data.getDisplayName().getFormattedText();
                        if (!str.isEmpty()) {
                            String replace = WynnLang.findReplace(MessageType.PLAYERLIST, str);
                            if (replace != null && !replace.isEmpty()) {
                                tablist.set(i, p.new AddPlayerData(data.getProfile(), data.getPing(), data.getGameMode(), new TextComponentString(replace)));
                            }
                        }
                    }
                }
            }
        } */ else if (msg instanceof SPacketOpenWindow) {
            SPacketOpenWindow p = (SPacketOpenWindow) msg;
            String str = p.getWindowTitle().getUnformattedText();
            String replace = WynnLang.handleString(MessageType.INVENTORY_NAME, str);
            if (replace != null) {
                //TODO: replace
            }
        } else if (msg instanceof SPacketUpdateScore) {
            SPacketUpdateScore p = (SPacketUpdateScore) msg;
            String str = p.getPlayerName();
            String replace = WynnLang.handleString(MessageType.SCOREBOARD, str);
            if (replace != null) {
                //TODO: replace
            }
        } else if (msg instanceof SPacketUpdateBossInfo) {
            SPacketUpdateBossInfo p = (SPacketUpdateBossInfo) msg;
            if (p.getName() != null) {
                String str = p.getName().getUnformattedText();
                String replace = WynnLang.handleString(MessageType.BOSSBAR, str);
                if (replace != null) {
                    //TODO: replace
                }
            }
        }
        super.channelRead(ctx, msg);
    }
}
