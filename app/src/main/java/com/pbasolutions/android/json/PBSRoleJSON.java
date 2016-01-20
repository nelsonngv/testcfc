package com.pbasolutions.android.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pbadell on 6/25/15.
 */
public class PBSRoleJSON implements Serializable{
    /**
     * Role AD_Role_ID.
     */
    private String AD_Role_ID;
    /**
     * Role Name.
     */
    private String Name;
    /**
     * Array of Organization belong to role.
     */
    private PBSOrgJSON Orgs[];

    public PBSClientJSON[] getClients() {
        return Clients;
    }

    public void setClients(PBSClientJSON[] clients) {
        Clients = clients;
    }

    /**
     * Array of Organization belong to role.
     */
    private PBSClientJSON Clients[];
    /**
     * Get Role AD_Role_ID.
     * @return
     */
    public String getAD_Role_ID() {
        return AD_Role_ID;
    }

    /**
     * Set Role AD_Role_ID.
     * @param iD
     */
    public void setAD_Role_ID(String iD) {
        AD_Role_ID = iD;
    }

    /**
     * Get Role Name.
     * @return
     */
    public String getName() {
        return Name;
    }

    /**
     * Set Role Name.
     * @param name
     */
    public void setName(String name) {
        Name = name;
    }

    /**
     * Get Role Organizations.
     * @return
     */
    public PBSOrgJSON[] getOrgs() {
        return Orgs;
    }

    /**
     * Set Role Organizations.
     * @param orgs
     */
    public void setOrgs(PBSOrgJSON[] orgs) {
        Orgs = orgs;
    }

    public List<String> getNames(PBSRoleJSON[] roleArray){
        List<String> list = new ArrayList<String>();
        for(PBSRoleJSON role : roleArray){
            list.add(role.getName());
        }
        return list;
    }

    public static List<String> getOrgNames(PBSOrgJSON[] orgArray){
        List<String> list = new ArrayList<String>();
        for(PBSOrgJSON org : orgArray){
            list.add(org.getOrgName());
        }
        return list;
    }

    public static List<String> getClientNames(PBSClientJSON[] clientArray){
        List<String> list = new ArrayList<String>();
        for(PBSClientJSON client : clientArray){
            list.add(client.getName());
        }
        return list;
    }

    public static List<String> getProjLoc(PBSProjLocJSON[] projArray) {
        List<String> list = new ArrayList<String>();
        for (PBSProjLocJSON projLoc:projArray) {
            list.add(projLoc.getName());
        }
        return list;
    }
}
