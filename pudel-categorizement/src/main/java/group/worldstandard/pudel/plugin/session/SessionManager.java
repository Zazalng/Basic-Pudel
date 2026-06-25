package group.worldstandard.pudel.plugin.session;

import net.dv8tion.jda.api.entities.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages user session state for the categorization plugin.
 * Encapsulates all per-user ephemeral state to avoid scattering maps across the main class.
 */
public class SessionManager {

    /** Ephemeral control panel message per user (userId -> Message). */
    private final Map<String, Message> controlMessages = new ConcurrentHashMap<>();
    /** Permission cursor index per user. */
    private final Map<String, Integer> permCursor = new ConcurrentHashMap<>();
    /** Currently editing profile name per user. */
    private final Map<String, String> editingProfileName = new ConcurrentHashMap<>();
    /** Temporary permission state per user: permEnumName -> "ALLOW"/"INHERIT"/"DENY". */
    private final Map<String, java.util.LinkedHashMap<String, String>> tempPermState = new ConcurrentHashMap<>();
    /** Create form state per user. */
    private final Map<String, Map<String, String>> createFormState = new ConcurrentHashMap<>();
    /** Import form state per user. */
    private final Map<String, Map<String, String>> importFormState = new ConcurrentHashMap<>();

    // ==================== CONTROL MESSAGES ====================

    public void putControlMessage(String userId, Message message) {
        controlMessages.put(userId, message);
    }

    public Message getControlMessage(String userId) {
        return controlMessages.get(userId);
    }

    public Message removeControlMessage(String userId) {
        return controlMessages.remove(userId);
    }

    // ==================== PERMISSION CURSOR ====================

    public int getPermCursor(String userId) {
        return permCursor.getOrDefault(userId, 0);
    }

    public void setPermCursor(String userId, int cursor) {
        permCursor.put(userId, cursor);
    }

    public void removePermCursor(String userId) {
        permCursor.remove(userId);
    }

    // ==================== EDITING PROFILE ====================

    public void setEditingProfileName(String userId, String profileName) {
        editingProfileName.put(userId, profileName);
    }

    public String getEditingProfileName(String userId) {
        return editingProfileName.get(userId);
    }

    public void removeEditingProfileName(String userId) {
        editingProfileName.remove(userId);
    }

    // ==================== TEMP PERMISSION STATE ====================

    public void putTempPermState(String userId, java.util.LinkedHashMap<String, String> state) {
        tempPermState.put(userId, state);
    }

    public java.util.LinkedHashMap<String, String> getTempPermState(String userId) {
        return tempPermState.get(userId);
    }

    public void removeTempPermState(String userId) {
        tempPermState.remove(userId);
    }

    // ==================== CREATE FORM STATE ====================

    public void putCreateFormState(String userId, Map<String, String> state) {
        createFormState.put(userId, state);
    }

    public Map<String, String> getCreateFormState(String userId) {
        return createFormState.get(userId);
    }

    public void removeCreateFormState(String userId) {
        createFormState.remove(userId);
    }

    // ==================== IMPORT FORM STATE ====================

    public void putImportFormState(String userId, Map<String, String> state) {
        importFormState.put(userId, state);
    }

    public Map<String, String> getImportFormState(String userId) {
        return importFormState.get(userId);
    }

    public void removeImportFormState(String userId) {
        importFormState.remove(userId);
    }

    // ==================== BULK CLEANUP ====================

    /**
     * Clears all session state for a user.
     */
    public void clearUserSession(String userId) {
        removeControlMessage(userId);
        removePermCursor(userId);
        removeEditingProfileName(userId);
        removeTempPermState(userId);
        removeCreateFormState(userId);
        removeImportFormState(userId);
    }

    /**
     * Clears all sessions (used on shutdown).
     */
    public void clearAllSessions() {
        controlMessages.values().forEach(msg -> {
            try { msg.delete().queue(null, _ -> {}); } catch (Exception ignored) {}
        });
        controlMessages.clear();
        permCursor.clear();
        editingProfileName.clear();
        tempPermState.clear();
        createFormState.clear();
        importFormState.clear();
    }
}