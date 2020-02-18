package ru.artfect.wynnlang.translate;

import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import ru.artfect.translates.Flipped;
import ru.artfect.wynnlang.Reference;
import ru.artfect.wynnlang.WynnLang;

import java.lang.reflect.Field;
import java.util.Map;

@Getter
public class ReverseTranslation {
    private boolean enabled = false;
    private Field chatLinesF;
    private Map<Flipped, BiMap<String, String>> translated = Maps.newHashMap();

    public ReverseTranslation() {
        chatLinesF = ReflectionHelper.findField(GuiNewChat.class, "chatLines", "field_146252_h");
        chatLinesF.setAccessible(true);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent e) {
        if (e.phase != Phase.START || !Reference.onWynncraft || !Reference.modEnabled) {
            return;
        }

        boolean pressed;
        int key = Reference.keyBindings[0].getKeyCode();
        pressed = key < 0 ? Mouse.isButtonDown(key + 100) : Keyboard.isKeyDown(key);

        if (pressed && !enabled) {
            enabled = true;
            reverse();
        } else if (!pressed && enabled) {
            enabled = false;
            reverse();
        }
    }

    public void reverse() {
        try {
            translated.keySet().forEach(flip -> {
                flip.reverse(translated.get(flip));
                translated.put(flip, translated.get(flip).inverse());
            });
        } catch (Exception e) {
            WynnLang.sendMessage("§4Не удалось восстановить оригинальные строки");
        }
    }
}
