package com.pbasolutions.android.controller;

import android.content.Context;

/**
 * Created by pbadell on 10/9/15.
 */
public class PBSAssetController extends PBSController{

    public static final String ARG_M_MOVEMENT_UUID = "ARG_M_MOVEMENT_UUID";
    public static final String MOVEMENT_DETAILS_EVENT = "MOVEMENT_DETAILS_EVENT";
    public static final String GET_MSTORAGES_EVENT = "GET_MSTORAGES_EVENT";
    public static final String ARG_PROJECTLOCATION_ID = "ARG_PROJECTLOCATION_ID";
    public static final String GET_MOVEMENTS_FROM_SERVER_EVENT = "GET_MOVEMENTS_FROM_SERVER_EVENT";
    public static final String ARG_MOVEMENTS = "ARG_MOVEMENTS";
    public static final String ARG_STORAGE = "ARG_STORAGE";
    public static final String GET_PROJECTLOCATIONS_EVENT = "GET_PROJECTLOCATIONS_EVENT";
    public static final String ARG_PROJECTLOCATIONS = "ARG_PROJECTLOCATIONS";
    public static final String SAVE_MOVEMENT_EVENT = "SAVE_MOVEMENT_EVENT";
    public static final String MOVE_EVENT = "MOVE_EVENT";
    public static final String GET_LINES_EVENT = "GET_LINES_EVENT";
    public static final String ARG_LINES = "ARG_LINES";
    public static final String ARG_EXCLUDE_PROJECTLOCATION_UUID = "ARG_EXCLUDE_PROJECTLOCATION_UUID";
    public static final String ADD_LINE_EVENT = "ADD_LINE_EVENT";
    public static final String GET_ASSET_PRODUCT_EVENT = "GET_ASSET_PRODUCT_EVENT";
    public static final String ARG_ASSET_PRODUCTS = "ARG_ASSET_PRODUCTS";
    public static final String ARG_ISNAME = "ARG_ISNAME";
    public static final String ARG_M_PRODUCT_UUID = "ARG_M_PRODUCT_UUID";
    public static final String GET_UOM_EVENT = "GET_UOM_EVENT";
    public static final String ARG_UOM_UUID = "ARG_UOM_UUID";
    public static final String ARG_UOM = "ARG_UOM";
    public static final String ARG_MOVEMENTLINE = "ARG_MOVEMENTLINE";
    public static final String SAVE_MOVEMENTLINE_EVENT = "SAVE_MOVEMENTLINE_EVENT";
    public static final String UPDATE_MOVEMENTLINE_EVENT = "UPDATE_MOVEMENTLINE_EVENT";
    public static final String GET_ASI_EVENT = "GET_ASI_EVENT";
    public static final String ARG_ASI = "ARG_ASI";
    public static final String GET_QTYONHAND_EVENT = "GET_QTYONHAND_EVENT";
    public static final String ARG_M_ATTRIBUTESETINSTANCE_ID = "ARG_M_ATTRIBUTESETINSTANCE_ID";
    public static final String ARG_QTYONHAND = "ARG_QTYONHAND";
    public static final String GET_MOVEMENT_EVENT = "GET_MOVEMENT_EVENT";
    public static final String ARG_MOVEMENT = "ARG_MOVEMENT";
    public static final String ARG_AD_USER_ID = "ARG_AD_USER_ID";
    public static final String COMPLETE_MOVEMENT_EVENT = "COMPLETE_MOVEMENT_EVENT";
    public static final String ARG_M_MOVEMENT_ID = "ARG_M_MOVEMENT_ID";
    public static final String GET_MOVEMENTLINE_EVENT = "GET_MOVEMENTLINE_EVENT";
    public static final String ARG_MOVEMENTLINES = "ARG_MOVEMENTLINES";
    public static final String ARG_M_MOVEMENTLINE_ID = "ARG_M_MOVEMENTLINE_ID";
    public static final String REMOVE_LINES_EVENT = "REMOVE_LINES_EVENT";
    public static final String UPDATE_MOVEMENT_INFO_EVENT = "UPDATE_MOVEMENT_INFO_EVENT";
    public static final String ARG_IS_GET_ASI = "ARG_IS_GET_ASI";

    public static String movementDate;

    public PBSAssetController(Context base) {
        super(base);
        cr = getContentResolver();
        task = new AssetTask(cr, base);
    }

}
