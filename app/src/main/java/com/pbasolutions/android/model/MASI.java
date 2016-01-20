package com.pbasolutions.android.model;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by pbadell on 12/7/15.
 */
public class MASI extends PBSJson {
    public static final String M_ATTRIBUTESETINSTANCE_ID_COL = "M_AttributeSetInstance_ID";
    public static final String M_ATTRIBUTESETINSTANCE_UUID_COL = "M_AttributeSetInstance_UUID";
    public static final String DESCRIPTION_COL = "Description";

    String M_AttributeSetInstance_ID;
    String M_AttributSetInstance_UUID;
    String Description;

    public String getM_AttributeSetInstance_ID() {
        return M_AttributeSetInstance_ID;
    }

    public void setM_AttributeSetInstance_ID(String m_AttributeSetInstance_ID) {
        M_AttributeSetInstance_ID = m_AttributeSetInstance_ID;
    }

    public String getM_AttributSetInstance_UUID() {
        return M_AttributSetInstance_UUID;
    }

    public void setM_AttributSetInstance_UUID(String m_AttributSetInstance_UUID) {
        M_AttributSetInstance_UUID = m_AttributSetInstance_UUID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

}
