package com.pbasolutions.android.controller;

import android.content.ContentResolver;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.util.Pair;

import com.google.gson.JsonObject;
import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.api.PBSIServerAPI;
import com.pbasolutions.android.api.PBSServerAPI;
import com.pbasolutions.android.model.MDeploy;
import com.pbasolutions.android.model.MShift;
import com.pbasolutions.android.model.ModelConst;

import java.util.concurrent.Callable;

/**
 * Created by pbadell on 12/15/15.
 */
public class DeployTask implements Callable<Bundle> {

    private static final String TAG = "DeployTask";
    private Bundle input;
    private Bundle output;
    private ContentResolver cr;
    private String event;
    private boolean isSelected = false;

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Bundle call() throws Exception {
        switch (event) {
            case PBSDeployController.GET_DEPLOYS_EVENT: {
                return getDeploys();
            }

            default:
                return null;
        }
    }

    private Bundle getDeploys() {
        int project_location_id =  input.getInt(PBSDeployController.ARG_PROJECTLOCATION_ID);
        String deploymentDate = input.getString(PBSDeployController.ARG_DEPLOYMENTDATE);

        JsonObject object = new JsonObject();
        object.addProperty(ModelConst.C_PROJECTLOCATION_ID_COL, project_location_id);
        object.addProperty(MDeploy.DEPLOYMENT_DATE_COL, deploymentDate);

        PBSIServerAPI serverAPI = new PBSServerAPI();
        String json = serverAPI.getDeployments(
                object, input.getString(PBSServerConst.PARAM_URL)
        );

        Pair pair = PandoraHelper.parseJsonWithArraytoPair(json, "Success", "Deployments", MDeploy[].class.getName());
        MDeploy deploy[] = (MDeploy[])pair.second;

        //convert from array to list
        ObservableArrayList<MDeploy> list = new ObservableArrayList();
        for (int x=0; x<deploy.length; x++) {
            deploy[x].setEmployeesName(getEmployeesName(deploy[x].getC_BPartner_IDs()));
            deploy[x].setProjectLocationName(getProjectLocationName(
                    deploy[x].getC_ProjectLocation_ID()));

            //get shift model.
            MShift shift = new MShift(deploy[x].getHR_Shift_ID());
            shift = shift.getShift(cr, false);

            deploy[x].setHRShiftName(shift.getName());
            deploy[x].setHRShiftTimeFrom(shift.getTimeFrom().toString());
            deploy[x].setHRShiftTimeTo(shift.getTimeTo().toString());

            list.add(deploy[x]);
        }
        output.putSerializable(PBSDeployController.ARG_DEPLOYS,  list);
        return output;
    }

    private String getHRShiftName(int hr_shift_id) {
        return ModelConst.mapIDtoColumn(ModelConst.HR_SHIFT_TABLE,
                ModelConst.NAME_COL, String.valueOf(hr_shift_id),
                ModelConst.C_BPARTNER_ID_COL, cr);
    }

    private String getProjectLocationName(int c_projectLocation_id) {
        return ModelConst.mapIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE,
                ModelConst.NAME_COL, String.valueOf(c_projectLocation_id),
                ModelConst.C_BPARTNER_ID_COL, cr);
    }

    private String getEmployeesName(int C_BPartner_IDs[]){
        StringBuffer names = new StringBuffer();
        for (int x=0; x<C_BPartner_IDs.length; x++){
            String name =ModelConst.mapIDtoColumn(ModelConst.C_BPARTNER_TABLE,
                    ModelConst.NAME_COL, String.valueOf(C_BPartner_IDs[x]),ModelConst.C_BPARTNER_ID_COL, cr);
            if (x != C_BPartner_IDs.length-1){
                names.append(name);
                names.append(',');
            } else {
                names.append(name);
            }

        }
        return names.toString();
    }

    public DeployTask(ContentResolver cr) {
        this.cr = cr;
    }
}
