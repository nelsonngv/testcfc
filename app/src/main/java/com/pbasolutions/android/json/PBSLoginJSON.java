package com.pbasolutions.android.json;

/**
 * Created by pbadell on 6/25/15.
 */

public class PBSLoginJSON extends PBSJson {

    /**
     * Server time returned value.
     */
    private String ServerTime;
    /**
     * Authentication token returned value.
     */
    private String Token;
    /**
     * Authentication token valid until expiration date returned value.
     */
    private String ValidTo;
    /**
     * Time for each sync interval returned value.(second)
     */
    private int SyncInterval;
    /**
     * is IsReset flag returned value.
     */
    private boolean IsReset;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setIsReset(boolean isReset) {
        IsReset = isReset;
    }

    /**
     * User ID
     */
    private String UserID;
    /**
     * Roles array returned value.
     */
    private PBSRoleJSON Roles[];
    /**
     * Table array returned value.
     */
    private PBSTableJSON Tables[];
    /**
     * Forms array returned value.
     */
    private String Forms[];

    public PBSLoginJSON() {
    }

    /**
     * Get server time returned value.
     * @return server time in String
     */
    public String getServerTime() {
        return ServerTime;
    }
    /**
     * Set server time returned value.
     * @param serverTime server time in String
     */
    public void setServerTime(String serverTime) {
        ServerTime = serverTime;
    }
    /**
     * Get authentication returned value.
     * @return token String
     */
    public String getToken() {
        return Token;
    }
    /**
     * Set authentication returned value.
     * @param token authentication token.
     */
    public void setToken(String token) {
        Token = token;
    }
    /**
     * Get expired token date returned value(in String).
     * @return expired date in String
     */
    public String getValidTo() {
        return ValidTo;
    }
    /**
     * Set expired token date returned value(in String).
     * @param validTo expired date
     */
    public void setValidTo(String validTo) {
        ValidTo = validTo;
    }

    /**
     * Get interval time of sync
     * @return int
     */
    public int getSyncInterval() {
        return SyncInterval;
    }

    /**
     * Set interval time of sync
     * @param syncInterval int
     */
    public void setSyncInterval(int syncInterval) {
        SyncInterval = syncInterval;
    }

    /**
     * Flag is data reset needed.
     * @return boolean result.
     */
    public boolean isReset() {
        return IsReset;
    }

    /**
     * Set data reset flag.
     * @param reset boolean
     */
    public void setReset(boolean reset) {
        IsReset = reset;
    }

    /**
     * Get Role array for specific user.
     * @return PBS_RoleJSON.
     */
    public PBSRoleJSON[] getRoles() {
        return Roles;
    }

    /**
     * Set Role array for specific user.
     * @param roles
     */
    public void setRoles(PBSRoleJSON[] roles) {
        Roles = roles;
    }

    /**
     * Get master data tables for specific user.
     * @return table
     */
    public PBSTableJSON[] getTables() {
        return Tables;
    }

    /**
     * Set master data tables for specific user.
     * @param tables
     */
    public void setTables(PBSTableJSON[] tables) {
        Tables = tables;
    }

    /**
     * Get Forms array for specific user.
     * @return forms
     */
    public String[] getForms() {
        return Forms;
    }

    /**
     * Set Forms array for specific user.
     * @param forms
     */
    public void setForms(String[] forms) {
        Forms = forms;
    }

}
