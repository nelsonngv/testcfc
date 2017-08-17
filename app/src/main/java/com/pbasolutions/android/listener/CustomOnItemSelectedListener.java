package com.pbasolutions.android.listener;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.controller.PBSAuthenticatorController;
import com.pbasolutions.android.fragment.RoleFragment;
import com.pbasolutions.android.json.PBSClientJSON;
import com.pbasolutions.android.json.PBSOrgJSON;
import com.pbasolutions.android.json.PBSProjLocJSON;
import com.pbasolutions.android.json.PBSRoleJSON;

/**
 * Created by pbadell on 7/30/15.
 */
public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    Object[] objectArray;
    Spinner orgSpinner;
    RoleFragment fragment;
    Spinner clientSpinner;
    Spinner projectSpinner;
    PandoraMain context;
    private static int ZERO_INDEX =0;

    public CustomOnItemSelectedListener(RoleFragment fragment, Object[] roles, Spinner spinner, Spinner client, Spinner projectSpinner) {
        this.objectArray = roles;
        this.orgSpinner = spinner;
        this.fragment = fragment;
        this.clientSpinner = client;
        this.projectSpinner = projectSpinner;
        context = (PandoraMain)fragment.getActivity();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedValue = adapterView.getItemAtPosition(i).toString();
        final PandoraContext globalVariable;
        if(PandoraMain.instance != null && PandoraMain.instance.getGlobalVariable() != null)
            globalVariable = PandoraMain.instance.getGlobalVariable();
        else globalVariable = (PandoraContext) view.getContext().getApplicationContext();
        if (objectArray instanceof PBSRoleJSON[]) {
            for ( int x=0; x<objectArray.length; x++) {
                PBSRoleJSON role = (PBSRoleJSON) objectArray[x] ;
                if (role.getName().equals(selectedValue)) {
                    if (view != null
                            && view.getContext()!=null
                            && view.getContext().getApplicationContext()!=null){
                        String prevRoleName = globalVariable.getAd_role_name();
                        globalVariable.setAd_role_name(role.getName());
                        globalVariable.setAd_role_id(role.getAD_Role_ID());
                        globalVariable.setAd_role_spinner_index(x);
                        //if the role is changed always set back the org spinner index to 0
                        if (prevRoleName == null || !prevRoleName.equals(role.getName()))
                        {
                            globalVariable.setAd_org_spinner_index(ZERO_INDEX);
                            //if the role is changed always set back the client spinner index to 0
                            globalVariable.setAd_client_spinner_index(ZERO_INDEX);
                        }
                    }
                    this.orgSpinner.setVisibility(View.VISIBLE);
                    this.clientSpinner.setVisibility(View.VISIBLE);

                    fragment.addItemsOnSpinner(this.orgSpinner, PBSRoleJSON.getOrgNames(role.getOrgs()), "orgSpinner");
                    orgSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener(fragment, role.getOrgs(), orgSpinner, clientSpinner, projectSpinner));
                    return;
                }
            }
        } else if (objectArray instanceof PBSOrgJSON[]) {
            for ( int x=0; x<objectArray.length; x++) {
                PBSOrgJSON org = (PBSOrgJSON) objectArray[x];
                if (org.getOrgName().equals(selectedValue)) {
                    globalVariable.setAd_org_name(org.getOrgName());
                    globalVariable.setAd_org_id(org.getAD_Org_ID());
                    globalVariable.setAd_org_spinner_index(x);
                    //TODO: in future to support array of client.
                    PBSClientJSON[] client = new PBSClientJSON[1];
                    client[0] = new PBSClientJSON();
                    client[0].setName(org.getClientName());
                    client[0].setAD_Client_ID(org.getAD_Client_ID());

                    PBSProjLocJSON[] projLoc = globalVariable.getProjLocJSON();
                    if (projLoc != null) {
                        fragment.addItemsOnSpinner(this.projectSpinner, PBSRoleJSON.getProjLoc(projLoc), "projectSpinner");
                        projectSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener(fragment, projLoc, orgSpinner, clientSpinner, projectSpinner));
                    }

                    fragment.addItemsOnSpinner(this.clientSpinner, PBSRoleJSON.getClientNames(client), "clientSpinner");
                    clientSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener(fragment, client, orgSpinner, clientSpinner, projectSpinner));

                    this.orgSpinner.setVisibility(View.VISIBLE);
                    return;
                }
            }
        } else if (objectArray instanceof PBSClientJSON[]) {
            for (int x=0; x<objectArray.length; x++) {
                PBSClientJSON client = (PBSClientJSON) objectArray[x];
                if (client.getName().equals(selectedValue)) {
                    globalVariable.setAd_client_name(client.getName());
                    globalVariable.setAd_client_id(client.getAD_Client_ID());
                    globalVariable.setAd_client_spinner_index(x);
                    this.clientSpinner.setVisibility(View.VISIBLE);
                    return;
                }
            }
        }

        else if (objectArray instanceof PBSProjLocJSON[]) {
            for (int x=0; x<objectArray.length; x++) {
                PBSProjLocJSON projloc = (PBSProjLocJSON) objectArray[x];
                if (projloc.getName().equals(selectedValue)) {
                    globalVariable.setC_projectlocation_name(projloc.getName());
                    globalVariable.setC_projectlocation_uuid(projloc.getC_ProjectLocation_UUID());
                    globalVariable.setC_projectlocation_id(projloc.getC_ProjectLocation_ID());
                    globalVariable.setC_ProjectLocation_Spinner_Index(x);

                    PBSAuthenticatorController controller = new PBSAuthenticatorController(context);
                    Bundle input = new Bundle ();
                    String arrayArgs[] = {PBSAuthenticatorController.PROJLOC_ARG, PBSAuthenticatorController.PROJLOC_INDEX_ARG, PBSAuthenticatorController.PROJLOCS_ARG};
                    input.putStringArray(PBSAuthenticatorController.ARRAY_ARG, arrayArgs);
                    input.putString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE, PBSAccountInfo.ACCOUNT_TYPE);
                    input.putString(PBSAuthenticatorController.USER_NAME_ARG, globalVariable.getAd_user_name());
                    input.putString(arrayArgs[0], projloc.getC_ProjectLocation_UUID());
                    input.putString(arrayArgs[1], String.valueOf(x));
                    String json = new Gson().toJson(objectArray);
                    input.putSerializable(arrayArgs[2], json);
                    controller.triggerEvent(PBSAuthenticatorController.SET_ACCOUNT_USERDATA_EVENT, input, new Bundle(), null);

                    this.projectSpinner.setVisibility(View.VISIBLE);
                    return;
                }
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}

