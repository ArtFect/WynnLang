package ru.artfect.translates;

import net.minecraft.network.Packet;

/**
 * @author func 18.02.2020
 */
public interface TranslatablePacket<T extends Packet> extends TranslateType {

    T translatePacket();
}
