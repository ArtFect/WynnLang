package ru.artfect.wynnlang;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.EntityDataManager.DataEntry;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketWindowItems;
import ru.artfect.wynnlang.WynnLang.MessageType;

public class PacketHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        }
        if (WynnLang.enabled) {
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
                    for (DataEntry<?> data : dataArray) {
                        if (data.getKey().getId() == 2) {
                            if (WynnLang.findReplace(MessageType.ENTITY_NAME, (String) data.getValue()) == null) {
                                Log.addString(MessageType.ENTITY_NAME, (String) data.getValue());
                            }
                        }
                    }
                }
            } else if (msg instanceof SPacketTitle) {
                SPacketTitle p = (SPacketTitle) msg;
                if (p.getMessage() != null) {
                    String str = p.getMessage().getFormattedText().replace("§r", "");
                    if (WynnLang.findReplace(MessageType.TITLE, str) == null) {
                        Log.addString(MessageType.TITLE, str);
                    }
                }
            }
        }
        super.channelRead(ctx, msg);
    }
}
