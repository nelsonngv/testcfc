package com.pbasolutions.android;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;

import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.controller.PBSIController;
import com.pbasolutions.android.json.PBSProjLocJSON;
import com.pbasolutions.android.model.ModelConst;

import java.util.concurrent.Callable;

/**
 * Created by pbadell on 11/5/15.
 */
public class PandoraTask implements Callable<Bundle> {
    /**
     * Class name.
     */
    private static final String TAG = "PandoraTask";
    /**
     * Input params.
     */
    private Bundle input;
    /**
     * Output params.
     */
    private Bundle output;
    /**
     * Content Resolver.
     */
    private ContentResolver cr;
    /**
     * Event name.
     */
    private String event;

    /**
     * Constructor.
     * @param input
     * @param result
     * @param cr
     */
    public PandoraTask(Bundle input, Bundle result, ContentResolver cr) {
        this.input = input;
        this.output = result;
        this.cr = cr;
        event = input.getString(PBSIController.ARG_TASK_EVENT);
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Bundle call() throws Exception {
        switch (event){
            case PandoraController.GET_PROJLOC_EVENT: {
                return getProjLoc();
            }

            default:return null;
        }
    }

    /**
     * Get project locations.
     * @return
     */
    public Bundle getProjLoc() {
        String ad_user_id = input.getString(PBSAssetController.ARG_AD_USER_ID);
        String ad_user_uuid = ModelConst.mapUUIDtoColumn(ModelConst.AD_USER_TABLE, ModelConst.AD_USER_ID_COL,
                ad_user_id, ModelConst.AD_USER_UUID_COL, cr);

        String projection [] = {ModelConst.C_PROJECTLOCATION_UUID_COL, ModelConst.NAME_COL, ModelConst.C_PROJECTLOCATION_ID_COL};
        String selection = ModelConst.ISACTIVE_COL + "=? AND hr_cluster_uuid != 'null' AND hr_cluster_uuid in (select hr_cluster_uuid from hr_clustermanagement where isactive=? and ad_user_uuid=? group by hr_cluster_uuid)";
        String selectionArgs [] = {"Y", "Y", ad_user_uuid};
        Cursor cursor = cr.query(ModelConst.uriCustomBuilder(ModelConst.C_PROJECT_LOCATION_TABLE), projection, selection, selectionArgs, ModelConst.NAME_COL + " ASC");
        int numberOfRows = cursor.getCount();
        if (numberOfRows > 0) {
            PBSProjLocJSON[] projLocJSONs = new PBSProjLocJSON[numberOfRows];
            cursor.moveToFirst();
            for (int x = 0; x < numberOfRows; x++) {
                projLocJSONs[x] = new PBSProjLocJSON();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (cursor.getColumnName(i).equalsIgnoreCase(ModelConst.NAME_COL))
                        projLocJSONs[x].setName(cursor.getString(i));
                    else if (cursor.getColumnName(i).equalsIgnoreCase(ModelConst.C_PROJECTLOCATION_UUID_COL))
                        projLocJSONs[x].setC_ProjectLocation_UUID(cursor.getString(i));
                    else if (ModelConst.C_PROJECTLOCATION_ID_COL.equalsIgnoreCase(cursor.getColumnName(i))) {
                        projLocJSONs[x].setC_ProjectLocation_ID(cursor.getString(i));
                    }
                }
                cursor.moveToNext();
            }
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            output.putString(PandoraConstant.ERROR, "Please select the project location in defaults setting tab.");
            output.putSerializable(PandoraController.ARG_PROJECT_LOCATION_JSON, projLocJSONs);
        } else {
            output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            output.putString(PandoraConstant.ERROR, "This app has not run initial sync yet. Please sync now.");
        }
        cursor.close();
        return output;
    }
}

