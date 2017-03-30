package com.pbasolutions.android.fragment;

import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.AssetMovementRVA;
import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.model.MMovement;
import com.wnafee.vector.compat.ResourcesCompat;

import java.util.Collections;
import java.util.Comparator;

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

    SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView sortDocDesc;
    private TextView sortDocAsc;
    private TextView sortDateDesc;
    private TextView sortDateAsc;
    private TextView sortStatusDesc;
    private TextView sortStatusAsc;

    protected String movementDetailTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetCont = new PBSAssetController(getActivity());

        movementDetailTitle = getString(R.string.title_movement_details);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.asset_movement_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.asset_movement_rv);
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
                        new AssetMovementDetails(), movementDetailTitle);

                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(inflater, recyclerView);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                movements = getMovements();
                AssetMovementRVA viewAdapter = new AssetMovementRVA(getActivity(), movements, inflater);
                recyclerView.setAdapter(viewAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        sortDocDesc = (TextView) rootView.findViewById(R.id.DocSortDesc);
        sortDocAsc = (TextView) rootView.findViewById(R.id.DocSortAsc);
        sortDateDesc = (TextView) rootView.findViewById(R.id.DateSortDesc);
        sortDateAsc = (TextView) rootView.findViewById(R.id.DateSortAsc);
        sortStatusDesc = (TextView) rootView.findViewById(R.id.StatusSortDesc);
        sortStatusAsc = (TextView) rootView.findViewById(R.id.StatusSortAsc);
        sortDocDesc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getActivity(), R.drawable.sortdescicon), null, null, null);
        sortDocAsc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getActivity(), R.drawable.sortascicon), null, null, null);
        sortDateDesc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getActivity(), R.drawable.sortdescicon), null, null, null);
        sortDateAsc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getActivity(), R.drawable.sortascicon), null, null, null);
        sortStatusDesc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getActivity(), R.drawable.sortdescicon), null, null, null);
        sortStatusAsc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getActivity(), R.drawable.sortascicon), null, null, null);

        sortDocAsc.setVisibility(View.GONE);
        sortDateAsc.setVisibility(View.GONE);
        sortStatusAsc.setVisibility(View.GONE);

        sortDocDesc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Collections.sort(movements, new Comparator<MMovement>(){
                    @Override
                    public int compare(MMovement lhs, MMovement rhs) {
                        return lhs.getDocumentNo().compareTo(rhs.getDocumentNo());
                    }
                });
                AssetMovementRVA viewAdapter = new AssetMovementRVA(getActivity(),movements, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, movements, getActivity(),
                        new AssetMovementDetails(), movementDetailTitle);
                sortDocDesc.setVisibility(View.GONE);
                sortDocAsc.setVisibility(View.VISIBLE);
                return false;
            }
        });

        sortDocAsc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Collections.sort(movements, new Comparator<MMovement>(){
                    @Override
                    public int compare(MMovement lhs, MMovement rhs) {
                        return rhs.getDocumentNo().compareTo(lhs.getDocumentNo());
                    }
                });
                AssetMovementRVA viewAdapter = new AssetMovementRVA(getActivity(),movements, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, movements, getActivity(),
                        new AssetMovementDetails(), movementDetailTitle);
                sortDocAsc.setVisibility(View.GONE);
                sortDocDesc.setVisibility(View.VISIBLE);
                return false;
            }


        });

        sortDateDesc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                for (int i = 0; i< movements.size(); i++){
//                    movements.get(i).getRequestDate();
//                    Log.i(TAG, "onTouch: " + movements.get(i).getRequestDate());
//                }
                Collections.sort(movements, new Comparator<MMovement>(){
                    @Override
                    public int compare(MMovement lhs, MMovement rhs) {

                        return lhs.getMovementDateFormat().compareTo(rhs.getMovementDateFormat());
                    }
                });
                AssetMovementRVA viewAdapter = new AssetMovementRVA(getActivity(),movements, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, movements, getActivity(),
                        new AssetMovementDetails(), movementDetailTitle);
                sortDateDesc.setVisibility(View.GONE);
                sortDateAsc.setVisibility(View.VISIBLE);
                return false;
            }
        });

        sortDateAsc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Collections.sort(movements, new Comparator<MMovement>(){
                    @Override
                    public int compare(MMovement lhs, MMovement rhs) {
                        return rhs.getMovementDateFormat().compareTo(lhs.getMovementDateFormat());
                    }
                });
                AssetMovementRVA viewAdapter = new AssetMovementRVA(getActivity(),movements, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, movements, getActivity(),
                        new AssetMovementDetails(), movementDetailTitle);
                sortDateDesc.setVisibility(View.VISIBLE);
                sortDateAsc.setVisibility(View.GONE);
                return false;
            }
        });

        sortStatusDesc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Collections.sort(movements, new Comparator<MMovement>(){
                    @Override
                    public int compare(MMovement lhs, MMovement rhs) {
                        return rhs.getStatus().compareTo(lhs.getStatus());
                    }
                });
                AssetMovementRVA viewAdapter = new AssetMovementRVA(getActivity(),movements, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, movements, getActivity(),
                        new AssetMovementDetails(), movementDetailTitle);
                sortStatusDesc.setVisibility(View.GONE);
                sortStatusAsc.setVisibility(View.VISIBLE);
                return false;
            }
        });

        sortStatusAsc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Collections.sort(movements, new Comparator<MMovement>(){
                    @Override
                    public int compare(MMovement lhs, MMovement rhs) {
                        return lhs.getStatus().compareTo(rhs.getStatus());
                    }
                });
                AssetMovementRVA viewAdapter = new AssetMovementRVA(getActivity(),movements, inflater);
                recyclerView.setAdapter(viewAdapter);
                PandoraHelper.addRecyclerViewListener(recyclerView, movements, getActivity(),
                        new AssetMovementDetails(), movementDetailTitle);
                sortStatusDesc.setVisibility(View.VISIBLE);
                sortStatusAsc.setVisibility(View.GONE);
                return false;
            }
        });

        return rootView;
    }

    private ObservableArrayList<MMovement> getMovements() {
        PandoraContext pc = ((PandoraMain)getActivity()).getGlobalVariable();
        Bundle input = new Bundle();
        input.putInt(assetCont.ARG_PROJECTLOCATION_ID, Integer.parseInt(pc.getC_projectlocation_id()));
        input.putInt(assetCont.ARG_AD_USER_ID, Integer.parseInt(pc.getAd_user_id()));
        input.putString(PBSServerConst.PARAM_URL, pc.getServer_url());
        Bundle result = assetCont.triggerEvent(assetCont.GET_MOVEMENTS_FROM_SERVER_EVENT, input, new Bundle(), null);
        return (ObservableArrayList)result.getSerializable(assetCont.ARG_MOVEMENT);
    }

}
