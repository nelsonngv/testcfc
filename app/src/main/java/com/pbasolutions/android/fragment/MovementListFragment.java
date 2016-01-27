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
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.AssetMovementRVA;
import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.model.MMovement;

/**
 * Created by pbadell on 10/8/15.
 */
public class MovementListFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "MovementListFragment";
    /**
     *
     */
    private PBSAssetController assetCont;
    /**
     *
     */
    private ObservableArrayList<MMovement> movements;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetCont = new PBSAssetController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.asset_movement_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.asset_movement_rv);
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
                    movements = null;
                } else {
                    movements = getMovements();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void av) {
                super.onPostExecute(av);

                AssetMovementRVA viewAdapter = new AssetMovementRVA(getActivity(), movements, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, movements, getActivity(),
                        new AssetMovementDetails(), getString(R.string.title_movement_details));

                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(inflater, recyclerView);

        return rootView;
    }

    private ObservableArrayList<MMovement> getMovements() {
        PandoraContext pc = ((PandoraMain)getActivity()).globalVariable;
        Bundle input = new Bundle();
        input.putInt(assetCont.ARG_PROJECTLOCATION_ID, Integer.parseInt(pc.getC_projectlocation_id()));
        input.putInt(assetCont.ARG_AD_USER_ID, Integer.parseInt(pc.getAd_user_id()));
        input.putString(PBSServerConst.PARAM_URL, pc.getServer_url());
        Bundle result = assetCont.triggerEvent(assetCont.GET_MOVEMENTS_FROM_SERVER_EVENT, input, new Bundle(), null);
        return (ObservableArrayList)result.getSerializable(assetCont.ARG_MOVEMENT);
    }

}
