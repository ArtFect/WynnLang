package ru.artfect.translates;

import com.google.common.collect.BiMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.EntityDataManager.DataEntry;
import org.apache.http.util.TextUtils;
import ru.artfect.wynnlang.StringUtil;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class Entity implements Flipped, Translatable {
    private List<DataEntry<?>> entityInfo;
    @Getter
    private static final String name = "ENTITY_NAME";

    public void translate() {
        if (entityInfo == null) {
            return;
        }
        for (int i = 0; i != entityInfo.size(); i++) {
            DataEntry<?> data = entityInfo.get(i);
            if (data.getKey().getId() == 2) {
                String replace = StringUtil.handleString(this, (String) data.getValue());
                if (!TextUtils.isEmpty(replace))
                    entityInfo.set(i, new DataEntry(data.getKey(), replace));
            }
        }
    }

    public void reverse(BiMap<String, String> translated) {
        Minecraft.getMinecraft().player.world.loadedEntityList.forEach(x -> {
            String replace = translated.get(x.getDisplayName().getFormattedText().replaceAll("§r", ""));
            if (TextUtils.isEmpty(replace))
                x.setCustomNameTag(replace);
        });
    }
}
