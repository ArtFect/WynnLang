package ru.artfect.wynnlang;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager.DataEntry;
import net.minecraft.network.play.server.SPacketEntity.S15PacketEntityRelMove;
import net.minecraft.network.play.server.SPacketEntity.S17PacketEntityLookMove;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import ru.artfect.wynnlang.WynnLang.MessageType;

public class PacketHandler extends ChannelInboundHandlerAdapter {
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg == null) return;
        if(WynnLang.enabled){
            if(msg instanceof SPacketWindowItems){
        		SPacketWindowItems p = (SPacketWindowItems) msg;
    			List<ItemStack> items = p.getItemStacks();
    			for(ItemStack item : items){
    				Items.translateItem(item);
    			}
        	} else if(msg instanceof SPacketSetSlot) {
        		SPacketSetSlot p = (SPacketSetSlot) msg;
        		Items.translateItem(p.getStack());
         	} else if (msg instanceof  SPacketEntityMetadata) {
            	SPacketEntityMetadata p = (SPacketEntityMetadata) msg;
            	List<DataEntry<?>> dataArray = p.getDataManagerEntries();
            	for(DataEntry<?> data : dataArray){
            		if(data.getKey().getId() == 2){
            			if(WynnLang.findReplace(MessageType.ENTITY_NAME, (String) data.getValue()) == null) Log.addString(MessageType.ENTITY_NAME, (String) data.getValue());
            		}
            	}
            } else if (msg instanceof SPacketTitle) {
            	SPacketTitle p = (SPacketTitle) msg;
            	if(p.getMessage() != null){
            		if(WynnLang.findReplace(MessageType.TITLE, p.getMessage().getFormattedText()) == null) Log.addString(MessageType.TITLE, p.getMessage().getFormattedText());
            	}
            } 
        }
        super.channelRead(ctx, msg);
	}
}
