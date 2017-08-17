package com.pbasolutions.android.model;

import android.graphics.Color;

import com.pbasolutions.android.json.PBSJson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pbadell on 12/2/15.
 */
public class MMovement extends PBSJson {

    public static final String TABLENAME = "M_Movement";
    public static final String AD_USER_UUID_COL = "AD_User_UUID";
    public static final String AD_USER_ID_COL = "AD_User_ID";
    public static String M_MOVEMENT_ID_COL = "M_Movement_ID";
    public static String M_MOVEMENT_UUID_COL = "M_Movement_UUID";
    public static String DOCUMENTNO_COL = "DocumentNo";
    public static String DESCRIPTION_COL = "Description";
    public static String MOVEMENTDATE_COL = "MovementDate";
    public static String DOCSTATUS_COL = "DocStatus";
    public static String C_PROJECTLOCATION_ID_COL = "C_ProjectLocation_ID";
    public static String C_PROJECTLOCATIONTO_ID_COL = "C_ProjectLocationTo_ID";
    public static String C_PROJECTLOCATION_UUID_COL = "C_ProjectLocation_UUID";
    public static String C_PROJECTLOCATIONTO_UUID_COL = "C_ProjectLocationTo_UUID";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_INACTIVE ="INACTIVE";
    public static final String STATUS_APPROVED ="APPROVED";
    public static final String STATUS_CHANGED ="CHANGED";
    public static final String STATUS_CLOSED ="CLOSED";
    public static final String STATUS_DRAFTED ="DRAFTED";
    public static final String STATUS_NOT_APPROVED ="NOT APPROVED";
    public static final String STATUS_POSTING_ERROR = "POSTING ERROR";
    public static final String STATUS_POSTED = "POSTED";
    public static final String STATUS_PRINTED ="PRINTED";
    public static final String STATUS_REVERSED ="REVERSED";
    public static final String STATUS_TRANSFER_ERROR ="TRANSFER ERROR";
    public static final String STATUS_TRANSFERRED ="TRANSFERRED";
    public static final String STATUS_VOIDED = "VOIDED";
    public static final String STATUS_PROCESSED ="PROCESSED";
    public static final String STATUS_BEING_PROCESSED ="BEING PROCESSED";

    public static final String STATUS_CO="CO";
    public static final String STATUS_IN="IN";
    public static final String STATUS_RE="RE";
    public static final String STATUS_DR="DR";
    public static final String STATUS_VO="VO";
    public static final String STATUS_AP="AP";
    public static final String STATUS_CH="CH";
    public static final String STATUS_CL="CL";
    public static final String STATUS_NA="NA";
    public static final String STATUS_PE="PE";
    public static final String STATUS_PO="PO";
    public static final String STATUS_PR="PR";
    public static final String STATUS_TE="TE";
    public static final String STATUS_TR="TR";
    public static final String STATUS_XX="XX";

    private Number M_Movement_ID;
    private String M_Movement_UUID;
    private String MovementDate;
    private String DocStatus;
    private String DocumentNo;
    private String Status;
    private Number C_ProjectLocation_ID;
    private Number C_ProjectLocationTo_ID;
    private MMovementLine Lines[];
    private Number Product_ID_List[];
    private String ProjectLocation;
    private String ProjectLocationTo;
    private String ProjectLocation_UUID;
    private String ProjectLocationTo_UUID;
    private String AD_User_UUID;
    private Number AD_User_ID;
    private int StatusColor;
    Date MovementDateFormat;

    public Number getM_Movement_ID() {
        return M_Movement_ID;
    }

    public void setM_Movement_ID(Number m_Movement_ID) {
        M_Movement_ID = m_Movement_ID;
    }

    public String getMovementDate() {
        return MovementDate;
    }

    public void setMovementDate(String movementDate) {
        MovementDate = movementDate;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = formatter.parse(movementDate);
            setMovementDateFormat(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getDocStatus() {
        return DocStatus;
    }

    public void setDocStatus(String docStatus) {
        DocStatus = docStatus;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;

        switch(Status){
            case MMovement.STATUS_COMPLETED: {
                setStatusColor(Color.GREEN);
                break;
            }

            case MMovement.STATUS_INACTIVE: {
                setStatusColor(Color.RED);
                break;
            }
            case MMovement.STATUS_REVERSED: {
                setStatusColor(Color.BLUE);
                break;
            }
            case MMovement.STATUS_DRAFTED: {
                setStatusColor(Color.DKGRAY);
                break;
            }
            case MMovement.STATUS_VOIDED: {
                setStatusColor(Color.GREEN);
                break;
            }
            case MMovement.STATUS_APPROVED: {
                setStatusColor(Color.GREEN);
                break;
            }
            case MMovement.STATUS_CHANGED: {
                setStatusColor(Color.BLUE);
                break;
            }
            case MMovement.STATUS_CLOSED: {
                setStatusColor(Color.RED);
                break;
            }
            case MMovement.STATUS_NOT_APPROVED: {
                setStatusColor(Color.RED);
                break;
            }
            case MMovement.STATUS_POSTING_ERROR: {
                setStatusColor(Color.RED);
                break;
            }
            case MMovement.STATUS_POSTED: {
                setStatusColor(Color.GREEN);
                break;
            }
            case MMovement.STATUS_PRINTED: {
                setStatusColor(Color.MAGENTA);
                break;
            }
            case MMovement.STATUS_TRANSFER_ERROR: {
                setStatusColor(Color.RED);
                break;
            }
            case MMovement.STATUS_TRANSFERRED: {
                setStatusColor(Color.GREEN);
                break;
            }
            case MMovement.STATUS_BEING_PROCESSED: {
                setStatusColor(Color.DKGRAY);
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

    public Number getC_ProjectLocation_ID() {
        return C_ProjectLocation_ID;
    }

    public void setC_ProjectLocation_ID(Number c_ProjectLocation_ID) {
        C_ProjectLocation_ID = c_ProjectLocation_ID;
    }

    public Number getC_ProjectLocationTo_ID() {
        return C_ProjectLocationTo_ID;
    }

    public void setC_ProjectLocationTo_ID(Number c_ProjectLocationTo_ID) {
        C_ProjectLocationTo_ID = c_ProjectLocationTo_ID;
    }

    public MMovementLine[] getLines() {
        return Lines;
    }

    public void setLines(MMovementLine[] lines) {
        this.Lines = lines;
    }

    public Number[] getProduct_ID_List() {
        return Product_ID_List;
    }

    public void setProduct_ID_List(Number[] product_ID_List) {
        Product_ID_List = product_ID_List;
    }

    public String getDocumentNo() {
        return DocumentNo;
    }

    public void setDocumentNo(String documentNo) {
        DocumentNo = documentNo;
    }

    public String getProjectLocation() {
        return ProjectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        ProjectLocation = projectLocation;
    }

    public String getProjectLocationTo() {
        return ProjectLocationTo;
    }

    public void setProjectLocationTo(String projectLocationTo) {
        ProjectLocationTo = projectLocationTo;
    }

    public String getM_Movement_UUID() {
        return M_Movement_UUID;
    }

    public void setM_Movement_UUID(String m_Movement_UUID) {
        M_Movement_UUID = m_Movement_UUID;
    }

    public String getProjectLocation_UUID() {
        return ProjectLocation_UUID;
    }

    public void setProjectLocation_UUID(String projectLocation_UUID) {
        ProjectLocation_UUID = projectLocation_UUID;
    }

    public String getProjectLocationTo_UUID() {
        return ProjectLocationTo_UUID;
    }

    public void setProjectLocationTo_UUID(String projectLocationTo_UUID) {
        ProjectLocationTo_UUID = projectLocationTo_UUID;
    }

    public String getAD_User_UUID() {
        return AD_User_UUID;
    }

    public void setAD_User_UUID(String AD_User_UUID) {
        this.AD_User_UUID = AD_User_UUID;
    }

    public Number getAD_User_ID() {
        return AD_User_ID;
    }

    public void setAD_User_ID(Number AD_User_ID) {
        this.AD_User_ID = AD_User_ID;
    }

    public void setMovementDateFormat(Date movementDateFormat){ MovementDateFormat = movementDateFormat;}

    public Date getMovementDateFormat(){ return MovementDateFormat;}
}
