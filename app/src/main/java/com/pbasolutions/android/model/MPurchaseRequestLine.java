package com.pbasolutions.android.model;

import com.pbasolutions.android.json.IPBSJson;

import java.io.Serializable;

/**
 * Created by pbadell on 11/13/15.
 */
public class MPurchaseRequestLine implements IPBSJson, IModel, Serializable {
    /**
     * Column names.
     */
    public static String TABLENAME = "M_PurchaseRequestLine";
    public static String M_PURCHASEREQUESTLINE_ID_COL = "M_PurchaseRequestLine_ID";
    public static String M_PURCHASEREQUESTLINE_UUID_COL = "M_PurchaseRequestLine_UUID";
    public static String M_PURCHASEREQUEST_ID_COL = "M_PurchaseRequest_ID";
    public static String M_PURCHASEREQUEST_UUID_COL = "M_PurchaseRequest_UUID";
    public static String M_PRODUCT_ID_COL = "M_Product_ID";
    public static String M_PRODUCT_UUID_COL = "M_Product_UUID";
    public static String DATEREQUIRED_COL = "DateRequired";
    public static String QTYREQUESTED_COL = "QtyRequested";
    public static String ISEMERGENCY_COL = "IsEmergency";
    public static String PURCHASEREASON_COL = "PurchaseReason";

    /**
     * Column setters getters.
     */
    private String M_PurchaseRequestLine_ID;
    private String M_PurchaseRequestLine_UUID;
    private String M_PurchaseRequest_ID;
    private String M_PurchaseRequest_UUID;
    private String M_Product_ID;
    private String M_Product_UUID;
    private String DateRequired;
    private int Line;
    private int QtyRequested;
    private String ProductName;
    private String ProductValue;
    private String QtyRequestedString;
    private String IsEmergency;
    private String PurchaseReason;
    private boolean isSelected = false;
    String _UUID;
    int _ID;
    public String getM_Product_UUID() {
        return M_Product_UUID;
    }

    public void setM_Product_UUID(String m_Product_UUID) {
        M_Product_UUID = m_Product_UUID;
    }

    public String getM_PurchaseRequestLine_ID() {
        return M_PurchaseRequestLine_ID;
    }

    public void setM_PurchaseRequestLine_ID(String m_PurchaseRequestLine_ID) {
        M_PurchaseRequestLine_ID = m_PurchaseRequestLine_ID;
    }

    public String getM_PurchaseRequestLine_UUID() {
        return M_PurchaseRequestLine_UUID;
    }

    public void setM_PurchaseRequestLine_UUID(String m_PurchaseRequestLine_UUID) {
        M_PurchaseRequestLine_UUID = m_PurchaseRequestLine_UUID;
    }

    public String getM_Product_ID() {
        return M_Product_ID;
    }

    public void setM_Product_ID(String m_Product_ID) {
        M_Product_ID = m_Product_ID;
    }

    public String getDateRequired() {
        return DateRequired;
    }

    public void setDateRequired(String dateRequired) {
        DateRequired = dateRequired;
    }

    public int getQtyRequested() {
        return QtyRequested;
    }

    public void setQtyRequested(int qtyRequested) {
        QtyRequested = qtyRequested;
    }

    public String getM_PurchaseRequest_ID() {
        return M_PurchaseRequest_ID;
    }

    public void setM_PurchaseRequest_ID(String m_PurchaseRequest_ID) {
        M_PurchaseRequest_ID = m_PurchaseRequest_ID;
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

    public String getQtyRequestedString() {
        return QtyRequestedString;
    }

    public void setQtyRequestedString(String qtyRequestedString) {
        QtyRequestedString = qtyRequestedString;
    }

    public String getM_PurchaseRequest_UUID() {
        return M_PurchaseRequest_UUID;
    }

    public void setM_PurchaseRequest_UUID(String m_PurchaseRequest_UUID) {
        M_PurchaseRequest_UUID = m_PurchaseRequest_UUID;
    }

    public String getIsEmergency() {
        return IsEmergency;
    }

    public void setIsEmergency(String isEmergency) {
        IsEmergency = isEmergency;
    }

    public String getPurchaseReason() {
        return PurchaseReason;
    }

    public void setPurchaseReason(String purchaseReason) {
        PurchaseReason = purchaseReason;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public void set_UUID(String _UUID) {
        this._UUID = _UUID;
    }

    @Override
    public String get_UUID() {
        return _UUID;
    }

    @Override
    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public int get_ID() {
        return _ID;
    }
}
