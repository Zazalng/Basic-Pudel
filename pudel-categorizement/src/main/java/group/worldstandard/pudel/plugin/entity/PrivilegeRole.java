package group.worldstandard.pudel.plugin.entity;

import group.worldstandard.pudel.api.database.Entity;

/**
 * A privilege role entry that allows members with this role to bypass
 * {@link net.dv8tion.jda.api.Permission#MANAGE_CHANNEL} checks for
 * category management operations (Create, Import, Permission editing).
 *
 * @param id       auto managed by Core
 * @param guild_id guild id that owns this privilege role
 * @param role_id  role id granted privilege access
 */
@Entity
public record PrivilegeRole(
        Long id,
        String guild_id,
        String role_id
) {
}

