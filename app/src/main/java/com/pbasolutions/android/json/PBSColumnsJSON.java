package com.pbasolutions.android.json;

import java.io.Serializable;

/**
 * Created by pbadell on 7/6/15.
 */
public class PBSColumnsJSON implements Serializable {

    String Name;
    Object Value;

    public PBSColumnsJSON(String name, Object value) {
        Name = name;
        Value = value;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Object getValue() {
        return Value;
    }

    public void setValue(String value) {
        if (value == null){
            Value = "null";
        } else {
            Value = value;
        }

    }


}
