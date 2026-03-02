package group.worldstandard.pudel.plugin.entity;

import group.worldstandard.pudel.api.database.Entity;

@Entity
public record PrankContainer(
        Long id, // core primary
        String user_id, // plugin primary (discord user id number)
        String container_id, // plugin primary (UUID perfer)
        String name,
        int usage
) {
}
