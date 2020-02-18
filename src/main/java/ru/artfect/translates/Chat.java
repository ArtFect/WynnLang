package ru.artfect.translates;

import com.google.common.collect.BiMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import ru.artfect.wynnlang.Reference;
import ru.artfect.wynnlang.StringUtil;
import ru.artfect.wynnlang.WynnLang;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class Chat implements Flipped, Translatable {
    private ClientChatReceivedEvent event;
    @Getter
    private static final String name = "CHAT";

    public void translate() {
        ITextComponent rawMsg = event.getMessage();
        String str = rawMsg.getFormattedText();

        ClickEvent clickEvent = null;
        HoverEvent hoverEvent = null;
        for (ITextComponent part : rawMsg.getSiblings()) {
            Style st = part.getStyle();
            if (st.getClickEvent() != null || st.getHoverEvent() != null) {
                clickEvent = st.getClickEvent();
                hoverEvent = st.getHoverEvent();
            }
        }

        String replace = StringUtil.handleString(this, str);
        if (replace != null) {
            TextComponentString msg = new TextComponentString(replace);
            if (clickEvent != null || hoverEvent != null) {
                Style style = new Style();
                style.setClickEvent(clickEvent);
                style.setHoverEvent(hoverEvent);
                msg.setStyle(style);
            }
            event.setMessage(msg);
        }
    }

    public void reverse(BiMap<String, String> translated) {
        try {
            GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
            List<ChatLine> chatLines = (List<ChatLine>) Reference.reverseTranslation.getChatLinesF().get(chat);
            for (int i = 0; i != chatLines.size(); i++) {
                ChatLine cl = chatLines.get(i);
                String str = cl.getChatComponent().getFormattedText().replaceAll("§r", "");
                String replace = translated.get(str);
                if (replace != null) {
                    chatLines.set(i, new ChatLine(cl.getUpdatedCounter(), new TextComponentString(replace), cl.getChatLineID()));
                }
            }
            chat.refreshChat();
        } catch (Exception e) {
            WynnLang.sendMessage("§4Ошибка: " + e.getMessage());
        }
    }
}
