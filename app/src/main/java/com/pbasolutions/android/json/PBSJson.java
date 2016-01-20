package com.pbasolutions.android.json;

import android.databinding.BaseObservable;

import com.google.gson.annotations.Expose;
import com.pbasolutions.android.model.IModel;

import java.io.Serializable;

/**
 * Created by pbadell on 8/19/15.
 */
public class PBSJson
        extends BaseObservable
        implements  IPBSJson, IModel, Serializable {

    String _UUID;
    int _ID;
    private boolean isSelected = false;
    public PBSJson() {
    }

    /**
     * Success returned value.
     */
    private String Success;
    private String InvalidSession;

    public String getSuccess() {
        return Success;
    }

    public void setSuccess(String success) {
        Success = success;
    }

    public String getInvalidSession() {
        return InvalidSession;
    }

    public void setInvalidSession(String invalidSession) {
        InvalidSession = invalidSession;
    }

    @Override
    public void set_UUID(String _UUID) {
        this._UUID = _UUID;
    }

    @Override
    public String get_UUID() {
        return _UUID;
    }

    public int get_ID() {
        return _ID;
    }

    @Override
    public void setIsSelected(boolean flag) {
        this.isSelected = flag;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }
}
