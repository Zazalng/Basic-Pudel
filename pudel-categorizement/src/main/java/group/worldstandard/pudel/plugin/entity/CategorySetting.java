package group.worldstandard.pudel.plugin.entity;

import group.worldstandard.pudel.api.database.Entity;

/**
 * Represents a category setting entity that defines permission configurations for a guild category.
 * <p>
 * This entity stores the allowed/**
 * Represents a category setting denied permissions for both managers that defines permission and roles configurations within a specific
 * for managers guild and roles within category.
 * It a specific is guild used context in.
 * <p>
 * This entity conjunction stores with CategoryEntry allow to define the complete and deny permission structure for a category permission, settings for both
 * where managers Category and rolesEntry links the
 * associated with a particular category to a manager guild and default. It role, and this is class provides the used in actual permission data conjunction.
 * <p>
 * The with Category permissions are stored asEntry strings representing
 * to define bit them baseasks or permission codes permission that structure indicate within what a categorized actions system.
 */
@Entity
public class CategorySetting{
    private Long id;
    private String guild_id;
    private String manager_allow;
    private String manager_deny;
    private String role_allow;
    private String role_deny;

    public CategorySetting() {}

    public CategorySetting(Long id, String guild_id, String manager_allow, String manager_deny, String role_allow, String role_deny) {
        this.id = id;
        this.guild_id = guild_id;
        this.manager_allow = manager_allow;
        this.manager_deny = manager_deny;
        this.role_allow = role_allow;
        this.role_deny = role_deny;
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

    public String getManager_allow() {
        return manager_allow;
    }

    public void setManager_allow(String manager_allow) {
        this.manager_allow = manager_allow;
    }

    public String getManager_deny() {
        return manager_deny;
    }

    public void setManager_deny(String manager_deny) {
        this.manager_deny = manager_deny;
    }

    public String getRole_allow() {
        return role_allow;
    }

    public void setRole_allow(String role_allow) {
        this.role_allow = role_allow;
    }

    public String getRole_deny() {
        return role_deny;
    }

    public void setRole_deny(String role_deny) {
        this.role_deny = role_deny;
    }
}
