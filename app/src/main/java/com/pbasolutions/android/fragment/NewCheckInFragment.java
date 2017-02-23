package com.pbasolutions.android.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.controller.PBSCheckInController;
import com.pbasolutions.android.model.ModelConst;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pbadell on 8/5/15.
 */
public class NewCheckInFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "NewCheckInFragment";
    PBSCheckInController checkInController;
    private TextView scanInfo;
    private TextView tagID;
    private TextView latitudeInfo;
    private TextView longitudeInfo;
    private TextView timestampInfo;
    private EditText commentInfo;
    private NfcAdapter mNfcAdapter;
    private ContentValues contentValues;
    ProgressBar progressBar;
    PandoraMain context;
    private static final int SAVE_LINE_ID = 1;
    private Intent nfcIntent = null;

    public NewCheckInFragment() {
    }

    public Intent getNfcIntent() {
        return nfcIntent;
    }

    public void setNfcIntent(Intent intent) {
        this.nfcIntent = intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        contentValues = new ContentValues();
        checkInController = new PBSCheckInController(getActivity());
        context = (PandoraMain) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.checkin, container, false);
        scanInfo = (TextView) rootView.findViewById(R.id.scanSerialTextInfo);
        commentInfo = (EditText) rootView.findViewById(R.id.serialComment);
        tagID  = (TextView) rootView.findViewById(R.id.scanSerialTextID);
        timestampInfo = (TextView) rootView.findViewById(R.id.serialTimestamp);

        if (mNfcAdapter == null){
            scanInfo.setText("This device doesn't support NFC!");
        }
        if  (mNfcAdapter != null){
            if (!mNfcAdapter.isEnabled()) {
                scanInfo.setText("NFC is disabled");
            } else {
                scanInfo.setText("Please tap on NFC");
            }
        }
        latitudeInfo = (TextView) rootView.findViewById(R.id.serialLatitude);
        longitudeInfo = (TextView) rootView.findViewById(R.id.serialLongitude);

        Bundle resultBundle = null;
        try {
            resultBundle = checkInController.triggerEvent(PBSCheckInController.GET_LOCATION, null, new Bundle(), null);
            latitudeInfo.setText(String.valueOf(resultBundle.getDouble("DEVICE_LAT")));
            longitudeInfo.setText(String.valueOf(resultBundle.getDouble("DEVICE_LONG")));
            Date date= new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            timestampInfo.setText(sdf.format(date));
            contentValues.put("latitude", resultBundle.getDouble("DEVICE_LAT"));
            contentValues.put("longitude", resultBundle.getDouble("DEVICE_LONG"));
        } catch (Exception e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
        if (getNfcIntent() != null) {
            if (getNfcIntent().getAction() != null) {
                if (getNfcIntent().getAction().equals("android.nfc.action.TAG_DISCOVERED")) {
                    Bundle inputBundle = new Bundle();
                    Bundle resultBundle2 = new Bundle();
                    try {
                        resultBundle2 = checkInController.triggerEvent(PBSCheckInController.PROCESS_NFC, inputBundle, resultBundle2, getNfcIntent());
                        scanInfo.setText(resultBundle2.getString("NFC_TEXT"));
                        tagID.setText(resultBundle2.getString("NFC_TAG_ID"));
                        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.INVISIBLE);
                        //convert


                    } catch (Exception e) {
                        Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
                    }
                }
            }
        }
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem add;
        add = menu.add(0, SAVE_LINE_ID, 0, getString(R.string.text_icon_save));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.save);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case SAVE_LINE_ID: {
                try {
                    checkin();
                } catch (Exception e) {
                    Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
                }
                return  true;
            }
            default:return false;
        }
    }

    public void checkin() throws Exception {
        try {
            String orgUUID = ModelConst.mapIDtoColumn(ModelConst.AD_ORG_TABLE, ModelConst.AD_ORG_UUID_COL, context.getGlobalVariable().getAd_org_id(),
                    ModelConst.AD_ORG_TABLE + ModelConst._ID, getActivity().getContentResolver());
            contentValues.put("ad_org_uuid", orgUUID);
            String clientUUID = ModelConst.mapIDtoColumn(ModelConst.AD_CLIENT_TABLE, ModelConst.AD_CLIENT_UUID_COL, context.getGlobalVariable().getAd_client_id(),
                    ModelConst.AD_CLIENT_TABLE + ModelConst._ID, getActivity().getContentResolver());
            contentValues.put("ad_client_uuid", clientUUID);
            String userUUID = ModelConst.mapIDtoColumn(ModelConst.AD_USER_TABLE, ModelConst.AD_USER_UUID_COL, context.getGlobalVariable().getAd_user_id(),
                    ModelConst.AD_USER_TABLE + ModelConst._ID, getActivity().getContentResolver());
            contentValues.put("ad_user_uuid", userUUID);
            contentValues.put("createdBy",userUUID);
            contentValues.put("updatedBy",userUUID);
            Bundle validateBundle = new Bundle();
            validateBundle.putString(PBSCheckInController.NFC_TAG_ID, tagID.getText().toString());
            Bundle validateResult = checkInController.triggerEvent(PBSCheckInController.VALIDATE_TAG, validateBundle, new Bundle(), null);
            if (validateResult != null) {
                String checkpointUUID = validateResult.getString(PBSServerConst.RESULT);
                if (checkpointUUID != null) {
                    contentValues.put(ModelConst.M_CHECKPOINT_TABLE + ModelConst._UUID, checkpointUUID);
                }
            }
            contentValues.put("description", commentInfo.getText().toString());
            Bundle inputBundle = new Bundle();
            inputBundle.putParcelable("Content_Values", contentValues);
            Bundle resultBundle = checkInController.triggerEvent(PBSCheckInController.INSERT_DATA, inputBundle, new Bundle(), null);
            if (resultBundle != null){
                if (PandoraConstant.ERROR.equalsIgnoreCase(resultBundle.getString(PandoraConstant.TITLE)))
                PandoraHelper.showMessage((PandoraMain)getActivity(), resultBundle.getString(resultBundle.getString(PandoraConstant.TITLE)));
                else {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentManager.popBackStack();
//                    fragmentManager.popBackStack();
//                    Fragment frag = new CheckInFragment();
//                    frag.setRetainInstance(true);
//                    fragmentTransaction.replace(R.id.container_body, frag);
//                    fragmentTransaction.addToBackStack(frag.getClass().getName());
//                    fragmentTransaction.commit();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
    }
}
