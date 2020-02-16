package ru.artfect.wynnlang.translate;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.*;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.artfect.translates.*;
import ru.artfect.wynnlang.Reference;

import java.util.List;

public class MessageHandler extends ChannelInboundHandlerAdapter {
    public MessageHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        }
        if (msg instanceof SPacketWindowItems) {
            SPacketWindowItems p = (SPacketWindowItems) msg;
            List<ItemStack> items = p.getItemStacks();
            for (ItemStack item : items) {
                new ItemName(item).translate();
                new ItemLore(item).translate();
            }
        } else if (msg instanceof SPacketSetSlot) {
            SPacketSetSlot p = (SPacketSetSlot) msg;
            new ItemName(p.getStack()).translate();
            new ItemLore(p.getStack()).translate();
        } else if (msg instanceof SPacketEntityMetadata) {
            SPacketEntityMetadata p = (SPacketEntityMetadata) msg;
            new Entity(p.getDataManagerEntries()).translate();
        } else if (msg instanceof SPacketTitle) {
            SPacketTitle p = (SPacketTitle) msg;
            msg = new Title(p).translatePacket();
        } else if (msg instanceof SPacketPlayerListItem) {
            SPacketPlayerListItem p = (SPacketPlayerListItem) msg;
            new Playerlist(p).translate();
        } else if (msg instanceof SPacketOpenWindow) {
            SPacketOpenWindow p = (SPacketOpenWindow) msg;
            msg = new InventoryName(p).translatePacket();
        } else if (msg instanceof SPacketUpdateBossInfo) {
            SPacketUpdateBossInfo p = (SPacketUpdateBossInfo) msg;
            msg = new BossBar(p).translatePacket();
        } else if (msg instanceof SPacketUpdateScore) {
            SPacketUpdateScore p = (SPacketUpdateScore) msg;
            msg = new Scoreboard(p).translatePacket();
        }
        super.channelRead(ctx, msg);
    }

    @SubscribeEvent
    public void onMessage(ClientChatReceivedEvent e) {
        if (e.getType() == ChatType.GAME_INFO || !Reference.onWynncraft || !Reference.modEnabled) {
            return;
        }
        new Chat(e).translate();
    }
}
