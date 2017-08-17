package com.pbasolutions.android.model;

import android.graphics.Color;

import com.pbasolutions.android.json.PBSJson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pbadell on 10/12/15.
 */
public class MPurchaseRequest extends PBSJson {

    public static String TABLENAME = "M_PurchaseRequest";
    public static String M_PURCHASEREQUEST_ID_COL = "M_PurchaseRequest_ID";
    public static String M_PURCHASEREQUEST_UUID_COL = "M_PurchaseRequest_UUID";
    public static String C_PROJECTLOCATION_ID_COL = "C_ProjectLocation_ID";
    public static String C_PROJECTLOCATION_UUID_COL = "C_ProjectLocation_UUID";
    public static String AD_USER_ID_COL = "AD_User_ID";
    public static String AD_USER_UUID_COL = "AD_User_UUID";
    public static String DOCUMENTNO_COL = "DocumentNo";
    public static String REQUESTDATE_COL = "RequestDate";
    public static String ISAPPROVED_COL = "IsApproved";

    String M_PurchaseRequest_ID;
    String M_PurchaseRequest_UUID;
    String C_ProjectLocation_ID;
    String C_ProjectLocation_UUID;
    String AD_User_UUID;
    String IsApproved;
    String AD_User_ID;
    String RequestDate;
    String DocumentNo;
    String UserName;
    String Status;
    Date RequestDateFormat;
    MPurchaseRequestLine[] Lines;
    String ProjLocName;
    private int StatusColor;

    public String getM_PurchaseRequest_ID() {
        return M_PurchaseRequest_ID;
    }

    public void setM_PurchaseRequest_ID(String m_PurchaseRequest_ID) {
        M_PurchaseRequest_ID = m_PurchaseRequest_ID;
    }

    public String getC_ProjectLocation_ID() {
        return C_ProjectLocation_ID;
    }

    public void setC_ProjectLocation_ID(String c_ProjectLocation_ID) {
        C_ProjectLocation_ID = c_ProjectLocation_ID;
    }

    public String getIsApproved() {
        return IsApproved;
    }

    public void setIsApproved(String isApproved) {
        IsApproved = isApproved;
    }

    public String getAD_User_ID() {
        return AD_User_ID;
    }

    public void setAD_User_ID(String AD_User_ID) {
        this.AD_User_ID = AD_User_ID;
    }

    public MPurchaseRequestLine[] getLines() {
        return Lines;
    }

    public void setLines(MPurchaseRequestLine[] lines) {
        Lines = lines;
    }

    public String getDocumentNo() {
        return DocumentNo;
    }

    public void setDocumentNo(String documentNo) {
        DocumentNo = documentNo;
    }

    public String getM_PurchaseRequest_UUID() {
        return M_PurchaseRequest_UUID;
    }

    public void setM_PurchaseRequest_UUID(String m_PurchaseRequest_UUID) {
        M_PurchaseRequest_UUID = m_PurchaseRequest_UUID;
    }

    public String getC_ProjectLocation_UUID() {
        return C_ProjectLocation_UUID;
    }

    public void setC_ProjectLocation_UUID(String c_ProjectLocation_UUID) {
        C_ProjectLocation_UUID = c_ProjectLocation_UUID;
    }

    public String getAD_User_UUID() {
        return AD_User_UUID;
    }

    public void setAD_User_UUID(String AD_User_UUID) {
        this.AD_User_UUID = AD_User_UUID;
    }


    public String getProjLocName() {
        return ProjLocName;
    }

    public void setProjLocName(String projLocName) {
        ProjLocName = projLocName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getRequestDate() {
        return RequestDate;
    }

    public void setRequestDate(String requestDate) {
        RequestDate = requestDate;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = formatter.parse(requestDate);
            setRequestDateFormat(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
        switch(Status){
            case "WAITING APPROVAL": {
                setStatusColor(Color.RED);
                break;
            }

            case "APPROVED": {
                setStatusColor(Color.parseColor("#32CD32"));
                break;
            }
            default:
        }
    }


    public int getStatusColor() {
        return StatusColor;
    }

    public void setStatusColor(int statusColor) {
        StatusColor = statusColor;
    }

    public void setRequestDateFormat(Date requestDateFormat){ RequestDateFormat = requestDateFormat;}

    public Date getRequestDateFormat(){ return RequestDateFormat;}

}
