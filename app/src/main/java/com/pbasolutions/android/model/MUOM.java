package com.pbasolutions.android.model;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by pbadell on 12/7/15.
 */
public class MUOM extends PBSJson {
    public static final String C_UOM_UUID_COL = "C_UOM_UUID";
    public static final String C_UOM_ID_COL = "C_UOM_ID";
    public static final String NAME_COL = "Name";
    public static final String DESCRIPTION = "Description";
    public static final String UOMSYMBOL_COL = "UOMSymbol";
    public static final String TABLENAME = "C_UOM";

    private String C_UOM_UUID;
    private Number C_UOM_ID;
    private String Description;
    private String UOMSymbol;
    private String Name;

    public String getC_UOM_UUID() {
        return C_UOM_UUID;
    }

    public void setC_UOM_UUID(String c_UOM_UUID) {
        C_UOM_UUID = c_UOM_UUID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUOMSymbol() {
        return UOMSymbol;
    }

    public void setUOMSymbol(String UOMSymbol) {
        this.UOMSymbol = UOMSymbol;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Number getC_UOM_ID() {
        return C_UOM_ID;
    }

    public void setC_UOM_ID(Number c_UOM_ID) {
        C_UOM_ID = c_UOM_ID;
    }
}
