package ru.artfect.translates;

import com.google.common.collect.BiMap;

/**
 * @author func 18.02.2020
 */
public interface Flipped extends TranslateType {

    void reverse(BiMap<String, String> biMap);
}
