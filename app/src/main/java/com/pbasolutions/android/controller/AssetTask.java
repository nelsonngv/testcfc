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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.api.PBSIServerAPI;
import com.pbasolutions.android.api.PBSServerAPI;
import com.pbasolutions.android.model.MAsset;
import com.pbasolutions.android.model.MMovement;
import com.pbasolutions.android.model.MMovementLine;
import com.pbasolutions.android.model.MProduct;
import com.pbasolutions.android.model.MStorage;
import com.pbasolutions.android.model.MUOM;
import com.pbasolutions.android.model.ModelConst;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by pbadell on 12/1/15.
 */
public class AssetTask extends Task {

    private static final String TAG = "AssetTask";

    private ContentResolver cr;
    private boolean isSelected = false;

    private String mvlProjection[] = {
            MMovementLine.M_MOVEMENTLINE_ID_COL,
            MMovementLine.M_MOVEMENTLINE_UUID_COL,
            MMovementLine.M_MOVEMENT_UUID_COL,
            MMovementLine.MOVEMENTQTY_COL,
            MMovementLine.M_PRODUCT_UUID_COL,
            MMovementLine.C_UOM_UUID_COL,
            MMovementLine.M_ATTRIBUTESETINSTANCE_ID_COL,
            MMovementLine.ASI_DESCRIPTION_COL
    };

    public AssetTask(ContentResolver cr) {
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
            case PBSAssetController.GET_MSTORAGES_EVENT: {
                return getMStorages();
            }
            case PBSAssetController.GET_MOVEMENTS_FROM_SERVER_EVENT: {
                return getMovements();
            }
            case PBSAssetController.GET_PROJECTLOCATIONS_EVENT: {
                return getProjectLocations();
            }
            case PBSAssetController.GET_LINES_EVENT: {
                return getLines();
            }
            case PBSAssetController.SAVE_MOVEMENT_EVENT: {
                return saveMovement();
            }
            case PBSAssetController.GET_ASSET_PRODUCT_EVENT: {
                return getProducts();
            }
            case PBSAssetController.GET_UOM_EVENT: {
                return getUOM();
            }
            case PBSAssetController.SAVE_MOVEMENTLINE_EVENT: {
                return saveMovementLine();
            }
            case PBSAssetController.GET_ASI_EVENT: {
                return getAsi();
            }
            case PBSAssetController.GET_QTYONHAND_EVENT: {
                return getQtyOnHand();
            }
            case PBSAssetController.GET_MOVEMENT_EVENT: {
                return getMovement();
            }
            case PBSAssetController.MOVE_EVENT: {
                return move();
            }
            case PBSAssetController.COMPLETE_MOVEMENT_EVENT: {
                return completeMovement();
            }
            case PBSAssetController.GET_MOVEMENTLINE_EVENT: {
                return getMovementLine();
            }
            case PBSAssetController.REMOVE_LINES_EVENT: {
                return removeLines();
            }
            case PBSAssetController.UPDATE_MOVEMENT_INFO_EVENT: {
                return updateMovementInfo();
            }

            default:
                return null;
        }
    }

    private Bundle updateMovementInfo() {
        MMovement movement = (MMovement) input
                .getSerializable(PBSAssetController.ARG_MOVEMENT);

        String projectLocationUUID = ModelConst.mapIDtoColumn(
                ModelConst.C_PROJECT_LOCATION_TABLE,
                ModelConst.C_PROJECTLOCATION_UUID_COL,
                String.valueOf(movement.getC_ProjectLocation_ID()),
                ModelConst.C_PROJECTLOCATION_ID_COL,
                cr
        );

        movement.setProjectLocation_UUID(projectLocationUUID);

        String projectLocationName = ModelConst.mapIDtoColumn(
                ModelConst.C_PROJECT_LOCATION_TABLE,
                ModelConst.NAME_COL,
                String.valueOf(movement.getC_ProjectLocation_ID()),
                ModelConst.C_PROJECTLOCATION_ID_COL,
                cr
        );

        movement.setProjectLocation(projectLocationName);

        String projectLocationToUUID = ModelConst.mapIDtoColumn(
                ModelConst.C_PROJECT_LOCATION_TABLE,
                ModelConst.C_PROJECTLOCATION_UUID_COL,
                String.valueOf(movement.getC_ProjectLocationTo_ID()),
                ModelConst.C_PROJECTLOCATION_ID_COL,
                cr
        );

        movement.setProjectLocationTo_UUID(projectLocationToUUID);

        String projectLocationToName = ModelConst.mapIDtoColumn(
                ModelConst.C_PROJECT_LOCATION_TABLE,
                ModelConst.NAME_COL,
                String.valueOf(movement.getC_ProjectLocationTo_ID()),
                ModelConst.C_PROJECTLOCATION_ID_COL,
                cr
        );

        movement.setProjectLocationTo(projectLocationToName);

        //update movement line info
        MMovementLine lines[] = movement.getLines();
        if (lines == null)
            return null;
        for (int x = 0; x < lines.length; x++) {
            //todo : instead of using map uuid its quite overhead. try to make something like adempiere

            String productUUID = ModelConst.mapIDtoColumn(MProduct.TABLENAME, MProduct.M_PRODUCT_UUID_COL,
                    String.valueOf(lines[x].getM_Product_ID()), MProduct.M_PRODUCT_ID_COL, cr);
            lines[x].setM_Product_UUID(productUUID);

            //where get the whole object of eg: Product based on id/uuid.
            String productName = ModelConst.mapIDtoColumn(MProduct.TABLENAME, MProduct.NAME_COL,
                    String.valueOf(lines[x].getM_Product_ID()), MProduct.M_PRODUCT_ID_COL, cr);

            lines[x].setProductName(productName);

            String productValue = ModelConst.mapIDtoColumn(MProduct.TABLENAME, MProduct.VALUE_COL,
                    String.valueOf(lines[x].getM_Product_ID()), MProduct.M_PRODUCT_ID_COL, cr);

            lines[x].setProductValue(productValue);


            String UOMName = ModelConst.mapIDtoColumn(MUOM.TABLENAME, MUOM.NAME_COL,
                    String.valueOf(lines[x].getC_UOM_ID()), MUOM.C_UOM_ID_COL, cr);

            lines[x].setUOMName(UOMName);

            String UOMSymbol = ModelConst.mapIDtoColumn(MUOM.TABLENAME, MUOM.UOMSYMBOL_COL,
                    String.valueOf(lines[x].getC_UOM_ID()), MUOM.C_UOM_ID_COL, cr);

            lines[x].setUOMSymbol(UOMSymbol);

            lines[x].setASI_Description(lines[x].getDescription());

            lines[x].set_ID(lines[x].getM_MovementLine_ID().intValue());
        }

        movement.setLines(lines);
        output.putSerializable(PBSAssetController.ARG_MOVEMENT, movement);
        return output;
    }

    /**
     * @return
     */
    private Bundle removeLines() {
        ObservableArrayList<MMovementLine> lines =
                (ObservableArrayList) input.getSerializable(PBSAssetController.ARG_MOVEMENTLINES);

        ArrayList<String> uuidList = new ArrayList();
        for (MMovementLine mvl : lines) {
            if (mvl.isSelected()) {
                uuidList.add(mvl.getM_MovementLine_UUID());
            }
        }
        String selection = MMovementLine.M_MOVEMENTLINE_UUID_COL + "=?";
        String uuidArray[] = uuidList.toArray(new String[uuidList.size()]);
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<>();
        for (int x = 0; x < uuidList.size(); x++) {
            String[] selectionArgs = {uuidArray[x]};
            ops.add(ContentProviderOperation
                    .newDelete(ModelConst.uriCustomBuilder(MMovementLine.TABLENAME))
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
                    output.putString(PandoraConstant.ERROR, "Fail to delete selected line(s).");
                    return output;
                }
            }
            output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
            output.putString(PandoraConstant.RESULT, "Successfully delete line(s)");
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        } catch (OperationApplicationException e) {
            Log.e(TAG, e.getMessage());
        }
        return output;
    }

    private Bundle getMovementLine() {
        int m_movementline_id = input.getInt(PBSAssetController.ARG_M_MOVEMENTLINE_ID);
        ObservableArrayList<MMovementLine> movementLines =
                (ObservableArrayList) input.getSerializable(PBSAssetController.ARG_MOVEMENTLINES);

        //find in model list
        for (MMovementLine mMovementLine : movementLines) {
            if (m_movementline_id == mMovementLine.getM_MovementLine_ID().intValue()) {
                output.putSerializable(PBSAssetController.ARG_MOVEMENTLINE, mMovementLine);
                return output;
            }
        }
        return output;
    }

    /**
     *
     * @return
     */
    private Bundle completeMovement() {
        JsonObject object = new JsonObject();
        int m_movement_id = input.getInt(PBSAssetController.ARG_M_MOVEMENT_ID);
        String m_movement_uuid = input.getString(PBSAssetController.ARG_M_MOVEMENT_UUID);
        if (m_movement_id == 0 && (m_movement_uuid == null || m_movement_uuid.isEmpty())) {
            return output;
        }

        object.addProperty(MMovement.M_MOVEMENT_ID_COL, m_movement_id);

        PBSIServerAPI serverAPI = new PBSServerAPI();
        String result = serverAPI.completeMovement
                (
                        object,
                        input.getString(PBSServerConst.PARAM_URL)
                );

        JsonParser p = new JsonParser();
        JsonObject jsonObj = p.parse(result).getAsJsonObject();
        String success = jsonObj.get(PBSServerConst.SUCCESS).getAsString();
        if (PBSServerConst.TRUE.equalsIgnoreCase(success)) {
            //delete only if the movement exist else.... if the movement purely created in server,
            //just complete it.

            String selectionArgs[] = {String.valueOf(m_movement_id)};
            //check if its exist
            boolean isInserted = ModelConst.isInsertedRow(cr, MMovement.TABLENAME,
                    MMovement.M_MOVEMENT_ID_COL,
                    selectionArgs);

            if (isInserted){
                String selection = MMovement.M_MOVEMENT_UUID_COL + "=?";
                String args[] = {m_movement_uuid};
                //delete the movement from local db.
                ArrayList<ContentProviderOperation> ops =
                        new ArrayList<>();

                //selection will always based on movement_uuid
                //delete line tables first. due to dependency
                ops.add(ContentProviderOperation
                        .newDelete(ModelConst.uriCustomBuilder(MMovementLine.TABLENAME))
                        .withSelection(selection, args)
                        .build());

                ops.add(ContentProviderOperation
                        .newDelete(ModelConst.uriCustomBuilder(MMovement.TABLENAME))
                        .withSelection(selection, args)
                        .build());

                output = PandoraHelper.providerApplyBatch(output, cr, ops , "complete movement.");
            } else {
                output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                output.putString(PandoraConstant.RESULT, "Completed movement.");
            }
        } else {
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            output.putString(PandoraConstant.ERROR, "Fail to complete movement.");
        }
        return output;
    }

    private Bundle move() {
        PBSIServerAPI serverAPI = new PBSServerAPI();
        MMovement movement = (MMovement) input.getSerializable(PBSAssetController.ARG_MOVEMENT);
        MMovement resultMovement = serverAPI.createMovement
                (
                        movement,
                        input.getString(PBSServerConst.PARAM_URL)
                );
        String selection = MMovement.M_MOVEMENT_UUID_COL + "=?";
        String selectionArgs[] = {movement.get_UUID()};

        if (PandoraConstant.TRUE.equalsIgnoreCase(resultMovement.getSuccess())) {
            //update the data document no and id
            ContentValues cv = new ContentValues();
            cv.put(MMovement.M_MOVEMENT_ID_COL, resultMovement.getM_Movement_ID().intValue());
            cv.put(MMovement.DOCUMENTNO_COL, resultMovement.getDocumentNo());

            ArrayList<ContentProviderOperation> ops =
                    new ArrayList<>();


            ops.add(ContentProviderOperation
                    .newUpdate(ModelConst.uriCustomBuilder(MMovement.TABLENAME))
                    .withValues(cv)
                    .withSelection(selection, selectionArgs)
                    .build());

            //update lines
            ContentValues rlcv = new ContentValues();
            MMovementLine lines[] = resultMovement.getLines();
            MMovementLine originalLines[] = movement.getLines();

            for (int i = 0; i < lines.length; i++) {
                selection = MMovementLine.M_MOVEMENTLINE_UUID_COL + "=?";
                String lineSelectionArgs[] = {originalLines[i].getM_MovementLine_UUID()};
                rlcv.put(MMovementLine.M_MOVEMENTLINE_ID_COL,  lines[i].getM_MovementLine_ID().intValue());
                ops.add(ContentProviderOperation
                        .newUpdate(ModelConst.uriCustomBuilder(MMovementLine.TABLENAME))
                        .withValues(rlcv)
                        .withSelection(selection, lineSelectionArgs)
                        .build());
            }
            output = PandoraHelper.providerApplyBatch(output, cr, ops, "move movement.");
        } else {
            //delete the data
            ArrayList<ContentProviderOperation> ops =
                    new ArrayList<>();

            //selection will always based on movement_uuid
            //delete line tables first. due to dependency
            ops.add(ContentProviderOperation
                    .newDelete(ModelConst.uriCustomBuilder(MMovementLine.TABLENAME))
                    .withSelection(selection, selectionArgs)
                    .build());

            ops.add(ContentProviderOperation
                    .newDelete(ModelConst.uriCustomBuilder(MMovement.TABLENAME))
                    .withSelection(selection, selectionArgs)
                    .build());

            output = PandoraHelper.providerApplyBatch(output, cr, ops , "delete movement");
            output = new Bundle();
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            output.putString(PandoraConstant.ERROR, "Failed to create movement");
        }
        return output;
    }

    private Bundle getMovement() {
        //get uom uuid from m_product
        String selection = MMovement.M_MOVEMENT_UUID_COL + "=?";
        String selectionArgs[] = {input.getString(PBSAssetController.ARG_M_MOVEMENT_UUID)};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(MMovement.TABLENAME),
                null, selection, selectionArgs, null);
        cursor.moveToFirst();
        do {
            output.putSerializable(PBSAssetController.ARG_MOVEMENT, populateMovement(cursor));
        } while (cursor.moveToNext());
        cursor.close();
        return output;
    }

    private MMovement populateMovement(Cursor cursor) {
        MMovement movement = new MMovement();
        for (int x = 0; x < cursor.getColumnNames().length; x++) {
            String columnName = cursor.getColumnName(x);
            String rowValue = cursor.getString(x);
            if (MMovement.M_MOVEMENT_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                movement.setM_Movement_UUID(rowValue);
                movement.set_UUID(rowValue);
            } else if (MMovement.M_MOVEMENT_ID_COL
                    .equalsIgnoreCase(columnName)) {
                movement.setM_Movement_ID(cursor.getInt(x));
            } else if (MMovement.DOCUMENTNO_COL
                    .equalsIgnoreCase(columnName)) {
                movement.setDocumentNo(rowValue);
            } else if (MMovement.DOCSTATUS_COL
                    .equalsIgnoreCase(columnName)) {
                movement.setDocStatus(rowValue);
            } else if (MMovement.C_PROJECTLOCATION_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                movement.setProjectLocation(rowValue);
                String c_projectlocation_id = ModelConst.mapUUIDtoColumn(
                        ModelConst.C_PROJECT_LOCATION_TABLE,
                        ModelConst.C_PROJECTLOCATION_UUID_COL,
                        rowValue,
                        ModelConst.C_PROJECTLOCATION_ID_COL,
                        cr
                );

                movement.setC_ProjectLocation_ID(Integer.parseInt(c_projectlocation_id));
            } else if (MMovement.C_PROJECTLOCATIONTO_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                movement.setProjectLocationTo(rowValue);
                String c_projectlocationto_id = ModelConst.mapUUIDtoColumn(
                        ModelConst.C_PROJECT_LOCATION_TABLE,
                        ModelConst.C_PROJECTLOCATION_UUID_COL,
                        rowValue,
                        ModelConst.C_PROJECTLOCATION_ID_COL,
                        cr
                );
                movement.setC_ProjectLocationTo_ID(Integer.parseInt(c_projectlocationto_id));
            } else if (MMovement.MOVEMENTDATE_COL
                    .equalsIgnoreCase(columnName)) {
                movement.setMovementDate(PandoraHelper.parseToDisplaySDate(rowValue, "dd-MM-yyyy",
                        TimeZone.getDefault()));
            } else if (MMovement.AD_USER_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                movement.setAD_User_UUID(rowValue);
                String ad_user_id = ModelConst.mapUUIDtoColumn(
                        ModelConst.AD_USER_TABLE,
                        ModelConst.AD_USER_UUID_COL,
                        rowValue,
                        ModelConst.AD_USER_ID_COL,
                        cr
                );
                movement.setAD_User_ID(Integer.parseInt(ad_user_id));
            }
        }
        return movement;
    }

    private Bundle getQtyOnHand() {
        ObservableArrayList<MStorage> storages = (ObservableArrayList<MStorage>) input.getSerializable(PBSAssetController.ARG_STORAGE);
        Number m_attributesetinstance_id = Integer.parseInt(input.getString(PBSAssetController.ARG_M_ATTRIBUTESETINSTANCE_ID));
        for (MStorage storage : storages) {
            if (storage.getM_AttributeSetInstance_ID().intValue() == m_attributesetinstance_id.intValue()) {
                output.putDouble(PBSAssetController.ARG_QTYONHAND, storage.getQtyOnHand().doubleValue());
                return output;
            }
        }
        return output;
    }

    private Bundle getAsi() {
        ObservableArrayList<SpinnerPair> list = new ObservableArrayList();
        ObservableArrayList<MStorage> storages = getMStorage();
        if (storages!=null && !storages.isEmpty()) {
            output.putSerializable(PBSAssetController.ARG_STORAGE, storages);
            for (MStorage storage : getMStorage()) {
                list.add(getAsiPairFromStorage(storage));
            }
            output.putSerializable(PBSAssetController.ARG_ASI, list);
        }
        return output;
    }

    private ObservableArrayList<MStorage> getMStorage() {
        String project_location_id = input.getString(PBSAssetController.ARG_PROJECTLOCATION_ID);
        String product_uuid = input.getString(PBSAssetController.ARG_M_PRODUCT_UUID);
        JsonObject object = new JsonObject();

        object.addProperty(ModelConst.C_PROJECTLOCATION_ID_COL, project_location_id);
        //get product id from uuid

        int product_id = 0;
        //case where in movement line. try to get the specific asi from selected product.
        if (product_uuid != null && !product_uuid.isEmpty()) {
            product_id = Integer.parseInt(ModelConst.mapUUIDtoColumn(MProduct.TABLENAME,
                    MProduct.M_PRODUCT_UUID_COL, product_uuid, MProduct.M_PRODUCT_ID_COL, cr));
        }

        object.addProperty(MStorage.M_PRODUCT_ID_COL, product_id);

        PBSIServerAPI serverAPI = new PBSServerAPI();
        String json = serverAPI.getMStorage(object, input.getString(PBSServerConst.PARAM_URL));
        Pair pair = PandoraHelper.parseJsonWithArraytoPair(json, "Success", "Storage",
                MStorage[].class.getName());
        if (pair != null) {
            MStorage aMstorage[] = (MStorage[]) pair.second;
            return addValueToMStorage(aMstorage);
        }
        return null;
    }

    private ObservableArrayList<MStorage> addValueToMStorage(MStorage[] array)
    {/// /convert from array to list
    ObservableArrayList<MStorage> list = new ObservableArrayList();
    for (int x = 0; x < array.length; x++) {
        Number m_product_id = array[x].getM_Product_ID();
        if (isAnAsset(String.valueOf(m_product_id))){
            String productName = ModelConst.mapIDtoColumn(MProduct.TABLENAME, MProduct.NAME_COL,
                    String.valueOf(m_product_id), MProduct.M_PRODUCT_ID_COL, cr);
            array[x].setProductName(productName);

            String productValue = ModelConst.mapIDtoColumn(MProduct.TABLENAME, MProduct.VALUE_COL,
                    String.valueOf(m_product_id), MProduct.M_PRODUCT_ID_COL, cr);
            array[x].setProductValue(productValue);
            list.add(array[x]);
        }
    }
        if (!list.isEmpty())
            return list;
        else
            return null;
    }

    private boolean isAnAsset(String productID) {
        //get product UUID
        String productUUID = ModelConst.mapIDtoColumn(MProduct.TABLENAME, MProduct.M_PRODUCT_UUID_COL,
                productID, MProduct.M_PRODUCT_ID_COL, cr);

        String args[] = { productUUID };
        //check is the product UUID is an asset
        return ModelConst.isInsertedRow(cr, MAsset.TABLENAME, MAsset.M_PRODUCT_UUID_COL, args);
    }

    private Bundle saveMovementLine() {
        ContentValues cv = input.getParcelable(PBSController.ARG_CONTENTVALUES);
        return ModelConst.insertData(cv, cr, MMovementLine.TABLENAME, output);
    }

    private Bundle getUOM() {
        //get uom uuid from m_product
        String projection[] = {MProduct.C_UOM_UUID_COL};
        String selection = MProduct.M_PRODUCT_UUID_COL + "=?";
        String selectionArgs[] = {input.getString(PBSAssetController.ARG_M_PRODUCT_UUID)};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(MProduct.TABLENAME),
                projection, selection, selectionArgs, null);
        if (cursor != null) {
            String c_uom_uuid = "";
            cursor.moveToFirst();
            do {
                for (int x = 0; x < cursor.getColumnNames().length; x++) {
                    String columnName = cursor.getColumnName(x);
                    String columnValue = cursor.getString(x);
                    if (MProduct.C_UOM_UUID_COL.equalsIgnoreCase(columnName)) {
                        c_uom_uuid = columnValue;
                    }
                }
            } while (cursor.moveToNext());

            cursor.close();
            if (!c_uom_uuid.isEmpty()) {
                //get the uom info
                String uomProjection[] = {MUOM.C_UOM_UUID_COL, MUOM.NAME_COL, MUOM.UOMSYMBOL_COL, MUOM.C_UOM_ID_COL};
                String uomSelection = MUOM.C_UOM_UUID_COL + "=?";
                String uomSelectionArg[] = {c_uom_uuid};
                Cursor uomCursor = cr.query(ModelConst.uriCustomBuilder(MUOM.TABLENAME), uomProjection, uomSelection, uomSelectionArg, null);
                uomCursor.moveToFirst();
                do {
                    output.putSerializable(PBSAssetController.ARG_UOM, populateUOM(uomCursor));
                } while (uomCursor.moveToNext());
                uomCursor.close();
                return output;
            }
        }
        return null;
    }

    private MUOM populateUOM(Cursor cursor) {
        MUOM uom = new MUOM();
        for (int x = 0; x < cursor.getColumnNames().length; x++) {
            String columnName = cursor.getColumnName(x);
            String rowValue = cursor.getString(x);
            if (MUOM.C_UOM_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                uom.setC_UOM_UUID(rowValue);
                uom.set_UUID(rowValue);
            } else if (MUOM.C_UOM_ID_COL
                    .equalsIgnoreCase(columnName)) {
                uom.setC_UOM_ID(cursor.getInt(x));
            } else if (MUOM.NAME_COL
                    .equalsIgnoreCase(columnName)) {
                uom.setName(rowValue);
            } else if (MUOM.UOMSYMBOL_COL
                    .equalsIgnoreCase(columnName)) {
                uom.setUOMSymbol(rowValue);
            } else if (MUOM.DESCRIPTION
                    .equalsIgnoreCase(columnName)) {
                uom.setDescription(rowValue);
            }
        }
        return uom;
    }

    private Bundle getProducts() {
        //first try to get the M_Product_ID/UUID from asset table.
        String projection[] = {MProduct.M_PRODUCT_UUID_COL};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(MAsset.TABLENAME),
                projection, null,
                null, null);
        if (cursor != null) {
            ArrayList<String> m_product_uuids = new ArrayList();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    for (int x = 0; x < cursor.getColumnNames().length; x++) {
                        String columnName = cursor.getColumnName(x);
                        String columnValue = cursor.getString(x);
                        if (MAsset.M_PRODUCT_UUID_COL.equalsIgnoreCase(columnName)) {
                            m_product_uuids.add(columnValue);
                        }
                    }
                } while (cursor.moveToNext());


                if (!m_product_uuids.isEmpty()) {
                    //get the product uuid info
                    String pProjection[] = {MProduct.M_PRODUCT_UUID_COL, MProduct.NAME_COL, MProduct.VALUE_COL};
                    String selection = MProduct.M_PRODUCT_UUID_COL + " IN (?";
                    for (int i = 0; i < m_product_uuids.size() - 1; i++)
                        selection += ",?";
                    selection += ")";
                    String selectionArg[] = m_product_uuids.toArray(new String[m_product_uuids.size()]);
                    Cursor pCursor = cr.query(ModelConst.uriCustomBuilder(MProduct.TABLENAME), pProjection, selection, selectionArg, null);
                    ArrayList<SpinnerPair> productList = new ArrayList<>();
                    boolean isName = input.getBoolean(PBSAssetController.ARG_ISNAME);
                    pCursor.moveToFirst();
                    do {
                        productList.add(ModelConst.getProductPair(pCursor, isName));
                    } while (pCursor.moveToNext());
                    pCursor.close();
                    output.putParcelableArrayList(PBSAssetController.ARG_ASSET_PRODUCTS, productList);
                }
            }
        }
        cursor.close();
        return output;
    }

    private Bundle saveMovement() {
        ContentValues cv = input.getParcelable(PBSController.ARG_CONTENTVALUES);
        //get ad_user uuid from ad_user_id
        String ad_user_id = input.getString(PBSAssetController.ARG_AD_USER_ID);
        String ad_user_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE,
                ModelConst.AD_USER_UUID_COL,
                ad_user_id, ModelConst.AD_USER_ID_COL, cr);

        cv.put(MMovement.AD_USER_UUID_COL, ad_user_uuid);
        String selectionArgs[] = {cv.get(MMovement.M_MOVEMENT_UUID_COL).toString()};
        if (!ModelConst.isInsertedRow(cr, MMovement.TABLENAME,
                MMovement.M_MOVEMENT_UUID_COL,
                selectionArgs
        )) {
            return ModelConst.insertData(cv, cr, MMovement.TABLENAME, output);
        } else {
            //TODO:share make it generic for return string.
            if (ModelConst.updateTableRow(cr, MMovement.TABLENAME, cv, MMovement.M_MOVEMENT_UUID_COL, selectionArgs)) {
                output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
                output.putString(PandoraConstant.RESULT, "Successfuly insert updated movement.");
            } else {
                output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                output.putString(PandoraConstant.ERROR, "Fail to update movement.");
            }
            return output;
        }
    }

    private Bundle getLines() {
        String selection = MMovement.M_MOVEMENT_UUID_COL + "=?";
        String selectArgs[] = {input.getString(PBSAssetController.ARG_M_MOVEMENT_UUID)};

        ObservableArrayList<MMovementLine> lines = new ObservableArrayList();
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(MMovementLine.TABLENAME),
                mvlProjection, selection, selectArgs, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                lines.add(MMovementLine.populateMovementLine(cursor, cr));
            } while (cursor.moveToNext());
        }
        cursor.close();
        output.putSerializable(PBSAssetController.ARG_LINES, lines);
        return output;
    }

    private Bundle getProjectLocations() {
        String projection[] = {ModelConst.C_PROJECTLOCATION_UUID_COL,
                ModelConst.NAME_COL};
        String selectionArgs[] = {input.getString(PBSAssetController.ARG_EXCLUDE_PROJECTLOCATION_UUID)};
        String selection = null;
        if (selectionArgs[0] != null) {
            selection = ModelConst.C_PROJECTLOCATION_UUID_COL + "!=?";
        } else {
            selectionArgs = null;
        }

        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_PROJECT_LOCATION_TABLE),
                projection, selection,
                selectionArgs, "LOWER(" + ModelConst.NAME_COL + ") ASC");

        //get the projectLocations list.
        ArrayList<SpinnerPair> projectLocations = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                projectLocations.add(getProjectLocation(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        output.putParcelableArrayList(PBSAssetController.ARG_PROJECTLOCATIONS, projectLocations);
        return output;
    }

    private SpinnerPair getProjectLocation(Cursor cursor) {
        SpinnerPair pair = new SpinnerPair();
        for (int x = 0; x < cursor.getColumnNames().length; x++) {
            String columnName = cursor.getColumnName(x);
            String rowValue = cursor.getString(x);
            if (ModelConst.C_PROJECTLOCATION_UUID_COL
                    .equalsIgnoreCase(columnName)) {
                pair.setKey(rowValue);
            } else if (ModelConst.NAME_COL
                    .equalsIgnoreCase(columnName)) {
                pair.setValue(rowValue);
            }
        }
        return pair;
    }

    private Bundle getMStorages() {
        output.putSerializable(PBSAssetController.ARG_STORAGE, getMStorage());
        return output;
    }

    private Bundle getMovements() {
        int project_location_id = input.getInt(PBSAssetController.ARG_PROJECTLOCATION_ID);
        int ad_user_id = input.getInt(PBSAssetController.ARG_AD_USER_ID);

        JsonObject object = new JsonObject();
        object.addProperty(ModelConst.C_PROJECTLOCATION_ID_COL, project_location_id);
        //  object.addProperty(MMovement.AD_USER_ID_COL, ad_user_id);

        PBSIServerAPI serverAPI = new PBSServerAPI();
        String json = serverAPI.getMovements(object, input.getString(PBSServerConst.PARAM_URL));
        Pair pair = PandoraHelper.parseJsonWithArraytoPair(json, "Success", "Movements", MMovement[].class.getName());
        if (pair != null) {
            MMovement aMovement[] = (MMovement[]) pair.second;
            //convert from array to list
            ObservableArrayList<MMovement> list = new ObservableArrayList();
            for (int x = 0; x < aMovement.length; x++) {
                aMovement[x].set_ID(aMovement[x].getM_Movement_ID().intValue());
                String docStatus = aMovement[x].getDocStatus();
                aMovement[x].setStatus(getDocStatus(docStatus));
                aMovement[x].setMovementDate(PandoraHelper.parseToDisplaySDate(
                        aMovement[x].getMovementDate(), "dd-MM-yyyy",
                        TimeZone.getDefault()));
                list.add(aMovement[x]);
            }
            output.putSerializable(PBSAssetController.ARG_MOVEMENT, list);
        }
        return output;
    }

    private String getDocStatus(String docStat){
        String status = "";
        switch(docStat){
            case MMovement.STATUS_CO: {
               status =  MMovement.STATUS_COMPLETED;
                break;
            }

            case MMovement.STATUS_IN: {
                status =  MMovement.STATUS_INACTIVE;
                break;
            }
            case MMovement.STATUS_RE: {
                status =  MMovement.STATUS_REVERSED;
                break;
            }
            case MMovement.STATUS_DR: {
                status =  MMovement.STATUS_DRAFTED;
                break;
            }
            case MMovement.STATUS_VO: {
                status =  MMovement.STATUS_VOIDED;
                break;
            }
            case MMovement.STATUS_AP: {
                status =  MMovement.STATUS_APPROVED;
                break;
            }
            case MMovement.STATUS_CH: {
                status =  MMovement.STATUS_CHANGED;
                break;
            }
            case MMovement.STATUS_CL: {
                status =  MMovement.STATUS_CLOSED;
                break;
            }
            case MMovement.STATUS_NA: {
                status =  MMovement.STATUS_NOT_APPROVED;
                break;
            }
            case MMovement.STATUS_PE: {
                status =  MMovement.STATUS_POSTING_ERROR;
                break;
            }
            case MMovement.STATUS_PO: {
                status =  MMovement.STATUS_POSTED;
                break;
            }
            case MMovement.STATUS_PR: {
                status =  MMovement.STATUS_PRINTED;
                break;
            }
            case MMovement.STATUS_TE: {
                status =  MMovement.STATUS_TRANSFER_ERROR;
                break;
            }
            case MMovement.STATUS_TR: {
                status =  MMovement.STATUS_TRANSFERRED;
                break;
            }
            case MMovement.STATUS_XX: {
                status =  MMovement.STATUS_BEING_PROCESSED;
                break;
            }
            default:

        }
        return status;
    }

    private Number[] getProductIDsFromAsset() {
        //get all asset product ids.
        String projection[] = {MAsset.M_PRODUCT_UUID_COL};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(MAsset.TABLENAME), projection, null,
                null, null);
        if (cursor == null) {
            return null;
        } else {
            List<String> m_product_uuid_list = new ArrayList();
            cursor.moveToFirst();
            do {
                for (int x = 0; x < cursor.getColumnCount(); x++) {
                    if (MAsset.M_PRODUCT_UUID_COL.equalsIgnoreCase(cursor.getColumnName(x))) {
                        String p_uuid = cursor.getString(x);
                        if (p_uuid != null && !p_uuid.isEmpty())
                            m_product_uuid_list.add(p_uuid);
                    }
                }
            } while (cursor.moveToNext());

            cursor.close();
            List<Number> list = new ArrayList();
            if (!m_product_uuid_list.isEmpty()) {
                for (String m_product_uuid : m_product_uuid_list) {
                    String m_product_id = ModelConst.mapUUIDtoColumn(
                            ModelConst.M_PRODUCT_TABLE, MAsset.M_PRODUCT_UUID_COL,
                            m_product_uuid, MAsset.M_PRODUCT_ID_COL, cr);
                    list.add(Integer.parseInt(m_product_id));
                }
            }
            return list.toArray(new Number[list.size()]);
        }
    }


    public static SpinnerPair getAsiPairFromStorage(MStorage storage) {
        SpinnerPair pair = new SpinnerPair();
        pair.setKey(String.valueOf(storage.getM_AttributeSetInstance_ID()));
        pair.setValue(storage.getDescription());
        return pair;
    }

}
