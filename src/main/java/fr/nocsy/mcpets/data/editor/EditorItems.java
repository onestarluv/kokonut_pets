package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.utils.PDCTag;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.config.CategoryConfig;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.PetConfig;
import fr.nocsy.mcpets.data.config.PetFoodConfig;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum EditorItems {

    UNKNOWN(UNKNOWN(), null, null, null, null, false),
    FILLER(FILLER(), null, null, null, null, false),

    BACK_TO_GLOBAL_SELECTION(BACK_TO_ITEM("전체 메뉴"), null, null, null, EditorState.GLOBAL_EDITOR, false),
    BACK_TO_PET_SELECTION(BACK_TO_ITEM("펫 선택"), null, null, null, EditorState.PET_EDITOR, false),
    BACK_TO_PET_EDIT(BACK_TO_ITEM("펫 편집"), null, null, null, EditorState.PET_EDITOR_EDIT, false),
    BACK_TO_PET_LEVELS_EDIT(BACK_TO_ITEM("펫 레벨"), null, null, null, EditorState.PET_EDITOR_LEVELS, false),
    BACK_TO_PET_SKINS_EDIT(BACK_TO_ITEM("펫 스킨"), null, null, null, EditorState.PET_EDITOR_SKINS, false),
    BACK_TO_CATEGORIES_EDIT(BACK_TO_ITEM("카테고리"), null, null, null, EditorState.CATEGORY_EDITOR, false),
    BACK_TO_ITEM_EDITOR(BACK_TO_ITEM("아이템"), null, null, null, EditorState.ITEM_EDITOR, false),
    BACK_TO_PETFOOD_EDITOR(BACK_TO_ITEM("펫 먹이"), null, null, null, EditorState.PETFOOD_EDITOR, false),

    // Default selection menu
    CONFIG_EDITOR(CONFIG_EDITOR(), null, null, null, EditorState.CONFIG_EDITOR, false),
    PET_EDITOR(PET_EDITOR(), null, null, null, EditorState.PET_EDITOR, false),
    CATEGORY_EDITOR(CATEGORY_EDITOR(), null, null, null, EditorState.CATEGORY_EDITOR, false),
    ITEM_EDITOR(ITEM_EDITOR(), null, null, null, EditorState.ITEM_EDITOR, false),
    PETFOOD_EDITOR(PETFOOD_EDITOR(), null, null, null, EditorState.PETFOOD_EDITOR, false),

    // Config editor
    CONFIG_EDITOR_PREFIX(CONFIG_EDITOR_PREFIX(), "Prefix", "config", EditorExpectationType.STRING, null, true),
    CONFIG_EDITOR_DEFAULT_NAME(CONFIG_EDITOR_DEFAULT_NAME(), "DefaultName", "config", EditorExpectationType.STRING, null, true),
    CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES(CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES(), "UseDefaultMythicMobsNames", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME(CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME(), "OverrideDefaultName", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU(CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU(), "RightClickToOpenMenu", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU(CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU(), "LeftClickToOpenMenu", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_SNEAKMODE(CONFIG_EDITOR_SNEAKMODE(), "SneakMode", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_NAMEABLE(CONFIG_EDITOR_NAMEABLE(), "Nameable", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_MOUNTABLE(CONFIG_EDITOR_MOUNTABLE(), "Mountable", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_DISTANCE_TELEPORT(CONFIG_EDITOR_DISTANCE_TELEPORT(), "DistanceTeleport", "config", EditorExpectationType.FLOAT, null, true),
    CONFIG_EDITOR_MAX_NAME_LENGTH(CONFIG_EDITOR_MAX_NAME_LENGTH(), "MaxNameLenght", "config", EditorExpectationType.INT, null, true),
    CONFIG_EDITOR_INVENTORY_SIZE(CONFIG_EDITOR_INVENTORY_SIZE(), "InventorySize", "config", EditorExpectationType.INT, null, true),
    CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU(CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU(), "EnableClickBackToMenu", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON(CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON(), "ActivateBackMenuIcon", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_DISMOUNT_ON_DAMAGED(CONFIG_EDITOR_DISMOUNT_ON_DAMAGED(), "DismountOnDamaged", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK(CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK(), "DisableInventoryWhileHoldingSignalStick", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN(CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN(), "PercentHealthOnRespawn", "config", EditorExpectationType.FLOAT, null, true),
    CONFIG_EDITOR_AUTO_SAVE_DELAY(CONFIG_EDITOR_AUTO_SAVE_DELAY(), "AutoSaveDelay", "config", EditorExpectationType.INT, null, true),
    CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN(CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN(), "DefaultRespawnCooldown", "config", EditorExpectationType.INT, null, true),
    CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN(CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN(), "GlobalRespawnCooldown", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_GLOBAL_AUTORESPAWN(CONFIG_EDITOR_GLOBAL_AUTORESPAWN(), "AutoRespawn", "config", EditorExpectationType.BOOLEAN, null, true),

    // Pet editor
    PET_EDITOR_EDIT_PET(UNKNOWN(), null, null, EditorExpectationType.PET, null, false),
    PET_EDITOR_CREATE_NEW(CREATE_NEW_ITEM("펫", Material.MAGMA_CUBE_SPAWN_EGG), null, null, EditorExpectationType.PET_CREATE, null, false),
    PAGE_SELECTOR(PAGE_SELECTOR(), null, null, EditorExpectationType.PAGE_SELECTOR, null, false),

    PET_EDITOR_DELETE(DELETE("펫"), null, null, EditorExpectationType.PET_DELETE, null, false),
    PET_EDITOR_LEVELS(PET_EDITOR_LEVELS(), null, null, null, EditorState.PET_EDITOR_LEVELS, false),
    PET_EDITOR_SKINS(PET_EDITOR_SKINS(), null, null, null, EditorState.PET_EDITOR_SKINS, false),

    PET_EDITOR_ICON(PET_EDITOR_ICON(), "Icon.Raw", null, EditorExpectationType.ITEM, null, false),
    PET_EDITOR_MYTHICMOB(PET_EDITOR_MYTHICMOB(), "MythicMob", null, EditorExpectationType.MYTHICMOB, null, false),
    PET_EDITOR_PERMISSION(PET_EDITOR_PERMISSION(), "Permission", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_MOUNTABLE(PET_EDITOR_MOUNTABLE(), "Mountable", null, EditorExpectationType.BOOLEAN, null, true),
    PET_EDITOR_MOUNT_TYPE(PET_EDITOR_MOUNT_TYPE(), "MountType", null, EditorExpectationType.MOUNT_TYPE, null, true),
    PET_EDITOR_DESPAWN_ON_DISMOUNT(PET_EDITOR_DESPAWN_ON_DISMOUNT(), "DespawnOnDismount", null, EditorExpectationType.BOOLEAN, null, true),
    PET_EDITOR_AUTORIDE(PET_EDITOR_AUTORIDE(), "AutoRide", null, EditorExpectationType.BOOLEAN, null, true),
    PET_EDITOR_MOUNT_PERMISSION(PET_EDITOR_MOUNT_PERMISSION(), "MountPermission", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_DESPAWN_SKILL(PET_EDITOR_DESPAWN_SKILL(), "DespawnSkill", null, EditorExpectationType.SKILL, null, true),
    PET_EDITOR_DISTANCE(PET_EDITOR_DISTANCE(), "Distance", null, EditorExpectationType.FLOAT, null, true),
    PET_EDITOR_SPAWN_RANGE(PET_EDITOR_SPAWN_RANGE(), "SpawnRange", null, EditorExpectationType.FLOAT, null, true),
    PET_EDITOR_COMING_BACK_RANGE(PET_EDITOR_COMING_BACK_RANGE(), "ComingBackRange", null, EditorExpectationType.FLOAT, null, true),
    PET_EDITOR_INVENTORY_SIZE(PET_EDITOR_INVENTORY_SIZE(), "InventorySize", null, EditorExpectationType.INT, null, true),
    PET_EDITOR_TAMING_PROGRESS_SKILL(PET_EDITOR_TAMING_PROGRESS_SKILL(), "Taming.TamingProgressSkill", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_TAMING_FINISHED_SKILL(PET_EDITOR_TAMING_FINISHED_SKILL(), "Taming.TamingFinishedSkill", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_SIGNALS(PET_EDITOR_SIGNALS(), "Signals.Values", null, EditorExpectationType.STRING_LIST, null, true),
    PET_EDITOR_SIGNAL_STICK(PET_EDITOR_SIGNAL_STICK(), "Signals.Item.Raw", null, EditorExpectationType.ITEM, null, true),
    PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU(PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU(), "Signals.Item.GetFromMenu", null, EditorExpectationType.BOOLEAN, null, true),
    // Pet editor - Levels
    PET_EDITOR_EDIT_LEVEL(UNKNOWN(), null, null, EditorExpectationType.PET_LEVEL_EDIT, null, false),
    PET_EDITOR_EDIT_LEVEL_DELETE(DELETE("레벨"), null, null, EditorExpectationType.PET_LEVEL_DELETE, null, false),
    PET_EDITOR_LEVEL_CREATE_NEW(CREATE_NEW_ITEM("레벨", Material.EXPERIENCE_BOTTLE), null, null, EditorExpectationType.PET_LEVEL_CREATE, null, false),
    PET_EDITOR_EDIT_LEVEL_NAME(PET_EDITOR_LEVEL_NAME(), "Levels.%path%.Name", null, EditorExpectationType.STRING, null, false),
    PET_EDITOR_EDIT_LEVEL_EXP_THRESHOLD(PET_EDITOR_LEVEL_EXP_THRESHOLD(), "Levels.%path%.ExperienceThreshold", null, EditorExpectationType.POSITIVE_INT, null, false),
    PET_EDITOR_EDIT_LEVEL_MAX_HEALTH(PET_EDITOR_LEVEL_MAX_HEALTH(), "Levels.%path%.MaxHealth", null, EditorExpectationType.POSITIVE_INT, null, false),
    PET_EDITOR_EDIT_LEVEL_REGENERATION(PET_EDITOR_LEVEL_REGENERATION(), "Levels.%path%.Regeneration", null, EditorExpectationType.POSITIVE_FLOAT, null, false),
    PET_EDITOR_EDIT_LEVEL_RESISTANCE_MODIFIER(PET_EDITOR_LEVEL_RESISTANCE_MODIFIER(), "Levels.%path%.ResistanceModifier", null, EditorExpectationType.FLOAT, null, false),
    PET_EDITOR_EDIT_LEVEL_DAMAGE_MODIFIER(PET_EDITOR_LEVEL_DAMAGE_MODIFIER(), "Levels.%path%.DamageModifier", null, EditorExpectationType.FLOAT, null, false),
    PET_EDITOR_EDIT_LEVEL_POWER(PET_EDITOR_LEVEL_POWER(), "Levels.%path%.Power", null, EditorExpectationType.FLOAT, null, false),
    PET_EDITOR_EDIT_LEVEL_COOLDOWN_RESPAWN(PET_EDITOR_LEVEL_COOLDOWN_RESPAWN(), "Levels.%path%.Cooldowns.Respawn", null, EditorExpectationType.POSITIVE_INT, null, true),
    PET_EDITOR_EDIT_LEVEL_COOLDOWN_REVOKE(PET_EDITOR_LEVEL_COOLDOWN_REVOKE(), "Levels.%path%.Cooldowns.Revoke", null, EditorExpectationType.POSITIVE_INT, null, true),
    PET_EDITOR_EDIT_LEVEL_INVENTORY_EXTENSION(PET_EDITOR_LEVEL_INVENTORY_EXTENSION(), "Levels.%path%.InventoryExtension", null, EditorExpectationType.INVENTORY_SIZE, null, true),
    PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_TEXT(PET_EDITOR_LEVEL_ANNOUNCEMENT_TEXT(), "Levels.%path%.Announcement.Text", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_TYPE(PET_EDITOR_LEVEL_ANNOUNCEMENT_TYPE(), "Levels.%path%.Announcement.Type", null, EditorExpectationType.ANNOUNCEMENT_TYPE, null, true),
    PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_SKILL(PET_EDITOR_LEVEL_ANNOUNCEMENT_SKILL(), "Levels.%path%.Announcement.Skill", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_EDIT_LEVEL_EVOLUTION_PET_ID(PET_EDITOR_LEVEL_EVOLUTION_PET_ID(), "Levels.%path%.Evolution.PetId", null, EditorExpectationType.PET_ID, null, true),
    PET_EDITOR_EDIT_LEVEL_EVOLUTION_DELAY(PET_EDITOR_LEVEL_EVOLUTION_DELAY(), "Levels.%path%.Evolution.DelayBeforeEvolution", null, EditorExpectationType.POSITIVE_INT, null, true),
    PET_EDITOR_EDIT_LEVEL_EVOLUTION_REMOVE_ACCESS(PET_EDITOR_LEVEL_EVOLUTION_REMOVE_ACCESS(), "Levels.%path%.Evolution.RemoveAccess", null, EditorExpectationType.BOOLEAN, null, true),
    // Pet editor - Skins
    PET_EDITOR_EDIT_SKIN(UNKNOWN(), null, null, EditorExpectationType.PET_SKIN_EDIT, null, false),
    PET_EDITOR_EDIT_SKIN_DELETE(DELETE("스킨"), null, null, EditorExpectationType.PET_SKIN_DELETE, null, false),
    PET_EDITOR_SKIN_CREATE_NEW(CREATE_NEW_ITEM("스킨", Material.LEATHER), null, null, EditorExpectationType.PET_SKIN_CREATE, null, false),
    PET_EDITOR_EDIT_SKIN_ICON(UNKNOWN(), "%path%.Icon.Raw", null, EditorExpectationType.ITEM, null, false),
    PET_EDITOR_EDIT_SKIN_MYTHICMOB(PET_EDITOR_SKIN_MYTHICMOB(), "%path%.MythicMob", null, EditorExpectationType.MYTHICMOB, null, false),
    PET_EDITOR_EDIT_SKIN_PERMISSION(PET_EDITOR_SKIN_PERMISSION(), "%path%.Permission", null, EditorExpectationType.STRING, null, true),

    // Category editor
    CATEGORY_EDITOR_EDIT_CATEGORY(UNKNOWN(), null, null, EditorExpectationType.CATEGORY_EDIT, null, false),

    CATEGORY_EDITOR_CATEGORY_CREATE(CREATE_NEW_ITEM("카테고리", Material.KNOWLEDGE_BOOK), null, null, EditorExpectationType.CATEGORY_CREATE, null, false),
    CATEGORY_EDITOR_CATEGORY_DELETE(DELETE("카테고리"), null, null, EditorExpectationType.CATEGORY_DELETE, null, false),

    CATEGORY_EDITOR_CATEGORY_EDIT_ID(CATEGORY_EDITOR_CATEGORY_EDIT_ID(), "Id", null, EditorExpectationType.STRING, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_ICON(UNKNOWN(), "Icon", null, EditorExpectationType.ITEM, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_ICON_NAME(CATEGORY_EDITOR_CATEGORY_EDIT_ICON_NAME(), "IconName", null, EditorExpectationType.STRING, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_TITLE_NAME(CATEGORY_EDITOR_CATEGORY_EDIT_TITLE_NAME(), "DisplayName", null, EditorExpectationType.STRING, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_DEFAULT_CATEGORY(CATEGORY_EDITOR_CATEGORY_EDIT_DEFAULT_CATEGORY(), "DefaultCategory", null, EditorExpectationType.BOOLEAN, null, true),
    CATEGORY_EDITOR_CATEGORY_EDIT_EXCLUDED_CATEGORIES(CATEGORY_EDITOR_CATEGORY_EDIT_EXCLUDED_CATEGORIES(), "ExcludedCategories", null, EditorExpectationType.STRING_LIST, null, true),
    CATEGORY_EDITOR_CATEGORY_EDIT_PET_ADD(CATEGORY_EDITOR_CATEGORY_EDIT_PET_ADD(), "Pets", null, EditorExpectationType.CATEGORY_PET_LIST_ADD, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_PET_REMOVE(CATEGORY_EDITOR_CATEGORY_EDIT_PET_REMOVE(), "Pets", null, EditorExpectationType.CATEGORY_PET_LIST_REMOVE, null, false),

    // Items editor
    ITEMS_EDIT(UNKNOWN(), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM_EDIT, null, false),
    ITEMS_DELETE(DELETE("아이템"), null, null, EditorExpectationType.ITEM_DELETE, null, false),
    ITEMS_CREATE(CREATE_NEW_ITEM("아이템", Material.EMERALD), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM_CREATE, null, false),
    ITEMS_EDIT_ID(ITEMS_EDIT_ID(), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM_SECTION_ID, null, false),
    ITEMS_EDIT_ITEM(UNKNOWN(), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM, null, false),

    // Pet food
    PETFOOD_EDITOR_EDIT(UNKNOWN(), null, "petfoods", EditorExpectationType.PETFOOD_EDIT, null, false),
    PETFOOD_EDITOR_EDIT_CREATE(CREATE_NEW_ITEM("펫 먹이", Material.COOKED_CHICKEN), null, "petfoods", EditorExpectationType.PETFOOD_CREATE, null, false),

    PETFOOD_EDITOR_EDIT_DELETE(DELETE("펫 먹이"), null, "petfoods", EditorExpectationType.PETFOOD_DELETE, null, false),
    PETFOOD_EDITOR_EDIT_ID(PETFOOD_EDITOR_EDIT_ID(), "%path%", "petfoods", EditorExpectationType.PETFOOD_ID, null, false),
    PETFOOD_EDITOR_EDIT_ITEM_ID(UNKNOWN(), "%path%.ItemId", "petfoods", EditorExpectationType.ITEM_ID_OR_MATERIAL, null, false),
    PETFOOD_EDITOR_EDIT_TYPE(PETFOOD_EDITOR_EDIT_TYPE(), "%path%.Type", "petfoods", EditorExpectationType.PETFOOD_TYPE, null, false),
    PETFOOD_EDITOR_EDIT_POWER(PETFOOD_EDITOR_EDIT_POWER(), "%path%.Power", "petfoods", EditorExpectationType.FLOAT, null, false),
    PETFOOD_EDITOR_EDIT_DURATION(PETFOOD_EDITOR_EDIT_DURATION(), "%path%.Duration", "petfoods", EditorExpectationType.INT, null, false),
    PETFOOD_EDITOR_EDIT_OPERATOR(PETFOOD_EDITOR_EDIT_OPERATOR(), "%path%.Operator", "petfoods", EditorExpectationType.OPERATOR_TYPE, null, true),
    PETFOOD_EDITOR_EDIT_SIGNAL(PETFOOD_EDITOR_EDIT_SIGNAL(), "%path%.Signal", "petfoods", EditorExpectationType.STRING, null, true),
    PETFOOD_EDITOR_EDIT_PETS_ADD(PETFOOD_EDITOR_EDIT_PETS_ADD(), "%path%.Pets", "petfoods", EditorExpectationType.PETFOOD_PET_LIST_ADD, null, false),
    PETFOOD_EDITOR_EDIT_PETS_REMOVE(PETFOOD_EDITOR_EDIT_PETS_REMOVE(), "%path%.Pets", "petfoods", EditorExpectationType.PETFOOD_PET_LIST_REMOVE, null, false),

    PETFOOD_EDITOR_EDIT_EVOLUTION(PETFOOD_EDITOR_EDIT_EVOLUTION(), "%path%.Evolution", "petfoods", EditorExpectationType.PET_ID, null, true),
    PETFOOD_EDITOR_EDIT_EXP_THRESHOLD(PETFOOD_EDITOR_EDIT_EXP_THRESHOLD(), "%path%.ExperienceThreshold", "petfoods", EditorExpectationType.POSITIVE_INT, null, true),
    PETFOOD_EDITOR_EDIT_DELAY(PETFOOD_EDITOR_EDIT_DELAY(), "%path%.DelayBeforeEvolution", "petfoods", EditorExpectationType.POSITIVE_INT, null, true),

    PETFOOD_EDITOR_EDIT_PERMISSION(PETFOOD_EDITOR_EDIT_PERMISSION(), "%path%.Permission", "petfoods", EditorExpectationType.STRING, null, true),
    PETFOOD_EDITOR_EDIT_UNLOCKED_PET(PETFOOD_EDITOR_EDIT_UNLOCKED_PET(), "%path%.UnlockPet", "petfoods", EditorExpectationType.PET_ID, null, true);

    private final static String editorTag = "MCPets:Editor:";
    @Getter
    private static ArrayList<String> cachedDeleted = new ArrayList<>();

    public static String RESET_VALUE_TAG = "ResetValue°897698575";

    @Getter
    private String id;
    @Setter
    private ItemStack item;
    @Getter
    private String filePath;
    @Getter
    private String inputFilePath;
    @Getter
    private String variablePath;
    private String variablePathPlaceholder;
    @Getter
    private Object value;
    @Getter
    private EditorExpectationType type;
    @Getter
    private EditorState nextState;
    @Getter
    private boolean resetable;

    EditorItems(final ItemStack item, final String variablePath, final String filePath, final EditorExpectationType type, final EditorState nextState, final boolean resetable) {
        this.id = this.name().toUpperCase();
        this.item = item;
        this.inputFilePath = filePath;
        this.filePath = fr.nocsy.mcpets.data.config.AbstractConfig.getPath() + filePath + ".yml";
        this.variablePath = variablePath;
        this.variablePathPlaceholder = "";
        this.type = type;
        this.nextState = nextState;
        this.resetable = resetable;

        refreshData();
    }

    public void refreshData() {
        if (filePath != null && variablePath != null) {
            final File file = new File(this.filePath);
            final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            this.value = config.get(variablePath.replace("%path%", variablePathPlaceholder));
        }
    }

    public boolean is(final EditorItems other) {
        return other.getId().equals(this.getId());
    }

    public EditorItems setFilePath(final String path) {
        this.filePath = path;
        refreshData();
        return this;
    }

    public EditorItems replaceVariablePath(final String pathPlaceholder) {
        this.variablePathPlaceholder = pathPlaceholder;
        refreshData();
        return this;
    }

    public boolean save(final Player creator) {
        if (this.value == null)
            return false;

        if (this.getType().equals(EditorExpectationType.PET_CREATE)) {
            final String illegalCharacters = "#%<>&*{}?/\\$§+!`|'\"=:@.";
            for (final char character : illegalCharacters.toCharArray()) {
                this.value = this.value.toString().replace(""+character, "");
            }
            this.value = this.value.toString().replace(" ", "_");
            // Create the pet config and add it to the pet objects for editing
            final PetConfig petConfig = new PetConfig("Pets/", this.value.toString() + ".yml");
            Pet.getObjectPets().add(petConfig.getPet());
            return true;
        }
        else if (this.getType().equals(EditorExpectationType.CATEGORY_PET_LIST_ADD) ||
                this.getType().equals(EditorExpectationType.CATEGORY_PET_LIST_REMOVE)) {
            final Pet pet = Pet.getFromId(this.value + "");
            if (pet == null)
                return false;

            final EditorEditing editing = EditorEditing.get(creator);
            final CategoryConfig config = CategoryConfig.getMapping().get(editing.getMappedId());
            if (this.getType().equals(EditorExpectationType.CATEGORY_PET_LIST_ADD)) {
                config.addPet(pet);
            }
            else {
                config.removePet(pet);
            }
            return true;
        }
        else if (this.getType().equals(EditorExpectationType.PETFOOD_PET_LIST_ADD) ||
                this.getType().equals(EditorExpectationType.PETFOOD_PET_LIST_REMOVE)) {
            final Pet pet = Pet.getFromId(this.value + "");
            if (pet == null)
                return false;

            final EditorEditing editing = EditorEditing.get(creator);
            final String key = editing.getMappedId();
            final PetFoodConfig config = PetFoodConfig.getInstance();
            if (this.getType().equals(EditorExpectationType.PETFOOD_PET_LIST_ADD)) {
                config.addPet(key, pet.getId());
            }
            else {
                config.removePet(key, pet.getId());
            }
            return true;
        }

        final File file = new File(filePath);
        final FileConfiguration config = YamlConfiguration.loadConfiguration(file);


        if (this.getType().equals(EditorExpectationType.ITEM_SECTION_ID)) {
            final EditorEditing editing = EditorEditing.get(creator);
            final String itemId = editing.getMappedId();

            final ItemStack item = config.getItemStack(itemId);

            config.set(itemId, null);
            config.set(this.value.toString(), item);

            try {
                config.save(file);

                editing.setMappedId(this.value.toString());
                ItemsListConfig.reloadInstance();
                return true;
            }
            catch (final IOException ex) {
                return false;
            }
        }
        else if (this.getType().equals(EditorExpectationType.PETFOOD_ID)) {
            final EditorEditing editing = EditorEditing.get(creator);
            final PetFood petFood = PetFood.getFromId(editing.getMappedId());

            PetFoodConfig.getInstance().changePetFoodKey(petFood, this.value.toString());
            editing.setMappedId(this.value.toString());
            return true;
        }
        else {
            if (this.value.equals(RESET_VALUE_TAG) && resetable)
                this.value = null;
            config.set(variablePath.replace("%path%", variablePathPlaceholder), this.value);
        }

        try {
            config.save(file);
        }
        catch (final IOException ignored) {
            return false;
        }
        return true;
    }


    public void toggleBooleanValue() {
        if (this.type == null)
            return;
        if (value == null && this.type.equals(EditorExpectationType.BOOLEAN)) {
            value = true;
        }
        else if (value != null && this.type.equals(EditorExpectationType.BOOLEAN)) {
            value = !(Boolean) value;
        }
    }

    public ItemStack getItem() {
        final ItemStack it = item.clone();
        final ItemMeta meta = it.getItemMeta();
        if (it.getType().equals(Material.FILLED_MAP))
            it.setType(Material.MAP);
        PDCTag.set(meta, editorTag + getId());

        // Basically, we are replacing the placeholder for the value within the lores
        final List<String> lores = meta.getLore();
        final ArrayList<String> newLores = new ArrayList<>();
        if (lores != null) {
            for (final String lore : lores) {
                if (lore.contains("%value%") && value != null && value instanceof List) {
                    newLores.add(lore.replace("%value%", ""));
                    final List<String> valueAsList = (List<String>) value;
                    if (!valueAsList.isEmpty()) {
                        for (final String entry : valueAsList) {
                            newLores.add("§7 - §e" + entry);
                        }
                    }
                    else {
                        newLores.add("§c비어 있음");
                    }

                }
                else {
                    String valueStr = value == null ? "§6기본값 (설정 안 됨)" : value.toString();

                    if (value == null && this.type == EditorExpectationType.BOOLEAN)
                        valueStr = "false";

                    // Just some cute formatting for lores
                    if (valueStr.equalsIgnoreCase("true"))
                        valueStr = "§a" + value;
                    else if (valueStr.equalsIgnoreCase("false"))
                        valueStr = "§c" + false;

                    if (Utils.isNumeric(valueStr))
                        valueStr = "§b" + valueStr;

                    newLores.add(lore.replace("%value%", valueStr));
                }
            }
        }

        if (resetable) {
            newLores.add(" ");
            newLores.add("§cSHIFT + 클릭§7하면 값을 §c초기화§7해요.");
        }

        meta.setLore(newLores);

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        it.setItemMeta(meta);
        return it;
    }

    public static EditorItems getFromItemstack(final ItemStack it) {
        if (it == null || !it.hasItemMeta())
            return null;

        final String localName = PDCTag.get(it.getItemMeta());
        // Item does not have the editor tag, so it's not an editor item
        if (localName == null || !localName.contains(editorTag))
            return null;

        final String id = localName.replace(editorTag, "");

        return Arrays.stream(EditorItems.values()).filter(editorItems -> editorItems.getId().equals(id)).findFirst().orElse(null);
    }

    public EditorItems setValue(final Object any) {
        this.value = any;
        return this;
    }

    /**
     * Items builder methods
     */
    private static ItemStack UNKNOWN() {
        final ItemStack it = new ItemStack(Material.BARRIER);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§c알 수 없는 아이템");
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack FILLER() {
        final ItemStack it = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§0");
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack BACK_TO_ITEM(final String where) {
        final ItemStack it = new ItemStack(Material.PAPER);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§c뒤로: " + where);

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7클릭하면 " + where + " 화면으로 돌아가요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * MENU SELECTOR ICONS
     */
    private static ItemStack CONFIG_EDITOR() {
        final ItemStack it = new ItemStack(Material.MOJANG_BANNER_PATTERN);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6설정 편집");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7클릭하면 설정 항목을 편집해요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR() {
        final ItemStack it = new ItemStack(Material.MAGMA_CUBE_SPAWN_EGG);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6펫 편집");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7클릭하면 펫을 편집하거나 만들어요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6카테고리 편집");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7클릭하면 카테고리를 편집하거나 만들어요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack ITEM_EDITOR() {
        final ItemStack it = new ItemStack(Material.EMERALD);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6아이템 편집");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7클릭하면 아이템을 편집하거나 추가해요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR() {
        final ItemStack it = new ItemStack(Material.COOKED_CHICKEN);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6펫 먹이 편집");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7클릭하면 펫 먹이를 편집하거나 추가해요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * CONFIG EDIT ICONS
     */
    private static ItemStack CONFIG_EDITOR_PREFIX() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6접두사");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7클릭하면 플러그인 메시지 접두사를 편집해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DEFAULT_NAME() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6기본 펫 이름");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7이름을 정하지 않은 펫에게");
        lores.add("§7붙는 이름이에요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6MythicMobs 이름 사용");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7MythicMobs에 정한 이름을");
        lores.add("§7기본 이름으로 보여 줄지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6기본 이름 덮어쓰기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7이름을 비워 두었을 때 펫 이름 변경이");
        lores.add("§7기본 이름을 덮어쓸지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6우클릭으로 메뉴 열기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫을 우클릭하면 메뉴를 열지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6좌클릭으로 메뉴 열기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫을 좌클릭하면 메뉴를 열지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_SNEAKMODE() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6웅크려야 메뉴 열기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫과 상호작용해 메뉴를 열 때");
        lores.add("§7웅크리기가 필요한지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_NAMEABLE() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6이름 짓기 허용");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7모든 펫에 이름을 붙일 수 있는지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_MOUNTABLE() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6기본 이름 덮어쓰기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7기능이 켜져 있고 펫에 설정돼 있을 때");
        lores.add("§7모든 펫을 기본으로 탈 수 있게");
        lores.add("§7할지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DISTANCE_TELEPORT() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6순간이동 거리");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7주인과 이 거리보다 멀어지면");
        lores.add("§7펫이 주인 곁으로 순간이동해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_MAX_NAME_LENGTH() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6이름 최대 길이");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫 이름에 쓸 수 있는");
        lores.add("§7최대 글자 수예요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_INVENTORY_SIZE() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6소환 메뉴 크기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7소환 메뉴 칸 수를 정해요.");
        lores.add("§7-1이면 펫 수에 맞춰 자동으로 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6바깥 클릭으로 카테고리 돌아가기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7카테고리 메뉴 바깥을 클릭하면");
        lores.add("§7카테고리 선택으로 돌아갈지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6메뉴로 돌아가기 아이콘");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7상호작용 메뉴에 \"메뉴로 돌아가기\"");
        lores.add("§7아이콘을 보여 줄지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DISMOUNT_ON_DAMAGED() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6피해를 입으면 내리기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7탄 상태에서 피해를 입으면");
        lores.add("§7펫에서 내릴지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6부활 시 체력 비율");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7쓰러진 펫이 부활할 때 회복하는");
        lores.add("§7체력 비율이에요 (성장형 펫).");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_AUTO_SAVE_DELAY() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6DB 자동 저장 주기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7데이터베이스를 주기적으로 저장하는");
        lores.add("§7간격이에요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6기본 부활 대기 시간");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7쓰러진 펫이 다시 살아나기까지의");
        lores.add("§7기본 대기 시간이에요 (성장형 펫).");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6공통 부활 대기 시간");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7대기 시간 동안 다른 펫도 전부");
        lores.add("§7소환할 수 없게 할지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_GLOBAL_AUTORESPAWN() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6자동 부활");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫이 쓰러지면 대기 시간이 끝난 뒤");
        lores.add("§7자동으로 주인 곁에 다시 소환할지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6명령 막대 들고 있을 때 가방 막기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7명령 막대로 펫을 클릭할 때");
        lores.add("§7펫 가방을 열 수 있는지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * PET EDITOR icons
     */
    public EditorItems setupPetIcon(final String petId) {
        final Pet pet = PetConfig.loadConfigPet(petId);

        ItemStack it = pet.getIcon().clone();
        if (value != null && value instanceof ItemStack) {
            it = ((ItemStack) value).clone();
        }
        final ItemMeta meta = it.getItemMeta();

        List<String> og_lores = it.getItemMeta().getLore();
        if (og_lores == null)
            og_lores = new ArrayList<>();

        final ArrayList<String> lores = (ArrayList<String>) og_lores;
        lores.add(" ");
        lores.add("§e클릭하면 이 펫을 편집해요.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        return this;
    }

    public EditorItems setupPetIconEdit(final String petId) {
        final Pet pet = PetConfig.loadConfigPet(petId);
        ItemStack it = pet.getIcon().clone();
        if (value != null && value instanceof ItemStack) {
            it = (ItemStack) ((ItemStack) value).clone();
        }
        final ItemMeta meta = it.getItemMeta();

        List<String> og_lores = it.getItemMeta().getLore();
        if (og_lores == null)
            og_lores = new ArrayList<>();

        if (og_lores.contains("§e아이템을 들고 이 아이콘을 클릭하면"))
            return this;

        final ArrayList<String> lores = (ArrayList<String>) og_lores;
        lores.add(" ");
        lores.add("§e아이템을 들고 이 아이콘을 클릭하면");
        lores.add("§e펫 아이콘을 바꿔요.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        return this;
    }

    public EditorItems setupSignalStickItem(final String petId) {
        final Pet pet = PetConfig.loadConfigPet(petId);
        ItemStack it = pet.getSignalStick().clone();
        if (value != null && value instanceof ItemStack) {
            it = (ItemStack) ((ItemStack) value).clone();
        }
        final ItemMeta meta = it.getItemMeta();
        if (meta.getDisplayName().equals("§c정해지지 않음"))
            meta.setDisplayName("§6명령 막대");

        List<String> og_lores = it.getItemMeta().getLore();
        if (og_lores == null)
            og_lores = new ArrayList<>();

        final ArrayList<String> lores = (ArrayList<String>) og_lores;
        lores.add(" ");
        lores.add("§e아이템을 들고 이 아이콘을 클릭하면");
        lores.add("§e명령 막대 아이템을 바꿔요.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        return this;
    }

    private static ItemStack PAGE_SELECTOR() {
        final ItemStack it = new ItemStack(Material.ARROW);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§a페이지 이동");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§c좌클릭§7: 이전 페이지");
        lores.add("§a우클릭§7: 다음 페이지");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CREATE_NEW_ITEM(final String what, final Material type) {
        final ItemStack it = new ItemStack(type);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§a새 " + what + " 만들기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7클릭하면 새 " + what + "을(를) 만들어요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack DELETE(final String what) {
        final ItemStack it = new ItemStack(Material.BARRIER);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§c" + what + " 삭제");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§cSHIFT§7 + 클릭하면 " + what + "을(를) 삭제해요.");
        lores.add(" ");
        lores.add("§c§l주의: 되돌릴 수 없어요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MYTHICMOB() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6MythicMob");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫으로 쓸 MythicMob을 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_PERMISSION() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6권한");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7이 펫을 쓸 수 있게 해 주는");
        lores.add("§7권한을 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MOUNTABLE() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6탈 수 있음");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫을 탈 수 있는지 정해요.");
        lores.add("§7Blockbench 모델에 \"mount\" 본이");
        lores.add("§7있어야 해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MOUNT_TYPE() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6탈것 종류");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫을 탈 때의 이동 방식을 정해요.");
        lores.add("§7탈 수 있는 펫에만 적용돼요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_DESPAWN_ON_DISMOUNT() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6내리면 사라지기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7플레이어가 펫에서 내리면");
        lores.add("§7펫을 회수할지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_AUTORIDE() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6자동 탑승");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫을 소환하면 플레이어가");
        lores.add("§7자동으로 올라탈지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MOUNT_PERMISSION() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6탑승 권한");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7탑승이 켜져 있을 때 펫에 올라탈 수");
        lores.add("§7있게 해 주는 권한을 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_DESPAWN_SKILL() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6회수 스킬");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫이 사라질 때 쓸 스킬을 정해요 (선택).");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_DISTANCE() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6돌아오는 거리");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7주인과 이 거리보다 멀어지면");
        lores.add("§7펫이 주인에게 돌아와요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SPAWN_RANGE() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6소환 범위");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫이 소환될 수 있는");
        lores.add("§7반경이에요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_COMING_BACK_RANGE() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6멈추는 거리");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7주인에게 충분히 가까워졌을 때");
        lores.add("§7펫이 멈춰 서는 거리예요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_INVENTORY_SIZE() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6가방 크기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫 가방의 칸 수를 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_TAMING_PROGRESS_SKILL() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6길들이기 - 진행 스킬");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7플레이어가 펫을 길들이는");
        lores.add("§7동안 쓸 스킬을 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_TAMING_FINISHED_SKILL() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6길들이기 - 완료 스킬");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7플레이어가 펫을 길들이는");
        lores.add("§7길들이기를 마쳤을 때 쓸 스킬이에요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_ICON() {
        final ItemStack it = new ItemStack(Material.END_CRYSTAL);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6MythicMob");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫을 나타내는 아이콘을 정해요.");
        lores.add(" ");
        lores.add("§7인벤토리의 아이템을 들고 클릭하면");
        lores.add("§7지금 아이템을 바꿔요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SIGNALS() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6명령 목록");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7명령 막대로 내릴 수 있는");
        lores.add("§7명령 목록을 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SIGNAL_STICK() {
        final ItemStack it = new ItemStack(Material.BLAZE_ROD);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6명령 막대");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7명령 막대 아이템을 정해요.");
        lores.add(" ");
        lores.add("§7인벤토리의 아이템을 들고 클릭하면");
        lores.add("§7지금 아이템을 바꿔요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6메뉴에서 명령 막대 받기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7상호작용 메뉴에서 명령 막대를");
        lores.add("§7바로 받을 수 있는지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SKINS() {
        final ItemStack it = new ItemStack(Material.LEATHER);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6스킨");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7이 펫의 스킨을 추가하거나 삭제해요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVELS() {
        final ItemStack it = new ItemStack(Material.EXPERIENCE_BOTTLE);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6성장형 펫 기능");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7성장형 펫 기능을 추가하거나 편집해요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * PET EDITOR LEVEL icon
     */
    public EditorItems setupPetLevelIcon(final String petId, final String levelId) {
        final Pet pet = PetConfig.loadConfigPet(petId);
        final PetLevel level = pet.getPetLevels().stream().filter(petLevel -> petLevel.getLevelId().equals(levelId)).findFirst().orElse(null);

        final ItemStack it = new ItemStack(Material.EXPERIENCE_BOTTLE);
        final ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§a" + level.getLevelName());
        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7필요 경험치: §a" + level.getExpThreshold());
        lores.add(" ");
        lores.add("§e클릭하면 이 레벨을 편집해요.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = level;

        return this;
    }

    private static ItemStack PET_EDITOR_LEVEL_NAME() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6레벨 이름");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7레벨에 보여 줄 이름을 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EXP_THRESHOLD() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6필요 경험치");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫이 이 레벨이 되는 데");
        lores.add("§7필요한 최소 경험치예요.");
        lores.add("§c첫 레벨은 0 XP에서 시작해야 해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_MAX_HEALTH() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6최대 체력");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫의 최대 체력을 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_REGENERATION() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6체력 회복");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7시간에 따라 회복하는");
        lores.add("§7체력이에요. §c(체력/초)");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_RESISTANCE_MODIFIER() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6저항 배율");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7받는 피해를 이 값으로");
        lores.add("§7나눠요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_DAMAGE_MODIFIER() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6피해 배율");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫이 주는 피해에");
        lores.add("§7곱하는 값이에요.");
        lores.add("§c자동으로 적용되지 않아요. MythicMobs에서");
        lores.add("§c플레이스홀더로 써야 해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_POWER() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6위력 배율");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫 스킬 위력에");
        lores.add("§7곱하는 값이에요.");
        lores.add("§c자동으로 적용되지 않아요. MythicMobs에서");
        lores.add("§c플레이스홀더로 써야 해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_COOLDOWN_RESPAWN() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6대기 시간 - 부활");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫을 다시 소환할 수 있기까지의");
        lores.add("§7시간이에요 (쓰러진 뒤).");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_COOLDOWN_REVOKE() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6대기 시간 - 회수");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫을 다시 소환할 수 있기까지의");
        lores.add("§7시간이에요 (회수한 뒤).");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_INVENTORY_EXTENSION() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6가방 확장");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7이 레벨에서 펫 가방에");
        lores.add("§7추가로 열리는 칸 수예요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_ANNOUNCEMENT_TEXT() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6알림 - 문구");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫이 성장했을 때");
        lores.add("§7보여 줄 문구예요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_ANNOUNCEMENT_TYPE() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6알림 - 방식");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7알림을 보여 줄 방식을 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_ANNOUNCEMENT_SKILL() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6알림 - 스킬");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫이 성장했을 때");
        lores.add("§7보여 줄 문구예요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EVOLUTION_PET_ID() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6진화 - 펫 ID");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7진화하면 바뀔 펫의 ID예요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EVOLUTION_DELAY() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6진화 - 지연");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7진화가 일어나기 전 기다리는");
        lores.add("§7시간이에요 (스킬 길이 등, 틱 단위).");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EVOLUTION_REMOVE_ACCESS() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6진화 - 이전 펫 권한 제거");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7진화할 때 이전 펫 권한을");
        lores.add("§7없앨지 정해요 (켜 두기를 권장해요).");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * PET EDITOR SKIN icon
     */
    public EditorItems setupSkinIcon(final String petId, final String skinId) {

        final Pet pet = PetConfig.loadConfigPet(petId);
        final PetSkin skin = PetSkin.getSkins(pet).stream().filter(petSkin -> petSkin.getPathId().equals(skinId)).findFirst().orElse(null);

        ItemStack it = new ItemStack(Material.LEATHER);
        if (skin.getIcon() != null)
            it = skin.getIcon().clone();

        final ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§6스킨: §e" + skin.getMythicMobId());
        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§e클릭하면 이 스킨을 편집해요.");

        meta.setLore(lores);

        it.setItemMeta(meta);

        this.item = it;

        this.value = skin;

        return this;
    }

    public EditorItems setupEditSkinIcon(final String petId, final String skinId) {

        final Pet pet = PetConfig.loadConfigPet(petId);
        final PetSkin skin = PetSkin.getSkins(pet).stream().filter(petSkin -> petSkin.getPathId().equals(skinId)).findFirst().orElse(null);

        ItemStack it = new ItemStack(Material.LEATHER);
        if (skin.getIcon() != null)
            it = skin.getIcon().clone();

        final ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§6스킨: §e" + skin.getMythicMobId());
        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§e아이템을 들고 클릭하면");
        lores.add("§e스킨 아이콘을 바꿔요.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = skin;

        return this;
    }

    private static ItemStack PET_EDITOR_SKIN_MYTHICMOB() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6스킨 - MythicMob");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7스킨으로 바꿔 쓸 MythicMob을 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SKIN_PERMISSION() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6스킨 - 권한");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7이 스킨을 쓸 수 있게");
        lores.add("§7해 주는 권한이에요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }


    /**
     * Category icons
     */
    public EditorItems setupCategoryIcon(final String categoryId) {
        final Category category = CategoryConfig.loadConfigCategory(categoryId);
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        if (category.getIcon() != null)
            it = category.getIcon().clone();

        final ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§6카테고리: §e" + category.getIconName());
        final ArrayList<String> lores = new ArrayList<>();

        lores.add("§e클릭하면 이 카테고리를 편집해요.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = category;

        return this;
    }


    public EditorItems setupEditCategoryIcon(final String categoryId) {
        final Category category = CategoryConfig.loadConfigCategory(categoryId);
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        if (category.getIcon() != null)
            it = category.getIcon().clone();

        final ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§6카테고리: §e" + category.getIconName());
        final ArrayList<String> lores = new ArrayList<>();

        lores.add("§a제외한 카테고리:");
        if (category.getExcludedCategoriesId().size() == 0)
            lores.add("§7- §6없음");
        for(final String excludedCategoryId : category.getExcludedCategoriesId())
            lores.add("§7- " + excludedCategoryId);

        lores.add(" ");

        if (category.isDefaultCategory()) {
            lores.add("§a모든 펫 포함 §7(기본 카테고리)");
        }
        else {
            lores.add("§a포함된 펫:");
            if (category.getPets().size() == 0)
                lores.add("§7- §6없음");
            for(final Pet pet : category.getPets())
                lores.add(" §7 - " + pet.getId());
        }

        lores.add(" ");

        lores.add("§e아이템을 들고 클릭하면");
        lores.add("§e카테고리 아이콘을 바꿔요.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = category;

        return this;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_ID() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6카테고리 ID");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7클릭하면 카테고리 ID를 편집해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_DEFAULT_CATEGORY() {
        final ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6기본 카테고리");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7(선택) 모든 펫을 기본으로");
        lores.add("§7이 카테고리에 넣을지 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }


    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_EXCLUDED_CATEGORIES() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6제외할 카테고리");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§a(선택)§7 지정한 카테고리의 펫을");
        lores.add("§7모두 빼요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_PET_ADD() {
        final ItemStack it = new ItemStack(Material.GOLD_INGOT);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§a펫 §6추가");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7카테고리에 펫을 넣어요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_PET_REMOVE() {
        final ItemStack it = new ItemStack(Material.NETHER_BRICK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§c펫 §6빼기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7카테고리에서 펫을 빼요.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_TITLE_NAME() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6카테고리 메뉴 제목");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7카테고리 메뉴 창에");
        lores.add("§7보일 제목이에요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_ICON_NAME() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6카테고리 아이콘 이름");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7메뉴에서 카테고리 아이콘에");
        lores.add("§7보일 이름이에요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * Items editor
     */
    public EditorItems setupItemIcon(final String itemId) {
        ItemStack it = new ItemStack(Material.BEDROCK);
        final ItemStack loadedIt = ItemsListConfig.loadConfigItem(itemId);
        if (loadedIt != null)
            it = loadedIt.clone();

        final ItemMeta meta = it.getItemMeta();

        final ArrayList<String> lores = new ArrayList<>();

        lores.add(" ");
        lores.add("§aID: §b" + itemId);
        lores.add(" ");
        lores.add("§e클릭하면 이 아이템을 편집해요.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = itemId;

        return this;
    }

    public EditorItems setupEditItemIcon(final String itemId) {
        ItemStack it = new ItemStack(Material.BEDROCK);
        final ItemStack loadedIt = ItemsListConfig.loadConfigItem(itemId);
        if (loadedIt != null)
            it = loadedIt.clone();

        final ItemMeta meta = it.getItemMeta();

        ArrayList<String> lores = new ArrayList<>();
        if (it.getItemMeta().hasLore() && it.getItemMeta().getLore() != null)
        {
            lores = (ArrayList<String>) it.getItemMeta().getLore();
        }

        lores.add(" ");
        lores.add("§e아이템을 들고 클릭하면 바꿔요.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = itemId;

        return this;
    }

    private static ItemStack ITEMS_EDIT_ID() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6아이템 ID");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7아이템 ID를 편집해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * Pet food editor
     */
    public EditorItems setupPetfoodIcon(final String petFoodId) {
        ItemStack it = new ItemStack(Material.BEDROCK);
        final PetFood petFood = PetFoodConfig.loadConfigPetFood(petFoodId);
        if (petFood.getItemStack() != null)
            it = petFood.getItemStack().clone();

        final ItemMeta meta = it.getItemMeta();

        final ArrayList<String> lores = new ArrayList<>();

        lores.add(" ");
        lores.add("§aID: §b" + petFood.getId());
        lores.add(" ");
        lores.add("§e클릭하면 이 펫 먹이를 편집해요.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = petFood;

        return this;
    }

    public EditorItems setupEditPetFoodIcon(final String petFoodId) {
        ItemStack it = new ItemStack(Material.BEDROCK);
        final PetFood petFood = PetFoodConfig.loadConfigPetFood(petFoodId);
        if (petFood.getItemStack() != null)
            it = petFood.getItemStack().clone();

        final ItemMeta meta = it.getItemMeta();

        ArrayList<String> lores = new ArrayList<>();
        if (it.getItemMeta().hasLore() && it.getItemMeta().getLore() != null)
        {
            lores = (ArrayList<String>) it.getItemMeta().getLore();
        }

        lores.add(" ");
        lores.add("§e아이템을 들고 클릭하면 바꿔요.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = petFood;

        return this;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_ID() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6펫 먹이 ID");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫 먹이 ID를 편집해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    public EditorItems setupPetFoodEditorEditItem(final String petFoodId) {
        final PetFood petFood = PetFoodConfig.loadConfigPetFood(petFoodId);
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        if (petFood.getItemStack() != null)
            it = petFood.getItemStack().clone();
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6먹이 아이템");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7먹이로 쓸 아이템을 정해요.");
        lores.add("§7등록한 §a펫 아이템§7이나");
        lores.add("§7아무 §bMATERIAL§7이나 쓸 수 있어요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;
        return this;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_TYPE() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6먹이 종류");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫 먹이의 종류를 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_POWER() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6위력 값");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫 먹이의 효과 크기예요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_DURATION() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6버프 지속 시간");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7버프 먹이라면 효과가");
        lores.add("§7유지되는 시간이에요 (틱 단위).");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_OPERATOR() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6연산 방식");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7먹이 위력을 적용할");
        lores.add("§7계산 방식을 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_SIGNAL() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6명령");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫에게 먹이를 줄 때");
        lores.add("§7실행할 명령이에요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_PETS_ADD() {
        final ItemStack it = new ItemStack(Material.GOLD_INGOT);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§a펫 §6추가");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7먹을 수 있는 펫을 §a추가§7해요. §a(선택)");
        lores.add(" ");
        lores.add("§7지금 먹을 수 있는 펫: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_PETS_REMOVE() {
        final ItemStack it = new ItemStack(Material.NETHER_BRICK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§c펫 §6빼기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7먹을 수 있는 펫에서 §c빼요§7. §a(선택)");
        lores.add(" ");
        lores.add("§7지금 먹을 수 있는 펫: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_EVOLUTION() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6진화");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§a(선택)§7 펫이 먹이를 먹으면");
        lores.add("§7일어날 진화를 정해요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_EXP_THRESHOLD() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6필요 경험치");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫이 이 경험치를 넘으면");
        lores.add("§7먹이를 먹을 수 있어요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_DELAY() {
        final ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6진화 전 대기");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7진화 먹이라면 진화가 일어나기까지");
        lores.add("§7기다리는 시간이에요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_PERMISSION() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6권한");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7펫 먹이를 쓰는 데 필요한 권한이에요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_UNLOCKED_PET() {
        final ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6해금할 펫");

        final ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7먹이 종류가 §aUNLOCK§7이면");
        lores.add("§7해금할 펫이에요.");
        lores.add(" ");
        lores.add("§7현재 값: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }
}
