package ru.artfect.wynnlang;

import net.minecraft.client.settings.KeyBinding;

public class Reference {
    public static final String VERSION = "${version}";
    public static final String MOD_ID = "wynnlang";
    public static final String NAME = "WynnLang";
    public static final String SERVER = "52.15.207.104";
    public static final String CHAT_PREFIX = "§a[WL]";

    public static RuChat ruChat;
    public static boolean onWynncraft = false;
    public static boolean modEnabled = true;
    public static KeyBinding[] keyBindings = new KeyBinding[1];
}
