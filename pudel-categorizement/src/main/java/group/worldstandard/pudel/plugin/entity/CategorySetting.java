package group.worldstandard.pudel.plugin.entity;

import group.worldstandard.pudel.api.database.Entity;

/**
 * Default permission settings for a guild, storing comma-separated
 * {@link net.dv8tion.jda.api.Permission} enum names for Manager and Default Role.
 *
 * <p>Each field stores permission names joined by {@code ","}.
 * Permissions NOT present in either allow or deny are considered <em>Inherit</em>.
 *
 * @param id            auto managed by Core
 * @param guild_id      guild id that owns this setting (unique)
 * @param manager_allow comma-separated Permission names to ALLOW for manager
 * @param manager_deny  comma-separated Permission names to DENY for manager
 * @param role_allow    comma-separated Permission names to ALLOW for default role
 * @param role_deny     comma-separated Permission names to DENY for default role
 */
@Entity
public record CategorySetting(
        Long id,
        String guild_id,
        String manager_allow,
        String manager_deny,
        String role_allow,
        String role_deny
) {
}
