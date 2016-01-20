package com.pbasolutions.android.model;

import com.pbasolutions.android.json.PBSJson;

/**
 * Created by pbadell on 12/1/15.
 */
public class MStorage extends PBSJson {

    public static final String M_PRODUCT_ID_COL = "M_Product_ID";
    private Number A_Asset_ID;
    private Number M_Product_ID;
    private Number M_AttributeSetInstance_ID;
    private Number QtyOnHand;
    private Number C_ProjectLocation_ID;
    private String ASI_Desc;
    private String Description;
    private String ProductName;
    private String ProductValue;
    private Number Product_ID_List[];


    public Number getA_Asset_ID() {
        return A_Asset_ID;
    }

    public void setA_Asset_ID(Number a_Asset_ID) {
        A_Asset_ID = a_Asset_ID;
    }

    public Number getM_Product_ID() {
        return M_Product_ID;
    }

    public void setM_Product_ID(Number m_Product_ID) {
        M_Product_ID = m_Product_ID;
    }

    public Number getQtyOnHand() {
        return QtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        QtyOnHand = qtyOnHand;
    }

    public Number getC_ProjectLocation_ID() {
        return C_ProjectLocation_ID;
    }

    public void setC_ProjectLocation_ID(Number c_ProjectLocation_ID) {
        C_ProjectLocation_ID = c_ProjectLocation_ID;
    }

    public String getASI_Desc() {
        return ASI_Desc;
    }

    public void setASI_Desc(String ASI_Desc) {
        this.ASI_Desc = ASI_Desc;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductValue() {
        return ProductValue;
    }

    public void setProductValue(String productValue) {
        ProductValue = productValue;
    }

    public Number[] getProduct_ID_List() {
        return Product_ID_List;
    }

    public void setProduct_ID_List(Number[] product_ID_List) {
        Product_ID_List = product_ID_List;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Number getM_AttributeSetInstance_ID() {
        return M_AttributeSetInstance_ID;
    }

    public void setM_AttributeSetInstance_ID(Number m_AttributeSetInstance_ID) {
        M_AttributeSetInstance_ID = m_AttributeSetInstance_ID;
    }
}
