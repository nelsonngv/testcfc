package com.pbasolutions.android.model;

import java.io.Serializable;

/**
 * Created by pbadell on 10/29/15.
 */
public interface IModel {
    public void set_UUID(String _UUID);
    public String get_UUID();
    public void set_ID(int _ID);
    public int get_ID();
    public void setIsSelected(boolean flag);
    public boolean isSelected();
}
