package com.pbasolutions.android.controller;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.model.ModelConst;

import java.util.UUID;

/**
 * Created by pbadell on 7/28/15.
 */
public class PBSCheckInController extends ContextWrapper implements PBSIController{
    /**
     * Static Strings.
     */
    public static final String PROCESS_NFC = "PROCESS_NFC";
    public static final String GET_LOCATION = "GET_LOCATION";
    public static final String INSERT_DATA = "INSERT_DATA";
    public static final String NFC_TEXT = "NFC_TEXT";
    public static final String NFC_TAG_ID = "NFC_TAG_ID";
    public static final String DEVICE_LAT = "DEVICE_LAT";
    public static final String DEVICE_LONG = "DEVICE_LONG";
    public static final String VALIDATE_TAG = "VALIDATE_TAG" ;

    private CustomLocationListener customlistener;
    private LocationListener locationListener;

    private Criteria criteria;
    private LocationManager locationManager;
    private String provider;
    private double latitude;
    private double longitude;
    private Context context;

    boolean gps_enabled = false;
    LocationManager lm;

    public PBSCheckInController(Context base) {
        super(base);
        context = base;
    }

    @Override
    public Bundle triggerEvent(String eventName, Bundle bundle, Bundle resultBundle, Object object) {
        switch (eventName) {
            case PROCESS_NFC: {
                processNFC(bundle, resultBundle, object);
                break;
            }
            case GET_LOCATION: {
                processLocation(bundle, resultBundle, object);
                break;
            }
            case INSERT_DATA: {
                insertData(bundle, resultBundle, object);
                break;
            }
            case VALIDATE_TAG: {
                validateTag(bundle, resultBundle, object);
                break;
            }
            default:
                break;
        }
        return resultBundle;
    }

    /**
     * Validate the tag value given by device, is the tag value is a registered m_checkpoint.
     *
     * @param bundle
     * @param resultBundle
     * @param object
     * @return Return null/ valid m_checkpoint_uuid.
     */
    private Bundle validateTag(Bundle bundle, Bundle resultBundle, Object object) {
        String selectionArgs[] = {bundle.getString(NFC_TAG_ID)};
        String projection[] = {ModelConst.M_CHECKPOINT_TABLE + ModelConst._UUID};

        Cursor cursor = getContentResolver().query(ModelConst.uriCustomBuilder(ModelConst.M_CHECKPOINT_TABLE), projection, "value=?", selectionArgs, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String mcheckpointUUID = cursor.getString(0);
            resultBundle.putString(PBSServerConst.RESULT, mcheckpointUUID);
            cursor.close();
        }
        return resultBundle;
    }

    /**
     * Insert check in data to local database.
     * @param bundle
     * @param resultBundle
     * @param object
     * @return
     */
    private Bundle insertData(Bundle bundle, Bundle resultBundle, Object object) {
        ContentValues contentValues = bundle.getParcelable(ModelConst.ARG_CONTENT_VALUES);
        contentValues.put(ModelConst.M_CHECKIN_UUID_COL, UUID.randomUUID().toString());
        Uri uriResult = getContentResolver().insert(ModelConst.uriCustomBuilder(ModelConst.M_CHECKIN_TABLE), contentValues);
        if (uriResult.getPath().equals("/-1")) {
            resultBundle.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
            resultBundle.putString(PandoraConstant.ERROR, "Unable to save");
        } else {
            resultBundle.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
            resultBundle.putString(PandoraConstant.RESULT, "Checked in successfully saved.");
        }
        return resultBundle;
    }

    /**
     * Get the nfc tag id byte in hexadecimal.
     * @param bundle
     * @param resultBundle
     * @param object
     * @return
     */
    private Bundle processNFC(Bundle bundle, Bundle resultBundle, Object object) {
        Intent nfcIntent = (Intent) object;
        if (nfcIntent != null) {
            /**
             * This method gets called, when a new Intent gets associated with the current activity instance.
             * Instead of creating a new activity, onNewIntent will be called. For more information have a look
             * at the documentation.
             *
             * In our case this method gets called, when the user attaches a Tag to the device.
             */
            String action = nfcIntent.getAction();
            Tag tag = nfcIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            String s = action + "\n\n" + tag.toString();
            String tagID = bytesToHexString(tag.getId()).substring(2).toUpperCase();
            StringBuilder str = new StringBuilder(tagID);
            int idx = str.length() - 2;
            while (idx > 0) {
                str.insert(idx, ":");
                idx = idx - 2;
            }
            tagID = str.toString();

            resultBundle.putString(NFC_TEXT, s);
            resultBundle.putString(NFC_TAG_ID, tagID);
        }
        return resultBundle;
    }

    /**
     * Convert from bytes to hexadecimal.
     * @param src bytes soucre.
     * @return string of hexadecimal.
     */
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString();
    }

    /**
     * Get latitude and longitude of current location.
     * @param bundle
     * @param resultBundle
     * @param object
     * @return
     */
    private Bundle processLocation(Bundle bundle, final Bundle resultBundle, Object object) {
        int MIN_TIME_BW_UPDATES = 10000;
        int MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000;
        locationManager = (LocationManager) (context).getSystemService(Context.LOCATION_SERVICE);
        int locationMode;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return resultBundle;
        }
        gps_enabled = locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;

        if(!gps_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("GPS is disabled or not in High Accuracy mode");
            dialog.setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            });
            dialog.show();
        } else {
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setCostAllowed(false);

            provider = locationManager.getBestProvider(criteria, false);

            customlistener = new CustomLocationListener();

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, customlistener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, customlistener);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.PASSIVE_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, customlistener);
                    location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }
            }

            if (location != null) {
                customlistener.onLocationChanged(location);
            }

            resultBundle.putDouble(DEVICE_LAT, latitude);
            resultBundle.putDouble(DEVICE_LONG, longitude);
        }
        return resultBundle;
    }

    /**
     * CustomLocationListener class.
     */
    private class CustomLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    }
}
