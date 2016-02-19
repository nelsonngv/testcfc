package com.pbasolutions.android.fragment;

import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.CheckPointRVA;
import com.pbasolutions.android.controller.PBSAuthenticatorController;
import com.pbasolutions.android.controller.PBSCheckpointController;
import com.pbasolutions.android.json.PBSProjLocJSON;
import com.pbasolutions.android.model.MCheckPoint;

/**
 * Created by pbadell on 9/22/15.
 */
public class CheckPointFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "CheckPointFragment";

    PBSCheckpointController checkpointController;

    public CheckPointFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkpointController = new PBSCheckpointController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.checkpoint_sequence, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.checkpoint_seq_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new AsyncTask<Object, Void, Bundle>() {
            protected LayoutInflater inflater;
            protected RecyclerView recyclerView;
            protected ObservableArrayList<MCheckPoint> checkPoints;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Object... params) {
                inflater = (LayoutInflater) params[0];
                recyclerView = (RecyclerView) params[1];

                PandoraContext globalVar = ((PandoraMain)getActivity()).globalVariable;

                Bundle result = null;
                //check the global varialble is null or not.
                if (globalVar != null && globalVar.getProjLocJSON() == null) {
                    //find in database.
                    result = checkpointController.triggerEvent(checkpointController.GET_PROJECT_LOCATION_DATA, new Bundle(), new Bundle(), null);
                    PBSProjLocJSON[] projLocJSONs = (PBSProjLocJSON[])result.getSerializable(checkpointController.ARG_PROJECT_LOCATION_JSON);
                    if (projLocJSONs  != null) {
                        globalVar.setProjLocJSON(projLocJSONs);
                        PBSAuthenticatorController authCont = new PBSAuthenticatorController(getActivity());
                        Bundle input = new Bundle();
                        String args[] = {authCont.PROJLOCS_ARG};
                        input.putStringArray(authCont.ARRAY_ARG, args);
                        input.putString(authCont.ARG_ACCOUNT_TYPE, PBSAccountInfo.ACCOUNT_TYPE);
                        input.putString(authCont.USER_NAME_ARG, globalVar.getAd_user_name());
                        input.putString(authCont.PROJLOCS_ARG, new Gson().toJson(projLocJSONs));
                        authCont.triggerEvent(authCont.SET_ACCOUNT_USERDATA_EVENT, input, new Bundle(), null);
                    }

                    checkPoints = null;
                } else {
                    checkPoints = getCheckPointSeqList();
                }
                return result;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
                if (result != null) {
                    PandoraHelper.showMessage((PandoraMain) getActivity(),
                            result.getString(result.getString(PandoraConstant.TITLE)));
                } else {
                    PandoraContext globalVar = ((PandoraMain)getActivity()).globalVariable;
                    if (globalVar != null && checkPoints == null)
                        PandoraHelper.showWarningMessage((PandoraMain)getActivity(),
                                "Project Location is not selected. Please select the project location in defaults setting tab.");
                }

                CheckPointRVA viewAdapter = new CheckPointRVA(getActivity(), checkPoints, inflater);
                recyclerView.setAdapter(viewAdapter);
                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(inflater, recyclerView);

        return rootView;
    }

    private ObservableArrayList<MCheckPoint> getCheckPointSeqList() {
        PandoraContext globalVar = ((PandoraMain)getActivity()).globalVariable;
        if (globalVar != null) {
            String projectLocationUUID = globalVar.getC_projectlocation_uuid();
            if (projectLocationUUID != null) {
                Bundle input = new Bundle();
                input.putString(checkpointController.ARG_PROJECT_LOCATION_UUID, projectLocationUUID);
                Bundle result = checkpointController.triggerEvent(checkpointController.CHECKPOINT_SEQ_ROWS_EVENT, input, new Bundle(), null);
                ObservableArrayList<MCheckPoint>  mCheckPointSeq =  (ObservableArrayList<MCheckPoint>) result.getSerializable(checkpointController.ARG_CHECKPOINT_SEQ);
                return mCheckPointSeq;
            } else {
//                PandoraHelper.showAlertMessage((PandoraMain)getActivity(), "Project Location is not selected. Please select the project location in defaults setting tab.", PandoraConstant.ERROR, "Ok", null);
            }
        }
        return null;
    }
}
