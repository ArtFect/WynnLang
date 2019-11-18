package ru.artfect.wynnlang.translate;

import java.util.regex.Matcher;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.artfect.wynnlang.WynnLang;
import ru.artfect.wynnlang.WynnLang.MessageType;

public class Chat {
    @SubscribeEvent
    public void onMessage(ClientChatReceivedEvent e) {
        if (e.getType() == ChatType.GAME_INFO || !WynnLang.onWynncraft || !WynnLang.enabled) {
            return;
        }
        ITextComponent rawMsg = e.getMessage();
        String message = rawMsg.getFormattedText();

        ClickEvent clickEvent = null;
        HoverEvent hoverEvent = null;
        for (ITextComponent part : rawMsg.getSiblings()) {
            Style st = part.getStyle();
            if (st.getClickEvent() != null || st.getHoverEvent() != null) {
                clickEvent = st.getClickEvent();
                hoverEvent = st.getHoverEvent();
            }
        }

        String str = message.replace("§r", "");
        String replace = WynnLang.handleString(MessageType.CHAT_NEW, str);
        if (replace != null) {
            e.setMessage(newMessage(replace, clickEvent, hoverEvent));
            return;
        }

        Matcher questMsg = WynnLang.questText.matcher(message);
        if (questMsg.matches()) {
            String npcName = WynnLang.format(questMsg.group(1));
            String format = WynnLang.format(questMsg.group(2));
            replace = WynnLang.findReplace(MessageType.QUEST, format);
            if (replace != null) {
                e.setMessage(new TextComponentString(message.replace(questMsg.group(2), "§a" + replace)));
                return;
            }
        } else {
            String format = WynnLang.format(message);
            replace = WynnLang.common.get(MessageType.CHAT).get(format);
            if (replace != null) {
                e.setMessage(new TextComponentString(replace));
            } else {
                replace = WynnLang.replaceRegex(WynnLang.regex.get(MessageType.CHAT), e.getMessage().getUnformattedText());
                if (replace != null) {
                    e.setMessage(new TextComponentString(replace));
                }
            }
        }
    }

    private TextComponentString newMessage(String replace, ClickEvent clickEvent, HoverEvent hoverEvent) {
        TextComponentString msg = new TextComponentString(replace);
        if (clickEvent != null || hoverEvent != null) {
            Style style = new Style();
            style.setClickEvent(clickEvent);
            style.setHoverEvent(hoverEvent);
            msg.setStyle(style);
        }
        return msg;
    }

    @SubscribeEvent
    public void sendMessage(ClientChatEvent e) throws Exception {
        if (!e.getMessage().startsWith("/") && WynnLang.ruChat.defaultChat) {
        	e.setCanceled(true);
        	WynnLang.ruChat.sendMessage("m:" + e.getMessage());
        }
    }
}