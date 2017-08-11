package com.pbasolutions.android;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;

import com.pbasolutions.android.json.PBSProjLocJSON;
import com.pbasolutions.android.json.PBSRoleJSON;
import com.pbasolutions.android.model.ModelConst;

import java.io.Serializable;

/**
 * Created by pbadell on 7/30/15.
 */
public class PandoraContext extends Application implements Serializable{


    private String ad_role_uuid = "";
    private String ad_role_id = "";
    private String ad_role_name = "";
    private String ad_org_uuid = "";
    private String ad_org_id = "";
    private String ad_org_name = "";
    private String ad_client_uuid = "";
    private String ad_client_id = "";
    private String ad_client_name = "";
    private String ad_user_uuid = "";

    private String ad_user_id = "";
    private String ad_user_name = "";
    private String ad_user_password = "";
    private String auth_token = "";
    private String project_uuid = "";

    private String project_id = "";
    private String project_name = "";
    private PBSRoleJSON[] roleJSON;


    private PBSProjLocJSON[] projLocJSON;
    private String server_url = "";
    private String serial = "";
    private boolean isAuth = false;
    private int ad_role_spinner_index;
    private int ad_org_spinner_index;
    private int ad_client_spinner_index;

    private String c_projectlocation_uuid = "";
    private String c_projectlocation_id = "";
    private String c_projectlocation_name = "";
    private int C_ProjectLocation_Spinner_Index;

    private boolean isInitialSynced = false;
    private boolean isFirstBatchSynced = false;

    /**
     * Keeps a reference of the application context
     */
    private Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

    }

    /**
     * Returns the application context
     *
     * @return application context
     */
    public  Context getContext() {
        return sContext;
    }

    public String getAd_user_id() {
        return ad_user_id;
    }

    public void setAd_user_id(String ad_user_id) {
        this.ad_user_id = ad_user_id;
    }

    public String getAd_role_id() {
        return ad_role_id;
    }

    public void setAd_role_id(String ad_role_id) {
        this.ad_role_id = ad_role_id;
    }

    public String getAd_org_id() {
        return ad_org_id;
    }

    public void setAd_org_id(String ad_org_id) {
        this.ad_org_id = ad_org_id;
    }

    public String getAd_client_id() {
        return ad_client_id;
    }

    public void setAd_client_id(String ad_client_id) {
        this.ad_client_id = ad_client_id;
    }

    public boolean isFullComment() {
        return isFullComment;
    }

    public void setIsFullComment(boolean isFullComment) {
        this.isFullComment = isFullComment;
    }

    private boolean isFullComment;


    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public int getAd_role_spinner_index() {
        return ad_role_spinner_index;
    }

    public void setAd_role_spinner_index(int ad_role_spinner_index) {
        this.ad_role_spinner_index = ad_role_spinner_index;
    }

    public int getAd_org_spinner_index() {
        return ad_org_spinner_index;
    }

    public void setAd_org_spinner_index(int ad_org_spinner_index) {
        this.ad_org_spinner_index = ad_org_spinner_index;
    }

    public int getAd_client_spinner_index() {
        return ad_client_spinner_index;
    }

    public void setAd_client_spinner_index(int ad_client_spinner_index) {
        this.ad_client_spinner_index = ad_client_spinner_index;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setIsAuth(boolean isAuth) {
        this.isAuth = isAuth;
    }
    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
    public String getServer_url() {
        return server_url;
    }

    public void setServer_url(String server_url) {
        this.server_url = server_url;
    }

    public PBSRoleJSON[] getRoleJSON() {
        return roleJSON;
    }

    public void setRoleJSON(PBSRoleJSON[] roleJSON) {
        this.roleJSON = roleJSON;
    }

    public String getAd_user_password() {
        return ad_user_password;
    }

    public void setAd_user_password(String ad_user_password) {
        this.ad_user_password = ad_user_password;
    }

    public String getAd_client_name() {
        return ad_client_name;
    }

    public void setAd_client_name(String ad_client_name) {
        this.ad_client_name = ad_client_name;
    }

    public String getAd_org_name() {
        return ad_org_name;
    }

    public void setAd_org_name(String ad_org_name) {
        this.ad_org_name = ad_org_name;
    }

    public String getAd_role_uuid() {
        return ad_role_uuid;
    }

    public void setAd_role_uuid(String ad_role_uuid) {
        this.ad_role_uuid = ad_role_uuid;
    }

    public String getAd_role_name() {
        return ad_role_name;
    }

    public void setAd_role_name(String ad_role_name) {
        this.ad_role_name = ad_role_name;
    }

    public String getAd_org_uuid() {
        return ad_org_uuid;
    }

    public void setAd_org_uuid(String ad_org_uuid) {
        this.ad_org_uuid = ad_org_uuid;
    }

    public String getAd_client_uuid() {
        return ad_client_uuid;
    }

    public void setAd_client_uuid(String ad_client_uuid) {
        this.ad_client_uuid = ad_client_uuid;
    }

    public String getAd_user_uuid() {
        return ad_user_uuid;
    }

    public void setAd_user_uuid(String ad_user_uuid) {
        this.ad_user_uuid = ad_user_uuid;
    }

    public String getAd_user_name() {
        return ad_user_name;
    }

    public void setAd_user_name(String ad_user_name) {
        this.ad_user_name = ad_user_name;
    }

    public String getProject_uuid() {
        return project_uuid;
    }

    public void setProject_uuid(String project_uuid) {
        this.project_uuid = project_uuid;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public int getC_ProjectLocation_Index() {
        return this.C_ProjectLocation_Spinner_Index;
    }

    public void setC_ProjectLocation_Spinner_Index(int c_ProjectLocation_Spinner_Index) {
        C_ProjectLocation_Spinner_Index = c_ProjectLocation_Spinner_Index;
    }

    public String getC_projectlocation_uuid() {
        return c_projectlocation_uuid;
    }

    public void setC_projectlocation_uuid(String c_projectlocation_uuid) {
        this.c_projectlocation_uuid = c_projectlocation_uuid;
    }

    public String getC_projectlocation_name() {
        return c_projectlocation_name;
    }

    public void setC_projectlocation_name(String c_projectlocation_name) {
        this.c_projectlocation_name = c_projectlocation_name;
    }


    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }


    public PBSProjLocJSON[] getProjLocJSON() {
        return projLocJSON;
    }

    public void setProjLocJSON(PBSProjLocJSON[] projLocJSON) {
        this.projLocJSON = projLocJSON;
    }

    public String getC_projectlocation_id() {
        return c_projectlocation_id;
    }

    public void setC_projectlocation_id(String c_projectlocation_id) {
        this.c_projectlocation_id = c_projectlocation_id;
    }

    public boolean isInitialSynced() {
        return isInitialSynced;
    }

    public void setIsInitialSynced(boolean isSyncCompleted) {
        this.isInitialSynced = isSyncCompleted;
    }

    public boolean isFirstBatchSynced() {
        return isFirstBatchSynced;
    }

    public void setIsFirstBatchSynced(boolean isFirstBatchSynced) {
        this.isFirstBatchSynced = isFirstBatchSynced;
    }
}
