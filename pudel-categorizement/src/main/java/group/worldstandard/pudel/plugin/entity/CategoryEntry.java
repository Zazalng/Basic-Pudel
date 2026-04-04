package group.worldstandard.pudel.plugin.entity;

import group.worldstandard.pudel.api.database.Entity;

/**
 * An entity representing a category record for a guild.
 *
 * @param id automatically managed by the core
 * @param guild_id the ID of the guild that owns this category
 * @param category_id the ID of the category within the guild; unique per guild
 * @param manager_id the ID of the user who has full permissions for this category
 * @param default_role the ID of the role that has default permissions for this category
 */
@Entity
public record CategoryEntry(
        Long id,
        String guild_id,
        String category_id,
        String manager_id, // user_id
        String default_role // role_id that had default permission set from modal
) {
}
