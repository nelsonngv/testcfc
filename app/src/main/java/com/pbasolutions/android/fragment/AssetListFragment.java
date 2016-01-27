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

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.AssetRVA;
import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.model.MStorage;

/**
 * Created by pbadell on 10/12/15.
 */
public class AssetListFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "AssetListFragment";

    private PBSAssetController assetCont;
    private ObservableArrayList<MStorage> assetList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetCont = new PBSAssetController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.asset_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.asset_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new AsyncTask<Object, Void, Void>() {
            protected LayoutInflater inflater;
            protected RecyclerView recyclerView;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Void doInBackground(Object... params) {
                inflater = (LayoutInflater) params[0];
                recyclerView = (RecyclerView) params[1];

                if (PBSServerConst.cookieStore == null){
                    assetList = null;
                } else {
                    assetList = getMStorage();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void avoid) {
                super.onPostExecute(avoid);
                if (assetList == null)
                    PandoraHelper.showAlertMessage((PandoraMain) getActivity(),
                        getString(R.string.error_logged_out_sync, getString(R.string.assets_and_movements)),
                        PandoraConstant.ERROR, PandoraConstant.OK_BUTTON, null);
                AssetRVA viewAdapter = new AssetRVA(getActivity(), assetList, inflater);
                recyclerView.setAdapter(viewAdapter);

                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(inflater, recyclerView);

        return rootView;
    }

    /**
     * Get mstorage data from server.
     * @return
     */
    private ObservableArrayList<MStorage> getMStorage() {
        PandoraContext pc = ((PandoraMain)getActivity()).globalVariable;
        Bundle input = new Bundle();
        input.putString(assetCont.ARG_PROJECTLOCATION_ID, pc.getC_projectlocation_id());
        input.putString(PBSServerConst.PARAM_URL, pc.getServer_url());
        Bundle result = assetCont.triggerEvent(assetCont.GET_MSTORAGES_EVENT, input, new Bundle(), null);
        return (ObservableArrayList<MStorage>) result.getSerializable(PBSAssetController.ARG_STORAGE);
    }
}
