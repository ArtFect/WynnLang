package ru.artfect.wynnlang;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import ru.artfect.translates.TranslateType;
import ru.artfect.wynnlang.command.RuCommand;
import ru.artfect.wynnlang.command.WynnLangCommand;
import ru.artfect.wynnlang.translate.MessageHandler;
import ru.artfect.wynnlang.translate.ReverseTranslation;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class WynnLang {
	public static HashMap<Class<? extends TranslateType>, HashMap<String, String>> common = new HashMap<>();
    public static HashMap<Class<? extends TranslateType>, HashMap<Pattern, String>> regex = new HashMap<>();

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        Reference.keyBindings[0] = new KeyBinding("Показ оригинальных строк", Keyboard.KEY_F8, "WynnLang");
        ClientRegistry.registerKeyBinding(Reference.keyBindings[0]);
    	
        MinecraftForge.EVENT_BUS.register(new Network());
        MinecraftForge.EVENT_BUS.register(new MessageHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException, InstantiationException, IllegalAccessException {
        ClientCommandHandler.instance.registerCommand(new WynnLangCommand());
        ClientCommandHandler.instance.registerCommand(new RuCommand());

        Config.loadConfigFromFile();
        StringLoader.load();
        Log.init();
        ReverseTranslation.init();
        UpdateManager.checkUpdate();
        Reference.ruChat = new RuChat();
        RuChat.startTimer();
    }

    public static void sendMessage(String message) {
    	Minecraft.getMinecraft().player.sendMessage(new TextComponentString(Reference.CHAT_PREFIX + " " + message));
    }
}