package group.worldstandard.pudel.plugin.entity;

import group.worldstandard.pudel.api.database.Entity;

@Entity
public record PrankCollection(
        Long id, // core primary
        String prank_id, // plugin primary
        String container_id, // foreign key with ContainerPrank.container_id
        String url,
        String placeholder
) {
}
