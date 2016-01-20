package com.pbasolutions.android.json;

/**
 * Created by pbadell on 6/30/15.
 */
public class PBSSyncJSON extends PBSJson {

    private PBSTableJSON New [];
    private PBSTableJSON Update [];
    private PBSTableJSON Delete[];

    public PBSTableJSON[] getNew() {
        return New;
    }

    public void setNew(PBSTableJSON[] data) {
        New = data;
    }

    public PBSTableJSON[] getDelete() {
        return Delete;
    }

    public void setDelete(PBSTableJSON[] delete) {
        Delete = delete;
    }

    public PBSTableJSON[] getUpdate() {
        return Update;
    }

    public void setUpdate(PBSTableJSON[] update) {
        Update = update;
    }
}
