package ru.artfect.translates;

import com.google.common.collect.BiMap;

public abstract class TranslateType {
    public abstract void translate();

    public abstract String getName();

    public abstract void reverse(BiMap<String, String> biMap);
}
