package com.pbasolutions.android.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.RequisitionLineRVA;
import com.pbasolutions.android.controller.PBSRequisitionController;
import com.pbasolutions.android.model.MProduct;
import com.pbasolutions.android.model.MPurchaseRequest;
import com.pbasolutions.android.model.MPurchaseRequestLine;
import com.pbasolutions.android.model.ModelConst;

import java.util.UUID;

/**
 * Created by pbadell on 11/26/15.
 */
public class NewRequisitionFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "NewRequisitionFragment";

    /**
     * PBSRequisitionController.
     */
    private PBSRequisitionController reqCont ;

    private ObservableArrayList<MPurchaseRequestLine> lines;
//    private MPurchaseRequest pr;

    private EditText documentNo;
    private EditText isApproved;
    private EditText projLoc;
    private TextView requestDate;
    private ImageButton addButton;
    private ImageButton removeButton;
    private Button requestButton;
    private String _UUID;

    protected static final String EVENT_DATE = "EVENT_DATE";
    protected static final String EVENT_ADD_LINE = "EVENT_ADD_LINE";
    private static final String EVENT_REMOVE_LINE = "EVENT_REMOVE_LINE";
    private static final String EVENT_SAVE = "EVENT_SAVE";
    private static final String EVENT_REQUEST = "EVENT_REQUEST";

    protected static final int ACTION_ADD_LINE = 300;
    protected static final int ACTION_REMOVE_LINE = 301;

    ContentResolver cr;

    FragmentActivity activity;
    RequisitionLineRVA viewAdapter;

    RecyclerView recyclerView;
    View rootView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        reqCont = new PBSRequisitionController(activity);
        cr = activity.getContentResolver();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.requisition_new, container, false);
        setUI(rootView);
//        populatePR();
        populatePRLines();
        setValues();
        addListener();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.requisitionline_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        viewAdapter = new RequisitionLineRVA(getActivity(),lines, inflater);
        recyclerView.setAdapter(viewAdapter);
        return rootView;
    }

    private void populatePRLines() {
        if (_UUID != null && !_UUID.isEmpty()) {
            Bundle input = new Bundle();
            input.putString(reqCont.ARG_PURCHASEREQUEST_UUID, get_UUID());
            Bundle result = reqCont.triggerEvent(reqCont.GET_REQUISITIONLINES_EVENT,
                    input, new Bundle(), null);
            lines = (ObservableArrayList<MPurchaseRequestLine>)result
                    .getSerializable(reqCont.ARG_PURCHASEREQUESTLINE_LIST);
//            if (pr != null && lines != null)
//                pr.setLines(lines.toArray(new MPurchaseRequestLine[lines.size()]));
        }
    }
//
//    private void populatePR() {
//        if (_UUID != null && !_UUID.isEmpty()) {
//            Bundle input = new Bundle();
//            input.putString(reqCont.ARG_PURCHASEREQUEST_UUID, get_UUID());
//            Bundle result = reqCont.triggerEvent(reqCont.GET_REQUISITION_EVENT,
//                    input, new Bundle(), null);
//            pr = (MPurchaseRequest)result.getSerializable(reqCont.ARG_PURCHASEREQUEST);
//        }
//    }

    private void setUI(View view){
        documentNo =  (EditText)view.findViewById(R.id.prDocNo) ;
        isApproved = (EditText)view.findViewById(R.id.prStatus) ;
        projLoc = (EditText) view.findViewById(R.id.prProjLoc);
        requestDate = (TextView) view.findViewById(R.id.prRequestDate);
//        addButton = (ImageButton)view.findViewById(R.id.addPrLine);
//        removeButton = (ImageButton) view.findViewById(R.id.removePrLine);
        requestButton = (Button) view.findViewById(R.id.prRequest);
    }

    private void setValues() {
        //set predefined values.
        documentNo.setEnabled(false);
        documentNo.setVisibility(View.INVISIBLE);
        isApproved.setText("Not Approved");
        PandoraContext globalVar = ((PandoraMain)getActivity()).globalVariable;
        String projLocUUID = globalVar.getC_projectlocation_uuid();
        String locName = ModelConst.mapUUIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE,
                ModelConst.C_PROJECTLOCATION_UUID_COL,
                projLocUUID,
                ModelConst.NAME_COL, cr);
        projLoc.setText(locName);
        projLoc.setTag(projLocUUID);
//        if (pr != null && ! pr.getRequestDate().isEmpty()) {
//           requestDate.setText(pr.getRequestDate());
//        } else {
            requestDate.setText(PandoraHelper.getTodayDate("dd-MM-yyyy"));
//        }
    }

    private void addListener(){
        setOnClickListener(requestDate, EVENT_DATE);
//        setOnClickListener(addButton, EVENT_ADD_LINE);
//        setOnClickListener(removeButton, EVENT_REMOVE_LINE);
        setOnClickListener(requestButton, EVENT_REQUEST);
    }


    protected void setOnClickListener(final View object, final String event) {
        object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (event) {
                    case EVENT_DATE: {
                        PandoraHelper.promptFutureDatePicker((TextView) object, getActivity());
                        break;
                    }
                    case EVENT_ADD_LINE: {
                        savePR(false);
                        addLine();
                        break;
                    }
                    case EVENT_REMOVE_LINE: {
                        removeLine();
                        break;
                    }
                    case EVENT_REQUEST: {
//                        savePR(true);
                        requestPR();
                        break;
                    }
                    default:
                        break;
                }
            }
        });
    }

    /**
     * Request PR will make call to Server API for processing the requisition/purchase request.
     */
    private void requestPR() {
//        if (!get_UUID().isEmpty() && pr == null){
//            populatePR();
//            populatePRLines();
//        }
//
//        if (pr == null) {
//            PandoraHelper.showWarningMessage((PandoraMain) getActivity(), getString(
//                    R.string.no_line_error, getString(R.string.request)));
//            return;
//        }

        if (lines == null || lines.size() == 0) {
            PandoraHelper.showWarningMessage((PandoraMain)getActivity(), getString(
                    R.string.no_line_error,getString(R.string.request)));
            return;
        }
        savePR(true);

        MPurchaseRequest pr = new MPurchaseRequest();

        pr.set_UUID(get_UUID());
        pr.setM_PurchaseRequest_UUID(get_UUID());

        PandoraContext pc = ((PandoraMain)getActivity()).globalVariable;

        pr.setC_ProjectLocation_ID(pc.getC_projectlocation_id());
        pr.setC_ProjectLocation_UUID(pc.getC_projectlocation_uuid());
        pr.setProjLocName(projLoc.getText().toString());

        pr.setRequestDate(requestDate.getText().toString());
        pr.setIsApproved("N");
        pr.setStatus("");
        pr.setUserName(pc.getAd_user_name());

        pr.setAD_User_ID(pc.getAd_user_id());
        String ad_user_uuid = pc.getAd_user_uuid();
        if (ad_user_uuid.isEmpty()) {
            ad_user_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE, ModelConst.AD_USER_UUID_COL,
                    pc.getAd_user_id(),
                    ModelConst.AD_USER_TABLE + ModelConst._ID,
                    getActivity().getContentResolver());
        }
        pr.setAD_User_UUID(ad_user_uuid);

        requestButton.setBackgroundColor(getResources().getColor(R.color.colorButtonDisable));

        for (MPurchaseRequestLine aLine : lines) {
            String mProductUUID = aLine.getM_Product_UUID();
            if (mProductUUID!= null && !mProductUUID.isEmpty()) {
                String m_product_id = ModelConst.mapUUIDtoColumn(ModelConst.M_PRODUCT_TABLE, MProduct.M_PRODUCT_UUID_COL,
                        mProductUUID,MProduct.M_PRODUCT_ID_COL, cr);
                aLine.setM_Product_ID(m_product_id);

            }
        }

        pr.setLines(lines.toArray(lines.toArray(new MPurchaseRequestLine[lines.size()])));

        Bundle input = new Bundle();
        input.putSerializable(reqCont.ARG_PURCHASEREQUEST, pr);
        input.putString(PBSServerConst.PARAM_URL, pc.getServer_url());

        new AsyncTask<Bundle, Void, Bundle>() {
            protected LayoutInflater inflater;
            protected RecyclerView recyclerView;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Bundle... params) {
                Bundle result = reqCont.triggerEvent(reqCont.CREATE_REQUISITION_EVENT, params[0], new Bundle(), null);
                return result;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);

                requestButton.setBackgroundColor(getResources().getColor(R.color.colorButtons));

                if (!PandoraConstant.ERROR.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentManager.popBackStack();
//                    fragmentManager.popBackStack();
//                    Fragment frag = new RequisitionFragment();
//                    frag.setRetainInstance(true);
//                    fragmentTransaction.replace(R.id.container_body, frag);
//                    fragmentTransaction.addToBackStack(frag.getClass().getName());
//                    fragmentTransaction.commit();
                } else {
                    PandoraHelper.showWarningMessage((PandoraMain) getActivity(), "Failed to request!");
                }


                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(input);

    }

    private void savePR(boolean saveToDB) {
        if (get_UUID() == null || get_UUID().isEmpty())
        set_UUID(UUID.randomUUID().toString());

        if (!saveToDB) return;

        Bundle input = new Bundle();
        ContentValues cv= new ContentValues();
        cv.put(MPurchaseRequest.M_PURCHASEREQUEST_UUID_COL, get_UUID());
        cv.put(MPurchaseRequest.REQUESTDATE_COL, requestDate.getText().toString());
        PandoraContext cont = ((PandoraMain) activity).globalVariable;
        cv.put(ModelConst.C_PROJECTLOCATION_UUID_COL, cont.getC_projectlocation_uuid());

        String ad_user_uuid = cont.getAd_user_uuid();
        if (ad_user_uuid.isEmpty()) {
            ad_user_uuid = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE, ModelConst.AD_USER_UUID_COL,
                    cont.getAd_user_id(),
                    ModelConst.AD_USER_TABLE + ModelConst._ID,
                    getActivity().getContentResolver());
        }
        cv.put(ModelConst.AD_USER_UUID_COL, ad_user_uuid);
        input.putParcelable(reqCont.ARG_CONTENTVALUES, cv);

        new AsyncTask<Bundle, Void, Void>() {
            protected LayoutInflater inflater;
            protected RecyclerView recyclerView;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Void doInBackground(Bundle... params) {
                reqCont.triggerEvent(reqCont.INSERT_REQ_EVENT, params[0], new Bundle(), null);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(input);
    }

    private void removeLine() {
        new AsyncTask<Void, Void, Bundle>() {
            protected LayoutInflater inflater;
            protected RecyclerView recyclerView;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Void... params) {
                Bundle input = new Bundle();
                input.putSerializable(reqCont.ARG_REQUISITIONLINE_LIST, viewAdapter.getLines());
                Bundle result = reqCont.triggerEvent(reqCont.REMOVE_REQLINES_EVENT, input, new Bundle(), null);
                return result;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
                if (!PandoraConstant.ERROR.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(NewRequisitionFragment.this).attach(NewRequisitionFragment.this).commit();
                }
                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute();
    }

    private boolean addLine() {
        Fragment fragment = new NewRequisitionLineFragment();
        if (get_UUID() != null){
            ((NewRequisitionLineFragment) fragment).setPrUUID(_UUID);
            if (fragment != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragment.setRetainInstance(true);
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
                fragmentTransaction.commit();
                ((PandoraMain) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_newrequisitionline));
            }
        }
        return  true;
    }

    public String get_UUID() {
        return _UUID;
    }

    public void set_UUID(String _UUID) {
        this._UUID = _UUID;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem add;
        add = menu.add(0, ACTION_ADD_LINE, 0, getString(R.string.text_add_line));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.add);

        add = menu.add(0, ACTION_REMOVE_LINE, 1, getString(R.string.text_remove_line));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.minus_white);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case ACTION_ADD_LINE: {
                savePR(false);
                addLine();
                return true;
            }
            case ACTION_REMOVE_LINE: {
                removeLine();
                return true;
            }
        }

        return false;
    }

}
