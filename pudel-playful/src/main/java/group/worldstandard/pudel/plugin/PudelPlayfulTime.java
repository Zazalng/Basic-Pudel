package group.worldstandard.pudel.plugin;

import group.worldstandard.pudel.api.PluginContext;
import group.worldstandard.pudel.api.annotation.OnEnable;
import group.worldstandard.pudel.api.annotation.Plugin;
import group.worldstandard.pudel.api.database.*;
import group.worldstandard.pudel.plugin.entity.PrankContainer;
import group.worldstandard.pudel.plugin.entity.PrankCollection;

@Plugin(
        name = "Pudel's Playful Time",
        version = "1.0.0",
        author = "Zazalng",
        description = ""
)
public class PudelPlayfulTime {
    private PluginContext ctx;
    private PluginDatabaseManager db;

    private PluginRepository<PrankContainer> prankContainer;
    private PluginRepository<PrankCollection> prankCollection;

    @OnEnable()
    public void onEnable(PluginContext ctx) {
        this.ctx = ctx;
        this.db = ctx.getDatabaseManager();
    }

    private void initializeDatabase() {
        TableSchema queueSchema = TableSchema.builder("music_queue")
                .column("guild_id", ColumnType.BIGINT, false)
                .column("user_id", ColumnType.BIGINT, false)
                .column("track_blob", ColumnType.TEXT, false)
                .column("status", ColumnType.STRING, 20, false, "'QUEUE'")
                .column("title", ColumnType.STRING, 255, true)
                .column("is_looped", ColumnType.BOOLEAN, false, "false")
                .index("guild_id")
                .build();
        db.createTable(queueSchema);

        TableSchema historySchema = TableSchema.builder("music_history")
                .column("guild_id", ColumnType.BIGINT, false)
                .column("user_id", ColumnType.BIGINT, false)
                .column("track_title", ColumnType.STRING, 255, false)
                .column("track_url", ColumnType.TEXT, false)
                .column("played_at", ColumnType.BIGINT, false)
                .index("guild_id")
                .build();
        db.createTable(historySchema);

        this.prankContainer = db.getRepository("prank_container", PrankContainer.class);
        this.prankCollection = db.getRepository("prank_collection", PrankCollection.class);
    }
}
