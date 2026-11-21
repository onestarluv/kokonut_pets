package fr.nocsy.mcpets.modeler.bone;

import kr.toxicity.model.api.nms.ModelNametag;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class BetterModelNameTag implements AbstractNameTag {

    private final ModelNametag delegate;

    public BetterModelNameTag(ModelNametag delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setString(String string) {
        // null/빈 문자열이면 이름표를 비운다 (Kokonut_Model 은 빈 이름표를 보내지 않는다).
        delegate.component(string == null || string.isBlank() ? null : LegacyComponentSerializer.legacySection().deserialize(string));
    }

    @Override
    public void setVisible(boolean visible) {
        delegate.alwaysVisible(visible);
    }
}
