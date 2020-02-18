package ru.artfect.wynnlang;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;
import ru.artfect.translates.TranslateType;
import ru.artfect.wynnlang.command.RuCommand;
import ru.artfect.wynnlang.command.WynnLangCommand;
import ru.artfect.wynnlang.translate.MessageHandler;
import ru.artfect.wynnlang.translate.ReverseTranslation;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class WynnLang {
    public static Map<Class<? extends TranslateType>, HashMap<String, String>> common = new HashMap<>();
    public static Map<Class<? extends TranslateType>, HashMap<Pattern, String>> regex = new HashMap<>();

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        Reference.keyBindings[0] = new KeyBinding("Показ оригинальных строк", Keyboard.KEY_F8, "WynnLang");
        ClientRegistry.registerKeyBinding(Reference.keyBindings[0]);

        Reference.modFile = event.getSourceFile();

        MinecraftForge.EVENT_BUS.register(new Network());
        MinecraftForge.EVENT_BUS.register(new MessageHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException, InstantiationException, IllegalAccessException {
        new ReverseTranslation();
        ClientCommandHandler.instance.registerCommand(new WynnLangCommand(new UpdateManager()));
        ClientCommandHandler.instance.registerCommand(new RuCommand());

        Config.loadConfigFromFile();
        new Log();
        StringLoader.load();
        Reference.ruChat = new RuChat();
        RuChat.startTimer();
    }

    public static void sendMessage(String message) {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(Reference.CHAT_PREFIX + " " + message));
    }
}