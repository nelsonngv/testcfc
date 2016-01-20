package com.pbasolutions.android.model;

import android.content.ContentResolver;
import android.database.Cursor;

import com.pbasolutions.android.json.IPBSJson;

import java.io.Serializable;

/**
 * Created by pbadell on 12/2/15.
 */
public class MMovementLine implements IPBSJson, IModel, Serializable {
    public static final String ASI_DESCRIPTION_COL = "ASI_DESCRIPTION";
    public static final String M_MOVEMENTLINE_ID_COL = "M_MovementLine_ID";
    public static final String M_MOVEMENTLINE_UUID_COL = "M_MovementLine_UUID";
    public static final String M_MOVEMENT_ID_COL = "M_Movement_ID";
    public static final String M_MOVEMENT_UUID_COL = "M_Movement_UUID";
    public static final String M_PRODUCT_ID_COL = "M_Product_ID";
    public static final String M_PRODUCT_UUID_COL = "M_Product_UUID";
    public static final String MOVEMENTQTY_COL = "MovementQty";
    public static final String M_ATTRIBUTESETINSTANCE_ID_COL= "M_AttributeSetInstance_ID";
    public static final String M_ATTRIBUTESETINSTANCE_UUID_COL= "M_AttributeSetInstance_UUID";
    public static final String C_UOM_ID_COL = "C_UOM_ID";
    public static final String C_UOM_UUID_COL = "C_UOM_UUID";
    public static final String TABLENAME = "M_MovementLine";

    private Number M_MovementLine_ID;
    private String M_MovementLine_UUID;
    private Number M_Movement_ID;
    private String M_Movement_UUID;
    private Number M_Product_ID;
    private String M_Product_UUID;
    private String ProductName;
    private String ProductValue;
    private Number MovementQty;
    private int QtyOnHand;
    private Number M_AttributeSetInstance_ID;
    private String M_AttributeSetInstance_UUID;
    private String ASI_Description;
    private String Description;
    private Number C_UOM_ID;
    private String C_UOM_UUID;
    private String UOMName;
    private String UOMSymbol;

    private boolean isSelected = false;
    String _UUID;
    int _ID;

    public Number getM_MovementLine_ID() {
        return M_MovementLine_ID;
    }

    public void setM_MovementLine_ID(Number m_MovementLine_ID) {
        M_MovementLine_ID = m_MovementLine_ID;
    }

    public Number getMovementQty() {
        return MovementQty;
    }

    public void setMovementQty(Number movementQty) {
        MovementQty = movementQty;
    }

    public Number getM_AttributeSetInstance_ID() {
        return M_AttributeSetInstance_ID;
    }

    public void setM_AttributeSetInstance_ID(Number m_AttributeSetInstance_ID) {
        M_AttributeSetInstance_ID = m_AttributeSetInstance_ID;
    }

    public String getASI_Description() {
        return ASI_Description;
    }

    public void setASI_Description(String ASI_Description) {
        this.ASI_Description = ASI_Description;
    }

    public Number getC_UOM_ID() {
        return C_UOM_ID;
    }

    public void setC_UOM_ID(Number c_UOM_ID) {
        C_UOM_ID = c_UOM_ID;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String get_UUID() {
        return _UUID;
    }

    public void set_UUID(String _UUID) {
        this._UUID = _UUID;
    }

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getM_MovementLine_UUID() {
        return M_MovementLine_UUID;
    }

    public void setM_MovementLine_UUID(String m_MovementLine_UUID) {
        M_MovementLine_UUID = m_MovementLine_UUID;
    }

    public String getM_Movement_UUID() {
        return M_Movement_UUID;
    }

    public void setM_Movement_UUID(String m_Movement_UUID) {
        M_Movement_UUID = m_Movement_UUID;
    }

    public Number getM_Movement_ID() {
        return M_Movement_ID;
    }

    public void setM_Movement_ID(Number m_Movement_ID) {
        M_Movement_ID = m_Movement_ID;
    }

    public String getM_Product_UUID() {
        return M_Product_UUID;
    }

    public void setM_Product_UUID(String m_Product_UUID) {
        M_Product_UUID = m_Product_UUID;
    }

    public Number getM_Product_ID() {
        return M_Product_ID;
    }

    public void setM_Product_ID(Number m_Product_ID) {
        M_Product_ID = m_Product_ID;
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

    public String getM_AttributeSetInstance_UUID() {
        return M_AttributeSetInstance_UUID;
    }

    public void setM_AttributeSetInstance_UUID(String m_AttributeSetInstance_UUID) {
        M_AttributeSetInstance_UUID = m_AttributeSetInstance_UUID;
    }

    public String getC_UOM_UUID() {
        return C_UOM_UUID;
    }

    public void setC_UOM_UUID(String c_UOM_UUID) {
        C_UOM_UUID = c_UOM_UUID;
    }

    public String getUOMName() {
        return UOMName;
    }

    public void setUOMName(String UOMName) {
        this.UOMName = UOMName;
    }

    public String getUOMSymbol() {
        return UOMSymbol;
    }

    public void setUOMSymbol(String UOMSymbol) {
        this.UOMSymbol = UOMSymbol;
    }

    public static MMovementLine populateMovementLine(Cursor cursor, ContentResolver cr) {
        MMovementLine mvl = new MMovementLine();
        if (cursor != null && cursor.getCount() > 0) {
            for (int x = 0; x < cursor.getColumnNames().length; x++) {
                String columnName = cursor.getColumnName(x);
                String rowValue = cursor.getString(x);
                if (MMovementLine.M_MOVEMENTLINE_ID_COL
                        .equalsIgnoreCase(columnName)) {
                    mvl.set_ID(cursor.getInt(x));
                    mvl.setM_MovementLine_ID(cursor.getInt(x));
                } else if (MMovementLine.M_MOVEMENTLINE_UUID_COL
                        .equalsIgnoreCase(columnName)) {
                    mvl.set_UUID(rowValue);
                    mvl.setM_MovementLine_UUID(rowValue);
                } else if (MMovementLine.M_MOVEMENT_UUID_COL
                        .equalsIgnoreCase(columnName)) {
                    mvl.setM_Movement_UUID(rowValue);
                    mvl.setM_Movement_UUID(rowValue);
                } else if (MMovementLine.M_PRODUCT_UUID_COL
                        .equalsIgnoreCase(columnName)) {
                    mvl.setM_Product_UUID(rowValue);
                    String prodName = ModelConst.mapUUIDtoColumn(ModelConst.M_PRODUCT_TABLE,
                            MProduct.M_PRODUCT_UUID_COL,
                            rowValue, MProduct.NAME_COL, cr);
                    String prodVal = ModelConst.mapUUIDtoColumn(ModelConst.M_PRODUCT_TABLE,
                            MProduct.M_PRODUCT_UUID_COL,
                            rowValue, MProduct.VALUE_COL, cr);
                    mvl.setProductName(prodName);
                    mvl.setProductValue(prodVal);
                    String productID = ModelConst.mapUUIDtoColumn(ModelConst.M_PRODUCT_TABLE,
                            MProduct.M_PRODUCT_UUID_COL,
                            rowValue, MProduct.M_PRODUCT_ID_COL, cr);
                    mvl.setM_Product_ID(Integer.parseInt(productID));
                } else if (MMovementLine.MOVEMENTQTY_COL
                        .equalsIgnoreCase(columnName)) {
                    mvl.setMovementQty(cursor.getDouble(x));
                } else if (MMovementLine.M_ATTRIBUTESETINSTANCE_ID_COL
                        .equalsIgnoreCase(columnName)) {
                    mvl.setM_AttributeSetInstance_ID(cursor.getInt(x));
                } else if (MMovementLine.ASI_DESCRIPTION_COL
                        .equalsIgnoreCase(columnName)) {
                    mvl.setASI_Description(rowValue);
                    mvl.setDescription(rowValue);
                } else if (MMovementLine.C_UOM_UUID_COL
                        .equalsIgnoreCase(columnName)) {
                    mvl.setC_UOM_UUID(rowValue);
                    String uomName = ModelConst.mapUUIDtoColumn(MUOM.TABLENAME,
                            MUOM.C_UOM_UUID_COL,
                            rowValue, MUOM.NAME_COL, cr);
                    mvl.setUOMName(uomName);
                    String uomSymbol = ModelConst.mapUUIDtoColumn(MUOM.TABLENAME,
                            MUOM.C_UOM_UUID_COL, rowValue, MUOM.UOMSYMBOL_COL, cr);
                    mvl.setUOMSymbol(uomSymbol);
                    String uomID = ModelConst.mapUUIDtoColumn(MUOM.TABLENAME,
                            MUOM.C_UOM_UUID_COL, rowValue, MUOM.C_UOM_ID_COL, cr);
                    mvl.setC_UOM_ID(Integer.parseInt(uomID));
                }
            }
        }
        return mvl;
    }

    public int getQtyOnHand() {
        return QtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        QtyOnHand = qtyOnHand;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
