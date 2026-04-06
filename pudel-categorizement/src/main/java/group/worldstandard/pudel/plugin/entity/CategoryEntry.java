package group.worldstandard.pudel.plugin.entity;

import group.worldstandard.pudel.api.database.Entity;

/**
 * Represents a category entry entity that defines the relationship between a guild category,
 * its manager, and a default role with predefined permissions.
 * <p>
 * This entity associates a Discord guild category with a designated manager user and a default role.
 * The default role is assigned basic permissions within the category as defined by
 * {@link CategorySetting}. Additional privileged roles may be defined separately using
 * {@link PrivilegeRole}.
 */
@Entity
public class CategoryEntry{
    private Long id;
    private String guild_id;
    private String category_id;
    private String manager_id; // user_id
    private String default_role; // role_id that had default permission set from modal

    public CategoryEntry() {}

    public CategoryEntry(Long id, String guild_id, String category_id, String manager_id, String default_role) {
        this.id = id;
        this.guild_id = guild_id;
        this.category_id = category_id;
        this.manager_id = manager_id;
        this.default_role = default_role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuild_id() {
        return guild_id;
    }

    public void setGuild_id(String guild_id) {
        this.guild_id = guild_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getManager_id() {
        return manager_id;
    }

    public void setManager_id(String manager_id) {
        this.manager_id = manager_id;
    }

    public String getDefault_role() {
        return default_role;
    }

    public void setDefault_role(String default_role) {
        this.default_role = default_role;
    }
}
