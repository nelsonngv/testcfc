package com.pbasolutions.android.controller;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.api.PBSIServerAPI;
import com.pbasolutions.android.api.PBSServerAPI;
import com.pbasolutions.android.model.MProduct;
import com.pbasolutions.android.model.MPurchaseRequest;
import com.pbasolutions.android.model.MPurchaseRequestLine;
import com.pbasolutions.android.model.ModelConst;

import java.util.ArrayList;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by pbadell on 11/13/15.
 */
public class RequisitionTask implements Callable<Bundle> {

    private static final String TAG = "RequisitionTask";
    private Bundle input;
    private Bundle output;
    private ContentResolver cr;
    private String event;
    private boolean isSelected = false;

    public RequisitionTask(ContentResolver cr) {
        this.cr = cr;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Bundle call() throws Exception {
        switch (event) {
            case PBSRequisitionController.GET_REQUISITIONS_EVENT: {
                return getRequisitions();
            }
            case PBSRequisitionController.SYNC_REQUISITIONS_EVENT: {
                return syncRequisitions();
            }
            case PBSRequisitionController.CREATE_REQUISITION_EVENT: {
                return createRequisition();
            }
            case PBSRequisitionController.GET_REQUISITION_EVENT: {
                return getRequisition();
            }
            case PBSRequisitionController.GET_PRODUCT_LIST_EVENT: {
//                return getProductList();
                return getProductListWithValue();
            }
            case PBSRequisitionController.GET_REQUISITIONLINES_EVENT: {
                return getRequisitionLines();
            }
            case PBSRequisitionController.GET_REQUISITIONLINE_EVENT: {
                return getRequisitionLine();
            }
            case PBSRequisitionController.INSERT_REQ_EVENT: {
                return insertReq();
            }
            case PBSRequisitionController.INSERT_REQLINE_EVENT: {
                return insertReqLine();
            }

            case PBSRequisitionController.REMOVE_REQ_EVENT: {
                return removeRequisition();
            }

            case PBSRequisitionController.REMOVE_REQLINES_EVENT: {
                return removeLines();
            }

            case PBSRequisitionController.CHECK_PR_IS_REQUESTED_EVENT : {
                return isPRRequested();
            }

            case PBSRequisitionController.UPDATE_PR_LINE_EVENT: {
                return updatePRLine();
            }

            default:
                return null;
        }
    }

    private Bundle updatePRLine() {
        ContentValues cv = input.getParcelable(PBSRequisitionController.ARG_CONTENTVALUES);
        String selectionArgs[] = {input.getString(PBSRequisitionController.ARG_PURCHASEREQUESTLINE_UUID)};
            if (ModelConst.updateTableRow(cr, MPurchaseRequestLine.TABLENAME, cv,
                    MPurchaseRequestLine.M_PURCHASEREQUESTLINE_UUID_COL, selectionArgs)) {
                output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                output.putString(PandoraConstant.RESULT, "Successfully updated requisition line.");
            } else {
                output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                output.putString(PandoraConstant.ERROR, "Fail to update requisition line.");
            }
            return output;
    }

    private Bundle isPRRequested() {
        String prUUID = input.getString(PBSRequisitionController.ARG_PURCHASEREQUEST_UUID);
        //check in PR table if the PR has document no.
        String selection = MPurchaseRequest.M_PURCHASEREQUEST_UUID_COL + "=?";
        String selectArgs[] = {input.getString(PBSRequisitionController.ARG_PURCHASEREQUEST_UUID)};
        String projection[] = {MPurchaseRequest.DOCUMENTNO_COL};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(MPurchaseRequest.TABLENAME),
                projection, selection, selectArgs, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            if (MPurchaseRequest.DOCUMENTNO_COL
                    .equalsIgnoreCase(cursor.getColumnName(0))) {
                //check value
                String value = cursor.getString(0);
                boolean isRequested = false;
                if (!value.isEmpty())
                    isRequested = true;

                output.putBoolean(PBSRequisitionController.ARG_IS_PR_REQUESTED, isRequested);
            }
        }
        cursor.close();
        return output;
    }

    /**
     * @return
     */
    private Bundle removeLines() {
        ObservableArrayList<MPurchaseRequestLine> prlList = (ObservableArrayList)
                input.getSerializable(PBSRequisitionController.ARG_REQUISITIONLINE_LIST);

        ArrayList<String> uuidList = new ArrayList();
        for (MPurchaseRequestLine prlLine : prlList) {
            if (prlLine.isSelected()) {
                uuidList.add(prlLine.get_UUID());
            }
        }
        String selection = MPurchaseRequestLine.M_PURCHASEREQUESTLINE_UUID_COL + "=?";
        String uuidArray[] = uuidList.toArray(new String[uuidList.size()]);
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<>();
            for (int x=0; x<uuidList.size(); x++) {
                String[] selectionArgs = {uuidArray[x]} ;
                ops.add(ContentProviderOperation
                        .newDelete(ModelConst.uriCustomBuilder(MPurchaseRequestLine.TABLENAME))
                        .withSelection(selection, selectionArgs)
                        .build());
            }

        try {
            ContentProviderResult results[] = cr.applyBatch(PBSAccountInfo.ACCOUNT_AUTHORITY, ops);
            for (ContentProviderResult result : results) {
                boolean resultFlag = false;
                if (result.uri != null) {
                    resultFlag = ModelConst.getURIResult(result.uri);
                } else {
                    if (result.count != null && result.count != 0) {
                        resultFlag = true;
                    }
                }

                if (!resultFlag) {
                    output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                    output.putString(PandoraConstant.ERROR, "Fail to delete selected note(s).");
                    return output;
                }
            }
            output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
            output.putString(PandoraConstant.RESULT, "Successfully synced notes");
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        } catch (OperationApplicationException e) {
            Log.e(TAG, e.getMessage());
        }
        return output;
    }

    private Bundle insertReq() {
        ContentValues cv = input.getParcelable(PBSRequisitionController.ARG_CONTENTVALUES);

        String selectionArgs[] = {cv.get(MPurchaseRequest.M_PURCHASEREQUEST_UUID_COL).toString()};
        if (!ModelConst.isInsertedRow(cr, MPurchaseRequest.TABLENAME,
                MPurchaseRequest.M_PURCHASEREQUEST_UUID_COL,
                selectionArgs
        )) {
            return ModelConst.insertData(cv, cr, MPurchaseRequest.TABLENAME, output);
        } else {
            if (ModelConst.updateTableRow(cr, MPurchaseRequest.TABLENAME, cv,
                    MPurchaseRequest.M_PURCHASEREQUEST_UUID_COL, selectionArgs)) {
                output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                output.putString(PandoraConstant.RESULT, "Successfully insert updated requisition.");
            } else {
                output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                output.putString(PandoraConstant.ERROR, "Fail to update requisition.");
            }
            return output;
        }
    }

    private Bundle insertReqLine() {
        ContentValues cv = input.getParcelable(PBSRequisitionController.ARG_CONTENTVALUES);
        return ModelConst.insertData(cv, cr, MPurchaseRequestLine.TABLENAME, output);
    }

    private Bundle getRequisitionLines() {
        String selection = MPurchaseRequest.M_PURCHASEREQUEST_UUID_COL + "=?";
        String selectArgs[] = {input.getString(PBSRequisitionController.ARG_PURCHASEREQUEST_UUID)};

        ObservableArrayList<MPurchaseRequestLine> prlList = new ObservableArrayList();
        //populate the purchaseRequestLine.
        Cursor prlCursor = cr.query(ModelConst.uriCustomBuilder(MPurchaseRequestLine.TABLENAME),
                prlProjection, selection, selectArgs, null);
        if (prlCursor != null && prlCursor.getCount() > 0) {
            prlCursor.moveToFirst();
            do {
                prlList.add(populatePurchaseRequestLine(prlCursor));
            } while (prlCursor.moveToNext());
        }
        prlCursor.close();
        output.putSerializable(PBSRequisitionController.ARG_PURCHASEREQUESTLINE_LIST, prlList);

        return output;
    }

    private Bundle getRequisitionLine() {
        //get requisition line based on purchase request uuid.
        String selection = MPurchaseRequestLine.M_PURCHASEREQUESTLINE_UUID_COL + "=?";
        String selectArgs[] = {input.getString(PBSRequisitionController.ARG_PURCHASEREQUESTLINE_UUID)};

        // ObservableArrayList<MPurchaseRequestLine> prlList = new ObservableArrayList();
        //populate the purchaseRequestLine.
        Cursor prlCursor = cr.query(ModelConst.uriCustomBuilder(MPurchaseRequestLine.TABLENAME),
                prlProjection, selection, selectArgs, null);
        if (prlCursor != null && prlCursor.getCount() > 0) {
            prlCursor.moveToFirst();
            do {
                output.putSerializable(PBSRequisitionController.ARG_PURCHASEREQUESTLINE,
                        populatePurchaseRequestLine(prlCursor));
            } while (prlCursor.moveToNext());
        }
        prlCursor.close();
        return output;
    }

    String prProjection[] = {MPurchaseRequest.M_PURCHASEREQUEST_ID_COL, MPurchaseRequest.M_PURCHASEREQUEST_UUID_COL,
            MPurchaseRequest.C_PROJECTLOCATION_UUID_COL, MPurchaseRequest.AD_USER_UUID_COL,
            MPurchaseRequest.DOCUMENTNO_COL, MPurchaseRequest.ISAPPROVED_COL,
            MPurchaseRequest.REQUESTDATE_COL};

    String prlProjection[] = {MPurchaseRequestLine.M_PURCHASEREQUESTLINE_ID_COL, MPurchaseRequestLine.M_PURCHASEREQUESTLINE_UUID_COL,
            MPurchaseRequestLine.M_PURCHASEREQUEST_UUID_COL,
            MPurchaseRequestLine.M_PRODUCT_UUID_COL,
            MPurchaseRequestLine.DATEREQUIRED_COL, MPurchaseRequestLine.QTYREQUESTED_COL,
            MPurchaseRequestLine.ISEMERGENCY_COL, MPurchaseRequestLine.PURCHASEREASON_COL};

    private Bundle getRequisition() {
        String selection = MPurchaseRequest.M_PURCHASEREQUEST_UUID_COL + "=?";
        String selectArgs[] = {input.getString(PBSRequisitionController.ARG_PURCHASEREQUEST_UUID)};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(MPurchaseRequest.TABLENAME), prProjection, selection, selectArgs, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            output.putSerializable(PBSRequisitionController.ARG_PURCHASEREQUEST, populatePurchaseRequest(cursor));
        }
        cursor.close();
        return output;
    }

    private Bundle getProductList() {

        //TODO: to improve. this input coming from GUI side due to user can choose either wants
        //product name or product value.
        String columnSelection = input.getString(PBSRequisitionController.ARG_PROD_COL_SELECTION);

        String projection[] = {MProduct.M_PRODUCT_UUID_COL, columnSelection};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.M_PRODUCT_TABLE), projection, null, null, null);

        //get the purchase request list.
        ArrayList<SpinnerPair> productList = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                productList.add(getProduct(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        output.putParcelableArrayList(PBSRequisitionController.ARG_PRODUCT_LIST, productList);
        return output;
    }

    private Bundle getProductListWithValue() {
        String projection[] = {MProduct.M_PRODUCT_UUID_COL, MProduct.NAME_COL, MProduct.VALUE_COL};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.M_PRODUCT_TABLE), projection, null, null, null);

        //get the purchase request list.
        ArrayList<SpinnerPair> productList = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            SpinnerPair pair = new SpinnerPair();
            ArrayList<String> list = new ArrayList();
            Gson gson = new Gson();
            do {
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    String columnName = cursor.getColumnName(x);
                    String rowValue = cursor.getString(x);
                    if (MProduct.M_PRODUCT_UUID_COL
                            .equalsIgnoreCase(columnName)) {
                        pair.setKey(rowValue);
                    } else if (MProduct.NAME_COL.equalsIgnoreCase(columnName) ||
                            MProduct.VALUE_COL.equalsIgnoreCase(columnName)) {
                        if (MProduct.NAME_COL
                                .equalsIgnoreCase(columnName)) {
                            //check wether the product value contains /"
                            String parsedEscape = PandoraHelper.parseEscapedChar(rowValue);
                            if (parsedEscape != null)
                                list.add(rowValue);
                            else
                                list.add(rowValue);
                        } else if (MProduct.VALUE_COL
                                .equalsIgnoreCase(columnName)) {
                            list.add(rowValue);
                        }
                        if (list.size() == 2) {
                            pair.setValue(gson.toJson(list));
                            list = new ArrayList();
                            if (!cursor.isLast())
                                pair = new SpinnerPair();
                        }
                    }
                }
                if (!cursor.isLast())
                    productList.add(pair);
            } while (cursor.moveToNext());
        }
        cursor.close();
        output.putParcelableArrayList(PBSRequisitionController.ARG_PRODUCT_LIST, productList);
        return output;
    }

    private Bundle createRequisition() {
        PBSIServerAPI serverAPI = new PBSServerAPI();

        MPurchaseRequest pr = (MPurchaseRequest) input.getSerializable(PBSRequisitionController.ARG_PURCHASEREQUEST);
        pr.setRequestDate(PandoraHelper.parseToDisplaySDate(pr.getRequestDate(), "yyyy-MM-dd hh:mm", null));
        MPurchaseRequest resultPR = serverAPI.createRequisition
                (
                        pr,
                        input.getString(PBSServerConst.PARAM_URL)
                );

        String selection = MPurchaseRequest.M_PURCHASEREQUEST_UUID_COL + "=?";
        String selectionArgs[] = {pr.getM_PurchaseRequest_UUID()};

        if (PandoraConstant.TRUE.equalsIgnoreCase(resultPR.getSuccess())) {
            //update the data document no and id
            ContentValues cv = new ContentValues();
            cv.put(MPurchaseRequest.M_PURCHASEREQUEST_ID_COL, resultPR.getM_PurchaseRequest_ID());
            cv.put(MPurchaseRequest.DOCUMENTNO_COL, resultPR.getDocumentNo());

            ArrayList<ContentProviderOperation> ops =
                    new ArrayList<>();


            ops.add(ContentProviderOperation
                    .newUpdate(ModelConst.uriCustomBuilder(MPurchaseRequest.TABLENAME))
                    .withValues(cv)
                    .withSelection(selection, selectionArgs)
                    .build());

            //update lines
            ContentValues rlcv = new ContentValues();
            MPurchaseRequestLine lines[] = resultPR.getLines();
            MPurchaseRequestLine originalLines[] = pr.getLines();

            for (int i = 0; i < lines.length; i++) {
                selection = MPurchaseRequestLine.M_PURCHASEREQUESTLINE_UUID_COL + "=?";
                String lineSelectionArgs[] = {originalLines[i].getM_PurchaseRequestLine_UUID()};
                rlcv.put(MPurchaseRequestLine.M_PURCHASEREQUESTLINE_ID_COL, lines[i].getM_PurchaseRequestLine_ID());
                ops.add(ContentProviderOperation
                        .newUpdate(ModelConst.uriCustomBuilder(MPurchaseRequestLine.TABLENAME))
                        .withValues(rlcv)
                        .withSelection(selection, lineSelectionArgs)
                        .build());
            }
            output = PandoraHelper.providerApplyBatch(output, cr, ops, "create requisition");
        } else {
            //delete the data
//            ArrayList<ContentProviderOperation> ops =
//                    new ArrayList<>();
//
//            //delete line tables first. due to dependency
//            ops.add(ContentProviderOperation
//                    .newDelete(ModelConst.uriCustomBuilder(MPurchaseRequestLine.TABLENAME))
//                    .withSelection(selection, selectionArgs)
//                    .build());
//
//            ops.add(ContentProviderOperation
//                    .newDelete(ModelConst.uriCustomBuilder(MPurchaseRequest.TABLENAME))
//                    .withSelection(selection, selectionArgs)
//                    .build());
//
//            output = PandoraHelper.providerApplyBatch(output, cr, ops, "delete requisition.");
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
        }
        return output;
    }

    private Bundle removeRequisition() {
        MPurchaseRequest pr = (MPurchaseRequest) input.getSerializable(PBSRequisitionController.ARG_PURCHASEREQUEST);
        pr.setRequestDate(PandoraHelper.parseToDisplaySDate(pr.getRequestDate(), "yyyy-MM-dd hh:mm", null));

        String selection = MPurchaseRequest.M_PURCHASEREQUEST_UUID_COL + "=?";
        String selectionArgs[] = {pr.getM_PurchaseRequest_UUID()};

        //delete the data
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<>();

        //delete line tables first. due to dependency
        ops.add(ContentProviderOperation
                .newDelete(ModelConst.uriCustomBuilder(MPurchaseRequestLine.TABLENAME))
                .withSelection(selection, selectionArgs)
                .build());

        ops.add(ContentProviderOperation
                .newDelete(ModelConst.uriCustomBuilder(MPurchaseRequest.TABLENAME))
                .withSelection(selection, selectionArgs)
                .build());

        output = PandoraHelper.providerApplyBatch(output, cr, ops, "delete requisition.");
        return output;
    }

    private Bundle syncRequisitions() {
        PBSIServerAPI serverAPI = new PBSServerAPI();

        MPurchaseRequest pr = new MPurchaseRequest();
        pr.setC_ProjectLocation_ID(input.getString(PBSRequisitionController.ARG_PROJECT_LOCATION_ID));
        pr.setAD_User_ID(input.getString(PBSRequisitionController.ARG_USER_ID));

        String resultJson = serverAPI.getRequisitions
                (
                        pr,
                        input.getString(PBSServerConst.PARAM_URL)
                );

        Pair pair = PandoraHelper.parseJsonWithArraytoPair(resultJson, "Success", "Requisitions", MPurchaseRequest[].class.getName());
        if (pair != null) {
            String success = (String) pair.first;
            MPurchaseRequest[] purchaseReqs = (MPurchaseRequest[]) pair.second;

            ArrayList<ContentProviderOperation> ops =
                    new ArrayList<>();
            if ("TRUE".equalsIgnoreCase(success)) {
                if(purchaseReqs != null) {
                    for (MPurchaseRequest purchaseReq : purchaseReqs) {
                        ContentValues rcv = new ContentValues();
                        rcv.put(MPurchaseRequest.M_PURCHASEREQUEST_ID_COL, purchaseReq.getM_PurchaseRequest_ID());
                        rcv.put(MPurchaseRequest.REQUESTDATE_COL, purchaseReq.getRequestDate());
                        String projLocUUID = ModelConst.mapIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE, ModelConst.C_PROJECTLOCATION_UUID_COL,
                                purchaseReq.getC_ProjectLocation_ID(), ModelConst.C_PROJECTLOCATION_ID_COL, cr);
                        rcv.put(ModelConst.C_PROJECTLOCATION_UUID_COL, projLocUUID);
                        String userUUID = ModelConst.mapIDtoColumn(
                                ModelConst.AD_USER_TABLE, ModelConst.AD_USER_UUID_COL,
                                purchaseReq.getAD_User_ID(), ModelConst.AD_USER_ID_COL, cr);
                        rcv.put(ModelConst.AD_USER_UUID_COL, userUUID);
                        rcv.put(MPurchaseRequest.ISAPPROVED_COL, purchaseReq.getIsApproved());
                        rcv.put(MPurchaseRequest.DOCUMENTNO_COL, purchaseReq.getDocumentNo());
                        String selection = ModelConst.M_PURCHASEREQUEST_TABLE + ModelConst._ID;

                        String[] arg = {purchaseReq.getM_PurchaseRequest_ID()};
                        String tableName = ModelConst.M_PURCHASEREQUEST_TABLE;
                        if (!ModelConst.isInsertedRow(cr, tableName, selection, arg)) {
                            purchaseReq.setM_PurchaseRequest_UUID(UUID.randomUUID().toString());
                            rcv.put(MPurchaseRequest.M_PURCHASEREQUEST_UUID_COL, purchaseReq.getM_PurchaseRequest_UUID());
                            ops.add(ContentProviderOperation
                                    .newInsert(ModelConst.uriCustomBuilder(tableName))
                                    .withValues(rcv)
                                    .build());
                        } else {
                            selection = selection + "=?";
                            ops.add(ContentProviderOperation
                                    .newUpdate(ModelConst.uriCustomBuilder(tableName))
                                    .withValues(rcv)
                                    .withSelection(selection, arg)
                                    .build());
                        }

                        //Requisition line insertions:
                        for (MPurchaseRequestLine prLine : purchaseReq.getLines()) {
                            ContentValues rlcv = new ContentValues();
                            rlcv.put(MPurchaseRequestLine.M_PURCHASEREQUESTLINE_ID_COL, prLine.getM_PurchaseRequestLine_ID());

                            rlcv.put(MPurchaseRequestLine.M_PURCHASEREQUEST_UUID_COL, purchaseReq.getM_PurchaseRequest_UUID());

                            rlcv.put(MPurchaseRequestLine.QTYREQUESTED_COL, prLine.getQtyRequested());

                            String prodUUID = ModelConst.mapIDtoColumn(
                                    ModelConst.M_PRODUCT_TABLE, MPurchaseRequestLine.M_PRODUCT_UUID_COL,
                                    prLine.getM_Product_ID(), MPurchaseRequestLine.M_PRODUCT_ID_COL, cr);
                            rlcv.put(MPurchaseRequestLine.M_PRODUCT_UUID_COL, prodUUID);

                            rlcv.put(MPurchaseRequestLine.DATEREQUIRED_COL, prLine.getDateRequired());

                            rlcv.put(MPurchaseRequestLine.ISEMERGENCY_COL, prLine.getIsEmergency());

                            rlcv.put(MPurchaseRequestLine.PURCHASEREASON_COL, prLine.getPurchaseReason());

                            String rlSelection = ModelConst.M_PURCHASEREQUESTLINE_TABLE + ModelConst._ID;
                            String[] rlArg = {prLine.getM_PurchaseRequestLine_ID()};
                            tableName = ModelConst.M_PURCHASEREQUESTLINE_TABLE;
                            if (!ModelConst.isInsertedRow(cr, tableName, rlSelection, rlArg)) {
                                rlcv.put(MPurchaseRequestLine.M_PURCHASEREQUESTLINE_UUID_COL, UUID.randomUUID().toString());
                                ops.add(ContentProviderOperation
                                        .newInsert(ModelConst.uriCustomBuilder(tableName))
                                        .withValues(rlcv)
                                        .build());
                            } else {
                                rlSelection = rlSelection + "=?";
                                String purcReqUUID = ModelConst.mapIDtoColumn(ModelConst.M_PURCHASEREQUEST_TABLE, MPurchaseRequest.M_PURCHASEREQUEST_UUID_COL, purchaseReq.getM_PurchaseRequest_ID(), MPurchaseRequest.M_PURCHASEREQUEST_ID_COL, cr);
                                rlcv.put(MPurchaseRequestLine.M_PURCHASEREQUEST_UUID_COL, purcReqUUID);
                                ops.add(ContentProviderOperation
                                        .newUpdate(ModelConst.uriCustomBuilder(tableName))
                                        .withValues(rlcv)
                                        .withSelection(rlSelection, rlArg)
                                        .build());
                            }
                        }

                    }
                }

                output = PandoraHelper.providerApplyBatch(output, cr, ops, "sync requisition(s).");
            } else {
                output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                output.putString(PandoraConstant.ERROR, "Fail to sync requisition(s).");
            }
        }

        return output;
    }

    public Bundle getRequisitions() {
        String orderBy = input.getString(PBSRequisitionController.ARG_ORDERBY);
        String projLocationUUID = input.getString(PBSRequisitionController.ARG_PROJECT_LOCATION_UUID);

        if ("Approved".equalsIgnoreCase(orderBy)) {
            orderBy = "IsApproved desc";
        } else if ("Not Approved".equalsIgnoreCase(orderBy)) {
            orderBy = "IsApproved asc";
        }

        String selection = MPurchaseRequest.C_PROJECTLOCATION_UUID_COL + "=?";
        String[] selectionArgs = {projLocationUUID};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(MPurchaseRequest.TABLENAME), prProjection, selection, selectionArgs, orderBy);

        //get the purchase request list.
        ObservableArrayList<MPurchaseRequest> prList = new ObservableArrayList();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                prList.add(populatePurchaseRequest(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        output.putSerializable(PBSRequisitionController.ARG_REQUISITION_LIST, prList);
        return output;
    }

    private MPurchaseRequestLine populatePurchaseRequestLine(Cursor cursor) {
        MPurchaseRequestLine prl = new MPurchaseRequestLine();
        for (int x = 0; x < cursor.getColumnNames().length; x++) {
            String columnName = cursor.getColumnName(x);
            String rowValue = cursor.getString(x);
            if (MPurchaseRequestLine.M_PURCHASEREQUESTLINE_ID_COL
                    .equalsIgnoreCase(columnName)) {
                prl.set_ID(cursor.getInt(x));
                prl.setM_PurchaseRequestLine_ID(rowValue);
            } else if (MPurchaseRequestLine.M_PURCHASEREQUESTLINE_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                prl.set_UUID(rowValue);
                prl.setM_PurchaseRequestLine_UUID(rowValue);
            } else if (MPurchaseRequestLine.M_PURCHASEREQUEST_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setM_PurchaseRequest_UUID(rowValue);
            } else if (MPurchaseRequestLine.M_PRODUCT_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setM_Product_UUID(rowValue);
                String prodName = ModelConst.mapUUIDtoColumn(ModelConst.M_PRODUCT_TABLE, MProduct.M_PRODUCT_UUID_COL,
                        rowValue, MProduct.NAME_COL, cr);
                String prodVal = ModelConst.mapUUIDtoColumn(ModelConst.M_PRODUCT_TABLE, MProduct.M_PRODUCT_UUID_COL,
                        rowValue, MProduct.VALUE_COL, cr);
                prl.setProductName(prodName);
                prl.setProductValue(prodVal);
            } else if (MPurchaseRequestLine.DATEREQUIRED_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setDateRequired(
                        PandoraHelper.parseToDisplaySDate(rowValue, "dd-MM-yyyy",
                                TimeZone.getDefault()));
            } else if (MPurchaseRequestLine.QTYREQUESTED_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setQtyRequested(Integer.parseInt(rowValue));
                prl.setQtyRequestedString(rowValue);
            } else if (MPurchaseRequestLine.ISEMERGENCY_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setIsEmergency(rowValue);
            } else if (MPurchaseRequestLine.PURCHASEREASON_COL
                    .equalsIgnoreCase(columnName)) {
                prl.setPurchaseReason(rowValue);
            }
        }
        return prl;
    }

    /**
     *
     * @param cursor
     * @return
     */
    private MPurchaseRequest populatePurchaseRequest(Cursor cursor) {
        MPurchaseRequest pr = new MPurchaseRequest();
        for (int x = 0; x < cursor.getColumnNames().length; x++) {
            String columnName = cursor.getColumnName(x);
            String rowValue = cursor.getString(x);
            if (MPurchaseRequest.M_PURCHASEREQUEST_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                pr.set_UUID(rowValue);
                pr.setM_PurchaseRequest_UUID(rowValue);
            } else if (MPurchaseRequest.M_PURCHASEREQUEST_ID_COL
                    .equalsIgnoreCase(columnName)) {
                pr.set_ID(cursor.getInt(x));
                pr.setM_PurchaseRequest_ID(rowValue);
            } else if (MPurchaseRequest.C_PROJECTLOCATION_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                //map to project location name.
                if (rowValue != null) {
                    if (!rowValue.isEmpty()) {
                        //String project location name.
                        String projLocName = ModelConst.mapUUIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE,
                                MPurchaseRequest.C_PROJECTLOCATION_UUID_COL,
                                rowValue, ModelConst.NAME_COL, cr
                        );
                        if (projLocName != null && !projLocName.isEmpty())
                            pr.setProjLocName(projLocName);
                        pr.setC_ProjectLocation_UUID(rowValue);
                    }
                }
            } else if (MPurchaseRequest.AD_USER_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                if (rowValue != null) {
                    if (!rowValue.isEmpty()) {
                        //String project location name.
                        String userName = ModelConst.mapUUIDtoColumn(ModelConst.AD_USER_TABLE,
                                MPurchaseRequest.AD_USER_UUID_COL,
                                rowValue, ModelConst.NAME_COL, cr
                        );
                        if (userName != null && !userName.isEmpty())
                            pr.setUserName(userName);
                        pr.setAD_User_UUID(rowValue);
                    }
                }
            } else if (MPurchaseRequest.DOCUMENTNO_COL
                    .equalsIgnoreCase(columnName)) {
                pr.setDocumentNo(rowValue);
            } else if (MPurchaseRequest.REQUESTDATE_COL
                    .equalsIgnoreCase(columnName)) {
                pr.setRequestDate(PandoraHelper.parseToDisplaySDate(
                        rowValue, "dd-MM-yyyy",
                        null));
            } else if (MPurchaseRequest.ISAPPROVED_COL
                    .equalsIgnoreCase(columnName)) {
                pr.setIsApproved(rowValue);
                //set approval status.
                pr.setStatus(getStatus(rowValue));
            }
        }
        return pr;
    }

    private String getStatus(String isApproved) {
        String status = "";
        if (PandoraConstant.FALSE.equalsIgnoreCase(isApproved)) {
            status = "WAITING APPROVAL";
        } else if (PandoraConstant.TRUE.equalsIgnoreCase(isApproved)) {
            status = PandoraConstant.APPROVED;
        } else if (isApproved.equalsIgnoreCase("N"))
            status = "WAITING APPROVAL";
        return status;
    }


    private SpinnerPair getProduct(Cursor cursor) {
        SpinnerPair pair = new SpinnerPair();
        for (int x = 0; x < cursor.getColumnNames().length; x++) {
            String columnName = cursor.getColumnName(x);
            String rowValue = cursor.getString(x);
            if (MProduct.M_PRODUCT_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                pair.setKey(rowValue);
            } else if (MProduct.NAME_COL
                    .equalsIgnoreCase(columnName)) {
                //check wether the product value contains /"
                String parsedEscape = PandoraHelper.parseEscapedChar(rowValue);
                if (parsedEscape != null)
                    pair.setValue(parsedEscape);
                else
                    pair.setValue(rowValue);
            } else if (MProduct.VALUE_COL
                    .equalsIgnoreCase(columnName)) {
                pair.setValue(rowValue);
            }
        }
        return pair;
    }


    public Bundle getInput() {
        return input;
    }

    public void setInput(Bundle input) {
        this.input = input;
        event = input.getString(PBSIController.ARG_TASK_EVENT);
    }

    public Bundle getOutput() {
        return output;
    }

    public void setOutput(Bundle output) {
        this.output = output;
    }

}
