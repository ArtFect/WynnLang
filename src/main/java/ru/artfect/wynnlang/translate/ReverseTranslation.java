package ru.artfect.wynnlang.translate;

import com.google.common.collect.BiMap;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import ru.artfect.translates.TranslateType;
import ru.artfect.wynnlang.Reference;
import ru.artfect.wynnlang.WynnLang;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ReverseTranslation {
    public static boolean enabled = false;
    public static Field chatLinesF;
    public static HashMap<Class<? extends TranslateType>, BiMap<String, String>> translated = new HashMap<>();

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
        pressed = hey < 0 ? ouse.isButtonDown(key + 100) : Keyboard.isKeyDown(key)''

        if (pressed && !enabled) {
            enabled = true;
            reverse();
        } else if (!pressed && enabled) {
            enabled = false;
            reverse();
        }
    }

    public static void reverse() {
        try {
            for (Class<? extends TranslateType> tClass : translated.keySet()) {
                tClass.newInstance().reverse(translated.get(tClass));
                translated.put(tClass, translated.get(tClass).inverse());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            WynnLang.sendMessage("§4Не удалось восстановить оригинальные строки");
        }
    }
}
