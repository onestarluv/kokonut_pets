package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.MCPets;
import lombok.Getter;

import java.util.HashMap;

public class LanguageConfig extends AbstractConfig {

    public static LanguageConfig instance;

    @Getter
    private final HashMap<String, String> map = new HashMap<>();

    public static LanguageConfig getInstance() {
        if (instance == null)
            instance = new LanguageConfig();

        return instance;
    }

    /**
     * 에고버스: 문구는 {@link Language} 기본값(한국어)으로 고정한다. language.yml 은 만들지도 읽지도 않는다.
     */
    public void init() {
        reload();
    }

    @Override
    public void save() {
        // 파일 없음
    }

    @Override
    public void reload() {
        map.clear();
        if (Language.PET_INVENTORY_TITLE.getMessage().equals(Language.INVENTORY_PETS_MENU_INTERACTIONS.getMessage())) {
            MCPets.getLog().severe("Interaction menu and prime menu have the same name, which might lead to unexpected behaviors.");
        }
    }
}
