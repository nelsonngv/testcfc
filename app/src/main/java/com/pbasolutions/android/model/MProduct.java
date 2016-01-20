package com.pbasolutions.android.model;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by pbadell on 11/23/15.
 */
public class MProduct extends PBSJson {

    public static final String VALUE_COL = "Value" ;
    public static final String TABLENAME = "M_Product";
    //Columns name:
    public static String M_PRODUCT_ID_COL = "M_Product_ID";
    public static String M_PRODUCT_UUID_COL = "M_Product_UUID";
    public static String C_UOM_ID_COL = "C_UOM_ID";
    public static String C_UOM_UUID_COL = "C_UOM_UUID";
    public static String NAME_COL = "Name";
    public static String M_ATTRIBUTESETINSTANCE_ID_COL = "M_AttributeSetInstance_ID";
    public static String M_ATTRIBUTESETINSTANCE_UUID_COL = "M_AttributeSetInstance_UUID";

    String M_Product_ID;
    String M_Product_UUID;
    String C_UOM_ID;
    String Name;
    String M_AttributeSetInstance_ID;

    public String getM_Product_ID() {
        return M_Product_ID;
    }

    public void setM_Product_ID(String m_Product_ID) {
        M_Product_ID = m_Product_ID;
    }

    public String getM_Product_UUID() {
        return M_Product_UUID;
    }

    public void setM_Product_UUID(String m_Product_UUID) {
        M_Product_UUID = m_Product_UUID;
    }

    public String getC_UOM_ID() {
        return C_UOM_ID;
    }

    public void setC_UOM_ID(String c_UOM_ID) {
        C_UOM_ID = c_UOM_ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getM_AttributeSetInstance_ID() {
        return M_AttributeSetInstance_ID;
    }

    public void setM_AttributeSetInstance_ID(String m_AttributeSetInstance_ID) {
        M_AttributeSetInstance_ID = m_AttributeSetInstance_ID;
    }
}
