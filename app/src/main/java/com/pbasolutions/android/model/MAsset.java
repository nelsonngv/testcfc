package com.pbasolutions.android.model;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by pbadell on 10/8/15.
 */
public class MAsset extends PBSJson {

    public static final String TABLENAME = "A_Asset";
    public static final String A_ASSET_ID_COL = "A_Asset_ID";
    public static final String A_ASSET_UUID_COL = "A_Asset_UUID";
    public static final String M_PRODUCT_ID_COL = "M_Product_ID";
    public static final String M_PRODUCT_UUID_COL = "M_Product_UUID";
    public static final String NAME_COL = "Name";
    public static final String VALUE_COL = "Value";
    public static final String ARG_M_PRODUCT_ID_LIST = "M_Product_ID_List";

    private String A_Asset_ID;
    private String A_Asset_UUID;
    private String M_Product_ID;
    private String Name;
    private String Value;

    public String getA_Asset_ID() {
        return A_Asset_ID;
    }

    public void setA_Asset_ID(String a_Asset_ID) {
        A_Asset_ID = a_Asset_ID;
    }

    public String getA_Asset_UUID() {
        return A_Asset_UUID;
    }

    public void setA_Asset_UUID(String a_Asset_UUID) {
        A_Asset_UUID = a_Asset_UUID;
    }

    public String getM_Product_ID() {
        return M_Product_ID;
    }

    public void setM_Product_ID(String m_Product_ID) {
        M_Product_ID = m_Product_ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
