package ru.artfect.translates;

import com.google.common.collect.BiMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.EntityDataManager.DataEntry;
import ru.artfect.wynnlang.StringUtil;

import java.util.List;

public class Entity extends TranslateType {
    private List<DataEntry<?>> entityInfo;

    public Entity(List<DataEntry<?>> entityInfo) {
        this.entityInfo = entityInfo;
    }

    public Entity() {

    }

    public void translate() {
        if (entityInfo == null) {
            return;
        }
        for (int i = 0; i != entityInfo.size(); i++) {
            DataEntry<?> data = entityInfo.get(i);
            if (data.getKey().getId() == 2) {
                String str = (String) data.getValue();
                String replace = StringUtil.handleString(this, str);
                if (replace != null) {
                    entityInfo.set(i, new DataEntry(data.getKey(), replace));
                }
            }
        }
    }

    public String getName() {
        return "ENTITY_NAME";
    }

    public void reverse(BiMap<String, String> translated) {
        for (net.minecraft.entity.Entity en : Minecraft.getMinecraft().player.world.loadedEntityList) {
            String str = en.getDisplayName().getFormattedText().replaceAll("§r", "");
            String replace = translated.get(str);
            if (replace != null) {
                en.setCustomNameTag(replace);
            }
        }
    }
}
