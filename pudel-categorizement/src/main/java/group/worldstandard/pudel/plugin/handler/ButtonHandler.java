package group.worldstandard.pudel.plugin.handler;

import group.worldstandard.pudel.plugin.builder.PanelBuilder;
import group.worldstandard.pudel.plugin.service.PermissionService;
import group.worldstandard.pudel.plugin.session.SessionManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;

/**
 * Handles button interactions using the strategy pattern.
 * Each logical group of buttons is delegated to a focused handler method.
 */
public class ButtonHandler {

    private final String btnPrefix;
    private final PermissionService permissionService;
    private final SessionManager sessionManager;
    private final PanelBuilder panelBuilder;

    public ButtonHandler(String btnPrefix, PermissionService permissionService,
                         SessionManager sessionManager, PanelBuilder panelBuilder) {
        this.btnPrefix = btnPrefix;
        this.permissionService = permissionService;
        this.sessionManager = sessionManager;
        this.panelBuilder = panelBuilder;
    }

    /**
     * Main entry point — dispatches to sub-handlers based on button ID prefix groups.
     */
    public void handle(ButtonInteractionEvent event) {
        var guild = event.getGuild();
        var member = event.getMember();
        if (guild == null || member == null) return;

        var userId = event.getUser().getId();
        var guildId = guild.getId();
        var buttonId = event.getComponentId().substring(btnPrefix.length());
        var hasAuth = permissionService.hasPermissionOrPrivilege(member, guildId);

        // Route to appropriate sub-handler
        if (isMainPanelButton(buttonId)) {
            handleMainPanel(event, userId, guildId, guild, member, hasAuth);
        } else if (isCreatePanelButton(buttonId)) {
            handleCreatePanel(event, userId, guildId, guild, member, hasAuth);
        } else if (isImportPanelButton(buttonId)) {
            handleImportPanel(event, userId, guildId, guild, member);
        } else if (isSettingPanelButton(buttonId)) {
            handleSettingPanel(event, userId, guildId, guild, member, hasAuth);
        } else if (isPermissionPanelButton(buttonId)) {
            handlePermissionPanel(event, userId, guildId, guild, member, hasAuth);
        } else if (isPrivilegePanelButton(buttonId)) {
            handlePrivilegePanel(event, guild, member, hasAuth);
        } else if (buttonId.equals("back_main")) {
            handleBackMain(event, userId, guild, member);
        }
    }

    // ==================== BUTTON ID CLASSIFICATION ====================

    private boolean isMainPanelButton(String id) {
        return id.equals("create") || id.equals("import") || id.equals("setting")
                || id.equals("back_setting") || id.equals("view") || id.equals("back_view") || id.equals("unlink");
    }

    private boolean isCreatePanelButton(String id) {
        return id.startsWith("create_");
    }

    private boolean isImportPanelButton(String id) {
        return id.startsWith("import_");
    }

    private boolean isSettingPanelButton(String id) {
        return id.startsWith("profile_") || id.equals("privilege") || id.equals("back_privilege");
    }

    private boolean isPermissionPanelButton(String id) {
        return id.equals("up") || id.equals("down") || id.equals("allow")
                || id.equals("inherit") || id.equals("deny") || id.equals("confirm")
                || id.equals("perm_reset") || id.equals("perm_back");
    }

    private boolean isPrivilegePanelButton(String id) {
        return id.equals("priv_add") || id.equals("priv_remove");
    }

    // ==================== MAIN PANEL ====================

    private void handleMainPanel(ButtonInteractionEvent event, String userId, String guildId,
                                  Guild guild,
                                  Member member, boolean hasAuth) {
        switch (event.getComponentId().substring(btnPrefix.length())) {
            case "create" -> {
                if (!hasAuth) { replyNoPermission(event); return; }
                sessionManager.putCreateFormState(userId, new java.util.LinkedHashMap<>());
                sessionManager.getCreateFormState(userId).put("controlBy", "false");
                event.editMessage(panelBuilder.buildCreatePanel(guildId, userId).build()).queue();
            }
            case "import" -> {
                if (!hasAuth) { replyNoPermission(event); return; }
                sessionManager.putImportFormState(userId, new java.util.LinkedHashMap<>());
                sessionManager.getImportFormState(userId).put("acknowledged", "false");
                event.editMessage(panelBuilder.buildImportPanel(guildId, userId).build()).queue();
            }
            case "setting", "back_setting" -> event.editMessage(panelBuilder.buildSettingPanel(guild, member).build()).queue();
            case "view", "back_view" -> event.editMessage(panelBuilder.buildViewCategoryPanel(guild).build()).queue();
            case "unlink" -> event.editMessage(panelBuilder.buildUnlinkPanel(guild, member).build()).queue();
        }
    }

    // ==================== CREATE PANEL ====================

    private void handleCreatePanel(ButtonInteractionEvent event, String userId, String guildId,
                                    Guild guild,
                                    Member member, boolean hasAuth) {
        String buttonId = event.getComponentId().substring(btnPrefix.length());
        switch (buttonId) {
            case "create_set_name" -> handleCreateSetName(event);
            case "create_toggle_control" -> handleCreateToggleControl(event, userId, guildId);
            case "create_confirm" -> handleCreateConfirm(event, userId, guildId, guild, member);
            case "create_cancel" -> handleCreateCancel(event, userId, guild, member);
        }
    }

    private void handleCreateSetName(ButtonInteractionEvent event) {
        var nameInput = TextInput.create("name",
                TextInputStyle.SHORT)
                .setPlaceholder("e.g. Staff Area, Private Channels")
                .setMinLength(1).setMaxLength(100).setRequired(true).build();
        event.replyModal(Modal.create(
                btnPrefix.replace("button", "modal") + "create_name", "Set Category Name")
                .addComponents(Label.of("Category Name", nameInput)).build()
        ).queue();
    }

    private void handleCreateToggleControl(ButtonInteractionEvent event, String userId, String guildId) {
        var state = sessionManager.getCreateFormState(userId);
        if (state != null) {
            boolean current = Boolean.parseBoolean(state.getOrDefault("controlBy", "false"));
            state.put("controlBy", String.valueOf(!current));
            event.editMessage(panelBuilder.buildCreatePanel(guildId, userId).build()).queue();
        }
    }

    private void handleCreateConfirm(ButtonInteractionEvent event, String userId, String guildId,
                                      Guild guild,
                                      Member member) {
        var state = sessionManager.getCreateFormState(userId);
        if (state == null) return;
        var name = state.getOrDefault("name", "").trim();
        if (name.isEmpty()) {
            event.reply("❌ Category name cannot be empty!").setEphemeral(true)
                    .queue(m -> m.deleteOriginal().queueAfter(5, java.util.concurrent.TimeUnit.SECONDS));
            return;
        }
        var managerId = state.get("manager");
        var roleId = state.get("default_role");
        var managerProfileName = state.get("manager_profile");
        var roleProfileName = state.get("role_profile");
        boolean controlPudel = Boolean.parseBoolean(state.getOrDefault("controlBy", "false"));

        var managerProfile = permissionService.findProfile(guildId, managerProfileName);
        var roleProfile = permissionService.findProfile(guildId, roleProfileName);

        var finalName = name;
        event.deferEdit().queue(hook -> guild.createCategory(finalName).queue(category -> {
            permissionService.applyPermissions(category, guild, managerId, roleId, managerProfile, roleProfile);
            if (controlPudel) {
                permissionService.saveCategoryEntry(
                        new group.worldstandard.pudel.plugin.entity.CategoryEntry(
                                null, guildId, category.getId(), managerId, managerProfileName, roleId, roleProfileName));
            }
            sessionManager.removeCreateFormState(userId);
            hook.editOriginalComponents(
                    TextDisplay.of(
                            "✅ Category **" + finalName + "** imported and tracked!" + (controlPudel ? " (Tracked by Pudel)" : "")))
                    .queue(m -> m.delete().queueAfter(5, java.util.concurrent.TimeUnit.SECONDS));
            refreshMainPanel(userId, guild, member);
        }, err -> hook.editOriginal("❌ Failed to create category: " + err.getMessage())
                .queue(m -> m.delete().queueAfter(5, java.util.concurrent.TimeUnit.SECONDS))));
    }

    private void handleCreateCancel(ButtonInteractionEvent event, String userId,
                                     Guild guild,
                                     Member member) {
        sessionManager.removeCreateFormState(userId);
        event.editMessage(panelBuilder.editMainPanel(guild, member).build()).queue();
    }

    // ==================== IMPORT PANEL ====================

    private void handleImportPanel(ButtonInteractionEvent event, String userId, String guildId,
                                    Guild guild,
                                    Member member) {
        String buttonId = event.getComponentId().substring(btnPrefix.length());
        switch (buttonId) {
            case "import_toggle_ack" -> handleImportToggleAck(event, userId, guildId);
            case "import_confirm" -> handleImportConfirm(event, userId, guildId, guild, member);
            case "import_cancel" -> handleImportCancel(event, userId, guild, member);
        }
    }

    private void handleImportToggleAck(ButtonInteractionEvent event, String userId, String guildId) {
        var state = sessionManager.getImportFormState(userId);
        if (state != null) {
            boolean current = Boolean.parseBoolean(state.getOrDefault("acknowledged", "false"));
            state.put("acknowledged", String.valueOf(!current));
            event.editMessage(panelBuilder.buildImportPanel(guildId, userId).build()).queue();
        }
    }

    private void handleImportConfirm(ButtonInteractionEvent event, String userId, String guildId,
                                      Guild guild,
                                      Member member) {
        var state = sessionManager.getImportFormState(userId);
        if (state == null) return;
        var categoryId = state.get("category");
        if (categoryId == null) {
            event.reply("❌ Please select a category!").setEphemeral(true)
                    .queue(m -> m.deleteOriginal().queueAfter(5, java.util.concurrent.TimeUnit.SECONDS));
            return;
        }
        if (permissionService.isCategoryTracked(categoryId)) {
            event.reply("❌ This category is already tracked by Pudel!").setEphemeral(true)
                    .queue(m -> m.deleteOriginal().queueAfter(5, java.util.concurrent.TimeUnit.SECONDS));
            return;
        }
        var managerId = state.get("manager");
        var roleId = state.get("default_role");
        var managerProfileName = state.get("manager_profile");
        var roleProfileName = state.get("role_profile");
        boolean acknowledged = Boolean.parseBoolean(state.getOrDefault("acknowledged", "false"));
        boolean hasManagerOrRole = (managerId != null) || (roleId != null);
        if (hasManagerOrRole && !acknowledged) {
            event.reply("❌ You must acknowledge the warning when setting a Manager User or Default Role!")
                    .setEphemeral(true).queue(m -> m.deleteOriginal().queueAfter(5, java.util.concurrent.TimeUnit.SECONDS));
            return;
        }

        var category = guild.getCategoryById(categoryId);
        if (category == null) {
            event.reply("❌ Category not found in this server!").setEphemeral(true)
                    .queue(m -> m.deleteOriginal().queueAfter(5, java.util.concurrent.TimeUnit.SECONDS));
            return;
        }

        var managerProfile = permissionService.findProfile(guildId, managerProfileName);
        var roleProfile = permissionService.findProfile(guildId, roleProfileName);
        var catName = category.getName();

        event.deferEdit().queue(hook -> {
            permissionService.applyPermissions(category, guild, managerId, roleId, managerProfile, roleProfile);
            if (hasManagerOrRole) {
                for (var ch : category.getChannels()) {
                    if (ch instanceof ICategorizableChannel catCh) {
                        catCh.getManager().sync(category).queue(null, _ -> {});
                    }
                }
            }
            permissionService.saveCategoryEntry(
                    new group.worldstandard.pudel.plugin.entity.CategoryEntry(
                            null, guildId, categoryId, managerId, managerProfileName, roleId, roleProfileName));
            sessionManager.removeImportFormState(userId);
            hook.editOriginalComponents(TextDisplay.of("✅ Category **" + catName + "** imported and tracked!"))
                    .queue(m -> event.editMessage(panelBuilder.editMainPanel(guild, member).build()).queueAfter(5, java.util.concurrent.TimeUnit.SECONDS));
            refreshMainPanel(userId, guild, member);
        });
    }

    private void handleImportCancel(ButtonInteractionEvent event, String userId, Guild guild, Member member) {
        sessionManager.removeImportFormState(userId);
        event.editMessage(panelBuilder.editMainPanel(guild, member).build()).queue();
    }

    // ==================== SETTING PANEL ====================

    private void handleSettingPanel(ButtonInteractionEvent event, String userId, String guildId,
                                    Guild guild,
                                    Member member, boolean hasAuth) {
        String buttonId = event.getComponentId().substring(btnPrefix.length());
        switch (buttonId) {
            case "profile_view" -> event.editMessage(panelBuilder.buildViewProfileSelectPanel(guildId).build()).queue();
            case "profile_create" -> {
                if (!hasAuth) { replyNoPermission(event); return; }
                handleCreateProfileModal(event);
            }
            case "profile_remove" -> {
                if (!hasAuth) { replyNoPermission(event); return; }
                event.editMessage(panelBuilder.buildRemoveProfileSelectPanel(guildId).build()).queue();
            }
            case "privilege", "back_privilege" -> event.editMessage(panelBuilder.buildPrivilegePanel(guild, hasAuth).build()).queue();
        }
    }

    private void handleCreateProfileModal(ButtonInteractionEvent event) {
        var nameInput = TextInput.create("profile_name",
                TextInputStyle.SHORT)
                .setPlaceholder("e.g. Manager Default, Read Only")
                .setMinLength(1).setMaxLength(50).setRequired(true).build();
        event.replyModal(Modal.create(
                btnPrefix.replace("button", "modal") + "profile_create", "Create Permission Profile")
                .addComponents(Label.of("Profile Name", nameInput)).build()
        ).queue();
    }

    // ==================== PERMISSION PANEL ====================

    private void handlePermissionPanel(ButtonInteractionEvent event, String userId, String guildId,
                                        Guild guild,
                                        Member member, boolean hasAuth) {
        String buttonId = event.getComponentId().substring(btnPrefix.length());
        switch (buttonId) {
            case "up" -> {
                int cur = sessionManager.getPermCursor(userId);
                sessionManager.setPermCursor(userId, Math.max(0, cur - 1));
                event.editMessage(panelBuilder.buildPermissionPanel(userId, hasAuth).build()).queue();
            }
            case "down" -> {
                int cur = sessionManager.getPermCursor(userId);
                int maxIdx = panelBuilder.getManageablePermissions().size() - 1;
                sessionManager.setPermCursor(userId, Math.min(maxIdx, cur + 1));
                event.editMessage(panelBuilder.buildPermissionPanel(userId, hasAuth).build()).queue();
            }
            case "allow" -> {
                if (!hasAuth) { replyNoPermission(event); return; }
                setCurrentPermState(userId, "ALLOW");
                event.editMessage(panelBuilder.buildPermissionPanel(userId, hasAuth).build()).queue();
            }
            case "inherit" -> {
                if (!hasAuth) { replyNoPermission(event); return; }
                setCurrentPermState(userId, "INHERIT");
                event.editMessage(panelBuilder.buildPermissionPanel(userId, hasAuth).build()).queue();
            }
            case "deny" -> {
                if (!hasAuth) { replyNoPermission(event); return; }
                setCurrentPermState(userId, "DENY");
                event.editMessage(panelBuilder.buildPermissionPanel(userId, hasAuth).build()).queue();
            }
            case "confirm" -> {
                if (!hasAuth) { replyNoPermission(event); return; }
                saveProfilePermState(userId, guildId);
                event.editMessage(panelBuilder.buildSettingPanel(guild, member).build()).queue();
            }
            case "perm_reset" -> {
                if (!hasAuth) { replyNoPermission(event); return; }
                String profileName = sessionManager.getEditingProfileName(userId);
                if (profileName != null) {
                    java.util.LinkedHashMap<String, String> state = permissionService.loadProfilePermState(guildId, profileName);
                    sessionManager.putTempPermState(userId, state);
                }
                event.editMessage(panelBuilder.buildPermissionPanel(userId, hasAuth).build()).queue();
            }
            case "perm_back" -> {
                sessionManager.removePermCursor(userId);
                sessionManager.removeEditingProfileName(userId);
                sessionManager.removeTempPermState(userId);
                event.editMessage(panelBuilder.buildSettingPanel(guild, member).build()).queue();
            }
        }
    }

    // ==================== PRIVILEGE PANEL ====================

    private void handlePrivilegePanel(ButtonInteractionEvent event,
                                       Guild guild,
                                       Member member, boolean hasAuth) {
        String buttonId = event.getComponentId().substring(btnPrefix.length());
        if (buttonId.equals("priv_add")) {
            if (!member.hasPermission(Permission.ADMINISTRATOR)) {
                replyNoPermission(event);
                return;
            }
            showAddPrivilegeModal(event);
        } else if (buttonId.equals("priv_remove")) {
            if (!member.hasPermission(Permission.ADMINISTRATOR)) {
                replyNoPermission(event);
                return;
            }
            event.editMessage(panelBuilder.buildPrivilegeRemovePanel(guild).build()).queue();
        }
    }

    private void showAddPrivilegeModal(ButtonInteractionEvent event) {
        var roleMenu = EntitySelectMenu.create("role",
                EntitySelectMenu.SelectTarget.ROLE)
                .setRequiredRange(1, 1).build();
        event.replyModal(Modal.create(
                btnPrefix.replace("button", "modal") + "priv_add", "Add Privilege Role")
                .addComponents(Label.of("Select Role to Grant Privilege", roleMenu)).build()
        ).queue();
    }

    // ==================== NAVIGATION ====================

    private void handleBackMain(ButtonInteractionEvent event, String userId,
                                 Guild guild,
                                 Member member) {
        sessionManager.removePermCursor(userId);
        sessionManager.removeEditingProfileName(userId);
        sessionManager.removeTempPermState(userId);
        sessionManager.removeCreateFormState(userId);
        sessionManager.removeImportFormState(userId);
        event.editMessage(panelBuilder.editMainPanel(guild, member).build()).queue();
    }

    // ==================== HELPERS ====================

    private void setCurrentPermState(String userId, String value) {
        int cursor = sessionManager.getPermCursor(userId);
        var state = sessionManager.getTempPermState(userId);
        if (state == null) return;
        var maxIdx = panelBuilder.getManageablePermissions().size();
        if (cursor >= maxIdx) return;
        var permName = panelBuilder.getManageablePermissions().get(cursor).name();
        state.put(permName, value);
    }

    private void saveProfilePermState(String userId, String guildId) {
        var state = sessionManager.getTempPermState(userId);
        var profileName = sessionManager.getEditingProfileName(userId);
        if (state == null || profileName == null) return;
        permissionService.saveProfilePermState(guildId, profileName, state);
        sessionManager.removePermCursor(userId);
        sessionManager.removeEditingProfileName(userId);
        sessionManager.removeTempPermState(userId);
    }

    private void replyNoPermission(ButtonInteractionEvent event) {
        event.reply("❌ You need **Manage Channels** permission or a Privilege Role to do this!")
                .setEphemeral(true).queue(m -> m.deleteOriginal().queueAfter(5, java.util.concurrent.TimeUnit.SECONDS));
    }

    private void refreshMainPanel(String userId, Guild guild,
                                   Member member) {
        var msg = sessionManager.getControlMessage(userId);
        if (msg != null) {
            msg.editMessage(panelBuilder.editMainPanel(guild, member).build()).queue(null, _ -> {});
        }
    }
}