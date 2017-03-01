package com.pbasolutions.android.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.controller.PBSAuthenticatorController;
import com.pbasolutions.android.json.PBSRoleJSON;
import com.pbasolutions.android.listener.CustomOnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pbadell on 7/30/15.
 */
public class RoleFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "RoleFragment";
    PBSRoleJSON[] roles;
    private Spinner roleSpinner;
    private Spinner orgSpinner;
    private Spinner clientSpinner;
    private Spinner projectSpinner;

    Button okButton;

    private PandoraMain context;
    private PBSAuthenticatorController authenticatorController;

    public RoleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (PandoraMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.role, container, false);

        roleSpinner = (Spinner) rootView.findViewById(R.id.role);
        orgSpinner = (Spinner) rootView.findViewById(R.id.organization);
        clientSpinner = (Spinner) rootView.findViewById(R.id.client);
        projectSpinner = (Spinner) rootView.findViewById(R.id.project);
        authenticatorController = new PBSAuthenticatorController(getActivity());

        okButton = (Button) rootView.findViewById(R.id.roleOK);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    roleOkClicked();
                } catch (Exception e) {
                    Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
                }
            }
        });
        return rootView;
    }

    public void onStart() {
        super.onStart();

        refreshSyncState();
        addSpinnerData();
    }

    void refreshSyncState() {
        if(context.getGlobalVariable() == null)
            context.setGlobalVariable((PandoraContext) getActivity().getApplicationContext());
        if (context.getGlobalVariable().isInitialSynced()) {
            okButton.setText(R.string.label_ok);
        } else {
            okButton.setText(R.string.label_sync);
        }
    }

    private void addSpinnerData() {
        List<String> list = new ArrayList<String>();
        if (context.getGlobalVariable() != null) {
            roles = context.getGlobalVariable().getRoleJSON();
        }
        if (roles != null) {
            for (PBSRoleJSON role : roles) {
                if (role != null) {
                    list.add(role.getName());
                }
            }
            addItemsOnSpinner(roleSpinner, list, "roleSpinner");
            roleSpinner.setOnItemSelectedListener(
                    new CustomOnItemSelectedListener(this, roles, orgSpinner, clientSpinner, projectSpinner));
        }
    }

    /**
     * @param spinner
     * @param list
     * @param spinnerName
     */
    public void addItemsOnSpinner(Spinner spinner, List list, String spinnerName) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        if (context.getGlobalVariable() != null) {
            setSpinnerSelection(spinner, spinnerName);
        }
    }

    /**
     * @param spinner
     * @param spinnerName
     */
    private void setSpinnerSelection(Spinner spinner, String spinnerName) {
        if (spinnerName.equals("roleSpinner")) {
            spinner.setSelection(context.getGlobalVariable().getAd_role_spinner_index());
        } else if (spinnerName.equals("orgSpinner")) {
            spinner.setSelection(context.getGlobalVariable().getAd_org_spinner_index());
        } else if (spinnerName.equals("clientSpinner")) {
            spinner.setSelection(context.getGlobalVariable().getAd_client_spinner_index());
        } else if (spinnerName.equals("projectSpinner")) {
            spinner.setSelection(context.getGlobalVariable().getC_ProjectLocation_Index());
        }
    }

    /**
     *
     */
    //TODO: check if already set role to server~
    public void roleOkClicked() {
        if (!context.getGlobalVariable().isInitialSynced()) {
            PandoraHelper.showMessage(context, R.string.label_sync);
            return;
        }
        new AsyncTask<Void, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                context.showProgressDialog("Loading...");
            }
            @Override
            protected Bundle doInBackground(Void... params) {
                Bundle input = new Bundle();
                input.putString(authenticatorController.ROLE_ARG,
                        context.getGlobalVariable().getAd_role_id());
                input.putString(authenticatorController.ORG_ARG,
                        context.getGlobalVariable().getAd_org_id());
                input.putString(authenticatorController.CLIENT_ARG,
                        context.getGlobalVariable().getAd_client_id());
                input.putString(authenticatorController.SERVER_URL_ARG,
                        context.getGlobalVariable().getServer_url());
                Bundle result = new Bundle();
                result = authenticatorController.triggerEvent(
                        authenticatorController.ROLE_SUBMIT_EVENT, input, result, null);

                return result;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
                if (result.getBoolean(PBSServerConst.RESULT)) {
                    context.getGlobalVariable().setIsAuth(false);
                    //set all the selections made by user (username, pass, server,
                    // roles, projlocs, orgs, etc...)
                    PandoraHelper.setAccountData(getActivity());
                    //set the apps to only start auto sync after successfully send the role to Server.
                    PandoraHelper.setAutoSync(getActivity(), context.getGlobalVariable().getAd_user_name(),
                            PBSAccountInfo.ACCOUNT_TYPE);
                    //todo . set flag as already sent role to server.
                    context.displayView(0, false);
                } else {
                    PandoraHelper.showMessage(context, result.getString(
                                    result.getString(PandoraConstant.TITLE)));
                }
                context.dismissProgressDialog();
            }
        }.execute();
    }

    public void completedInitialSync() {
        refreshSyncState();
        addSpinnerData();
    }
}