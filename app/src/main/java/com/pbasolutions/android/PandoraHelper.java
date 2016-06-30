package com.pbasolutions.android;


import android.accounts.Account;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.content.SyncInfo;
import android.databinding.ObservableArrayList;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.DimenRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.adapter.SpinAdapter;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAuthenticatorController;
import com.pbasolutions.android.fragment.PBSDetailsFragment;
import com.pbasolutions.android.fragment.ProjTaskDetailsFragment;
import com.pbasolutions.android.json.PBSJson;
import com.pbasolutions.android.json.PBSProjLocJSON;
import com.pbasolutions.android.json.PBSRoleJSON;
import com.pbasolutions.android.listener.FragmentListOnItemClickListener;
import com.pbasolutions.android.listener.RecyclerItemClickListener;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MPurchaseRequest;
import com.pbasolutions.android.model.ModelConst;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by pbadell on 8/7/15.
 */
public class PandoraHelper  {
    /**
     * Class name tag.
     */
    private static final String TAG = "PandoraHelper";
    /**
     * Local database format.
     */
    public static final String LOCAL_DATABASE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // 2001-07-04T12:08:56.235-07:00
    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    // 2001-07-04T12:08:56, assumed to be same time zone as server
    public static final String SERVER_DATE_FORMAT2 = "yyyy-MM-DD'T'hh:mm:ss";

    // 2001-07-04 12:08:56, assumed to be same time zone as server
    public static final String SERVER_DATE_FORMAT3 = "yyyy-MM-DD hh:mm:ss";

    // 2001-07-04 12:08, assumed to be same time zone as server
    public static final String SERVER_DATE_FORMAT4 = "yyyy-MM-DD hh:mm";

    //29-12-2015 field date format
    public static final String FIELD_DATE_FORMAT = "dd-MM-yyyy";

    /**
     *  Is initial sync completed
     */

    public static boolean isInitialSyncCompleted = false;


    public static final String[] formats = {
            LOCAL_DATABASE_DATE_FORMAT,
            SERVER_DATE_FORMAT,
            SERVER_DATE_FORMAT2,
            SERVER_DATE_FORMAT3,
            SERVER_DATE_FORMAT4,
            FIELD_DATE_FORMAT
    };
    public PandoraHelper() {
    }

    public static String OK_BUTTON_PRESSED = "OK_BUTTON_PRESSED";

    //TODO : MAKE THE METHOD MORE GENERIC, handle multiple or single button. is it enuf if true or false return?
    public static Bundle showAlertMessage(PandoraMain context, String message, String title,
                                          String okButton, String cancelButton) {

        final Bundle resultBundle = new Bundle();
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(title);
        if (okButton != null) {
            // Add the buttons
            builder.setPositiveButton(okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    resultBundle.putBoolean(OK_BUTTON_PRESSED, true);
                }
            });
        }
        if (cancelButton != null) {
            builder.setNegativeButton(cancelButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    resultBundle.putBoolean(OK_BUTTON_PRESSED, false);
                }
            });
        }
        // builder.but
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
        return resultBundle;
    }

    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showErrorMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showMessage(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showWarningMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showInfoMessage(Context context, String message) {
        Toast.makeText(context, "Information : " + message, Toast.LENGTH_SHORT).show();
    }

    public static void setVisibleView(View v, boolean bShow) {
        v.setVisibility(bShow ? View.VISIBLE : View.GONE);
    }


    public static boolean isInternetOn(Context context) {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    /**
     * Parse string date to required display format.
     * @param stringDate string date.
     * @param displayFormat string display format
     * @param tz time zone
     * @return parsed string date.
     */
    public static String parseToDisplaySDate(String stringDate, String displayFormat, TimeZone tz) {
        Date date = null;
        if (stringDate != null) {
            for (String parse : formats) {
                date = stringToDate(parse, stringDate);
                //format the date if its not null.
                if (date != null) {
                    SimpleDateFormat output = new SimpleDateFormat(displayFormat);
                    if (tz != null) {
                        output.setTimeZone(tz);
                    }
                    return output.format(date);
                }
            }
        }
        return null;
    }

    public static Date stringToDate(String format, String stringDate) {
        Date date = null;
        SimpleDateFormat input = new SimpleDateFormat(format);
        if (format.equals(LOCAL_DATABASE_DATE_FORMAT)) {
            input.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        try {
            date = input.parse(stringDate);
        } catch (java.text.ParseException e) {
//            Log.e(TAG, e.getMessage());
        }
        return date;
    }

    private static byte[] convertBitmaptoByte (final Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    public static List<SpinnerPair> getGenderList() {
        List<SpinnerPair> gender = new ArrayList<SpinnerPair>();
        gender.add(new SpinnerPair("M", "Male"));
        gender.add(new SpinnerPair("F", "Female"));
        return gender;
    }

    public static List<SpinnerPair> getStatusList() {
        List<SpinnerPair> status = new ArrayList<SpinnerPair>();
        status.add(new SpinnerPair("Y", "Approved"));
        status.add(new SpinnerPair("N", "Not Approved"));
        return status;
    }

    public static List<SpinnerPair> getExpList() {
        List<SpinnerPair> isExp = new ArrayList<SpinnerPair>();
        isExp.add(new SpinnerPair("Y", "Is Approved"));
        isExp.add(new SpinnerPair("N", "Not Approved"));
        return isExp;
    }

    public static List<SpinnerPair> getMaritalStatList() {
        List<SpinnerPair> marStat = new ArrayList<SpinnerPair>();
        marStat.add(new SpinnerPair("M", "Married"));
        marStat.add(new SpinnerPair("S", "Single"));
        return marStat;
    }

    public static ArrayAdapter addListToSpinner(Activity activity, Spinner spinner, List<SpinnerPair> list) {
        ArrayAdapter adapter;
        if (list == null) {
            adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item);
        } else {
            adapter = new SpinAdapter(activity,
                    android.R.layout.simple_spinner_item, list);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return adapter;
    }

    /**
     *
     * @param date
     */
    public static void promptDatePicker(final TextView date, Activity activity) {
        final Calendar c = Calendar.getInstance();
        int  mYear = c.get(Calendar.YEAR);
        int  mMonth = c.get(Calendar.MONTH);
        int  mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(activity,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        date.setText(dayOfMonth + "-"
                                + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(c.getTimeInMillis());
        dpd.show();
    }

    /**
     *
     * @param date
     */
    public static void promptDateTimePicker(final TextView date, final Activity activity) {
        final Calendar c = Calendar.getInstance();
        promptDateTimePicker(date, c, activity);
    }
    public static void promptDateTimePicker(final TextView date, final Calendar calendar, final Activity activity) {
        int  mYear = calendar.get(Calendar.YEAR);
        int  mMonth = calendar.get(Calendar.MONTH);
        int  mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(activity,
                new DatePickerDialog.OnDateSetListener() {
                    int m_nYear;
                    int m_nMonth;
                    int m_nDay;
                    boolean isPrompted = false;
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        if (isPrompted) return;
                        isPrompted = true;

                        m_nYear = year;
                        m_nMonth = monthOfYear;
                        m_nDay = dayOfMonth;

                        Calendar calTime = Calendar.getInstance();
                        TimePickerDialog timedlg = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String timeStr = String.format("%d-%d-%d %02d:%02d", m_nDay, m_nMonth + 1, m_nYear, hourOfDay, minute);
                                date.setText(timeStr);
                            }
                        }, calTime.get(Calendar.HOUR_OF_DAY), calTime.get(Calendar.MINUTE), false);
                        timedlg.show();
                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());
        dpd.show();
    }

    /**
     * Return model instance values based on given uuid.
     * @param modelList
     * @param _uuid
     * @return
     */
    public static IModel getModel(ObservableArrayList<IModel> modelList, String _uuid) {
        IModel targetModel = null;
        if (!modelList.isEmpty()) {
            for (IModel model : modelList) {
                if (_uuid.equalsIgnoreCase(model.get_UUID())) {
                    targetModel = model;
                }
            }
        }
        return targetModel;
    }

    public static boolean isSyncActive(Account account, String authority)
    {
        for(SyncInfo syncInfo : ContentResolver.getCurrentSyncs())
        {
            if(syncInfo.account.equals(account) &&
                    syncInfo.authority.equals(authority))
            {
                return true;
            }
        }
        return false;
    }

    public static void setAutoSync(Activity activity, String userName, String accountType){
        PBSAuthenticatorController authController = new PBSAuthenticatorController(activity);
        Bundle input = new Bundle();
        input.putString(authController.USER_NAME_ARG, userName);
        input.putString(authController.ARG_ACCOUNT_TYPE, accountType);
        Bundle result = authController
                .triggerEvent(authController.GET_USER_ACCOUNT_EVENT,
                        input, new Bundle(), null);

        boolean hasAccount = false;
        if (result != null) {
            Account userAccount = result.getParcelable(authController.USER_ACC_ARG);
            if (userAccount != null) {
                ContentResolver.setSyncAutomatically(userAccount,
                        PBSAccountInfo.ACCOUNT_AUTHORITY, true);
            }
        }
    }

    public static void getProjLocAvailable(Activity activity, boolean showResult) {
        PandoraController cont = new PandoraController(activity);
        PandoraContext globalVar = ((PandoraMain) activity).globalVariable;
        if (globalVar.getProjLocJSON() == null) {
            //find in database.
            Bundle result = cont.triggerEvent(cont.GET_PROJLOC_EVENT, new Bundle(),
                    new Bundle(), null);
            PBSProjLocJSON[] projLocJSONs = (PBSProjLocJSON[]) result
                    .getSerializable(cont.ARG_PROJECT_LOCATION_JSON);
//            isInitialSyncCompleted = projLocJSONs != null;
            if (projLocJSONs != null) {
                setProjLocUserData(globalVar, projLocJSONs, activity);
            }
            if (showResult)
            {
                PandoraHelper.showMessage((PandoraMain) activity, result.getString(result.getString(PandoraConstant.TITLE)));
//                PandoraHelper.showAlertMessage((PandoraMain) activity, result
//                                .getString(result.getString(PandoraConstant.TITLE)),
//                        result.getString(PandoraConstant.TITLE), "Ok", null);
            }
        } else {
//            isInitialSyncCompleted = true;
        }
    }

    public static void setProjLocUserData(PandoraContext context, PBSProjLocJSON[] projLocJSONs, Activity activity) {
        context.setProjLocJSON(projLocJSONs);
        PBSAuthenticatorController authCont = new PBSAuthenticatorController(activity);
        Bundle input = new Bundle();
        String args[] = {authCont.PROJLOCS_ARG};
        input.putStringArray(authCont.ARRAY_ARG, args);
        input.putString(authCont.ARG_ACCOUNT_TYPE, PBSAccountInfo.ACCOUNT_TYPE);
        input.putString(authCont.USER_NAME_ARG, context.getAd_user_name());
        input.putString(authCont.PROJLOCS_ARG, new Gson().toJson(projLocJSONs));
        authCont.triggerEvent(authCont.SET_ACCOUNT_USERDATA_EVENT, input, new Bundle(), null);
    }

    public static void setAccountData(Activity activity){

        PBSAuthenticatorController authController = new PBSAuthenticatorController(activity);

        PandoraContext pandoraContext = ((PandoraMain) activity).globalVariable;
        Bundle inputAccount = new Bundle();
        inputAccount.putString(authController.USER_NAME_ARG, pandoraContext.getAd_user_name());
        inputAccount.putString(authController.ARG_ACCOUNT_TYPE, PBSAccountInfo.ACCOUNT_TYPE);
        inputAccount.putString(authController.ROLE_ARG, pandoraContext.getAd_role_id());
        inputAccount.putString(authController.ORG_ARG, pandoraContext.getAd_org_id());
        inputAccount.putString(authController.CLIENT_ARG, pandoraContext.getAd_client_id());
        //save the project uuid in account manager.
        inputAccount.putString(authController.PROJLOC_ARG, pandoraContext.getC_projectlocation_uuid());
        inputAccount.putString(authController.PROJLOC_ID_ARG, pandoraContext.getC_projectlocation_id());
        inputAccount.putString(authController.PROJLOC_UUID_ARG, pandoraContext.getC_projectlocation_uuid());
        String roleJSON = new Gson().toJson(pandoraContext.getRoleJSON());
        inputAccount.putString(authController.ROLES_ARG, roleJSON);
        inputAccount.putString(authController.ROLE_INDEX_ARG, String.valueOf(pandoraContext.getAd_role_spinner_index()));
        inputAccount.putString(authController.ORG_INDEX_ARG, String.valueOf(pandoraContext.getAd_org_spinner_index()));
        inputAccount.putString(authController.CLIENT_INDEX_ARG, String.valueOf(pandoraContext.getAd_client_spinner_index()));
        inputAccount.putString(authController.PROJLOC_INDEX_ARG, String.valueOf(pandoraContext.getC_ProjectLocation_Index()));

        inputAccount.putString(authController.INITIALSYNC_ARG, pandoraContext.isInitialSynced()? "1" : "0");

        if (pandoraContext.getProjLocJSON() != null) {
            String projLocJSON = new Gson().toJson(pandoraContext.getProjLocJSON());
            inputAccount.putString(authController.PROJLOCS_ARG, projLocJSON);
        }

        inputAccount.putString(authController.AD_USER_ARG, pandoraContext.getAd_user_id());
        authController.triggerEvent(authController.SET_ACCOUNT_DATA_EVENT, inputAccount, new Bundle(), null);
    }


    public static void restoreGlobalVariables(Activity activity) {
        PBSAuthenticatorController authCont = new PBSAuthenticatorController(activity);
        Bundle input = new Bundle();
        input.putString(authCont.ARG_ACCOUNT_TYPE, PBSAccountInfo.ACCOUNT_TYPE);
        input.putString(authCont.ARG_AUTH_TYPE, PBSAccountInfo.AUTHTOKEN_TYPE_SYNC);
        //input.putSerializable(authenticatorController.GET_ACCOUNT_DATA_EVENT, globalVariable);
        Bundle result = authCont.triggerEvent(authCont.GET_ACCOUNT_DATA_EVENT, input, new Bundle(), null);

        PandoraContext var = ((PandoraMain)activity).globalVariable;
        if (result.getString(authCont.USER_NAME_ARG) != null){
            var.setAd_user_name(result.getString(authCont.USER_NAME_ARG));
            var.setAd_user_password(
                    result.getString(authCont.USER_PASS_ARG));
            var.setServer_url(
                    result.getString(authCont.SERVER_URL_ARG));

            String synced = result.getString(authCont.INITIALSYNC_ARG);
            if (synced != null)
                var.setIsInitialSynced(Integer.parseInt(synced) != 0);
        }

        if (result.getString(authCont.AUTH_TOKEN_ARG) != null) {
            //   resultBundle.putString(AUTH_TOKEN_ARG, getAuthToken(userAccount[which], inputBundle.getString(ARG_AUTH_TYPE)));
            var.setAd_user_password(
                    result.getString(authCont.USER_PASS_ARG));
            var.setServer_url(
                    result.getString(authCont.SERVER_URL_ARG));
            var.setSerial(
                    result.getString(authCont.SERIAL_ARG));
            var.setAuth_token(
                    result.getString(authCont.AUTH_TOKEN_ARG));
            var.setAd_role_id(
                    result.getString(authCont.ROLE_ARG));
            var.setAd_org_id(
                    result.getString(authCont.ORG_ARG));
            var.setAd_client_id(
                    result.getString(authCont.CLIENT_ARG));
            var.setAd_user_id(
                    result.getString(authCont.AD_USER_ARG));
            var.setC_projectlocation_uuid(
                    result.getString(authCont.PROJLOC_ARG));
            var.setC_projectlocation_id(
                    result.getString(authCont.PROJLOC_ID_ARG));
            var.setC_projectlocation_name(
                    result.getString(authCont.PROJLOC_NAME_ARG)
            );

            String projLoc = result.getString(authCont.PROJLOCS_ARG);
            if(projLoc != null && !projLoc.equalsIgnoreCase("null")) {
                PBSProjLocJSON projLocs[] = new Gson().fromJson(projLoc, PBSProjLocJSON [].class);
                var.setProjLocJSON(projLocs);
            }
            //Convert the role json to string.
            PBSRoleJSON role[] = (PBSRoleJSON[]) new Gson().fromJson(result.getString(authCont.ROLES_ARG), PBSRoleJSON[].class);
            var.setRoleJSON(role);
            if (result.getString(authCont.ROLE_INDEX_ARG) != null) {
                var.setAd_role_spinner_index(Integer.parseInt(result.getString(authCont.ROLE_INDEX_ARG)));
            }

            if (result.getString(authCont.ORG_INDEX_ARG) != null) {
                var.setAd_org_spinner_index(Integer.parseInt(result.getString(authCont.ORG_INDEX_ARG)));
            }

            if (result.getString(authCont.CLIENT_INDEX_ARG) != null) {
                var.setAd_client_spinner_index(Integer.parseInt(result.getString(authCont.CLIENT_INDEX_ARG)));
            }

            String projLocIndex = result.getString(authCont.PROJLOC_INDEX_ARG);
            if (projLocIndex != null)
                var.setC_ProjectLocation_Spinner_Index(Integer.parseInt(projLocIndex));

        }

    }

    /**
     * Add click listener on the recycler view. when ever user clicks the list item. it will navigate them to the item details.
     * @param rv
     */
    public static void addRecyclerViewListener(RecyclerView rv, ObservableArrayList list,
                                           FragmentActivity context, PBSDetailsFragment targetFragment, String fragmentTitle) {
        ObservableArrayList<IModel> modelList = list;
        rv.addOnItemTouchListener(new RecyclerItemClickListener(context,
                new FragmentListOnItemClickListener(modelList, targetFragment,
                        context, fragmentTitle)));
    }


    public static Pair parseJsonWithArraytoPair(String jsonResult, String objectParam, String arrayParam, String arrayClassName){
        if (jsonResult!= null && !jsonResult.isEmpty()) {
            JsonParser p = new JsonParser();
            JsonObject jsonObj = p.parse(jsonResult).getAsJsonObject();
            String success = jsonObj.get(objectParam).getAsString();
            JsonArray array = jsonObj.getAsJsonArray(arrayParam);
            try {
                Class cls= Class.forName(arrayClassName);
                PBSJson[] prs = (PBSJson[])new Gson().fromJson(array, cls);
                return new Pair(success, prs);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Bundle providerApplyBatch(Bundle output, ContentResolver cr,
                                            ArrayList<ContentProviderOperation> ops, String msg){
        try {
            ContentProviderResult results[] = cr.applyBatch(PBSAccountInfo.ACCOUNT_AUTHORITY, ops);
            for (ContentProviderResult result : results) {
                boolean resultFlag = false;
                if (result.uri != null) {
                    resultFlag = ModelConst.getURIResult(result.uri);
                } else {
                    if (result.count != null && result.count !=0) {
                        resultFlag = true;
                    }
                }

                if (!resultFlag) {
                    output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                    output.putString(PandoraConstant.ERROR, "Fail to " + msg);
                    return output;
                }
            }
            output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
            output.putString(PandoraConstant.RESULT, "Successfully " + msg);
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        } catch (OperationApplicationException e) {
            Log.e(TAG, e.getMessage());
        }
        return output;
    }


    public static String getTodayDate(String dateFormat) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);
    }


    public static Bundle deleteFromList(ObservableArrayList<IModel> modelList, String selection,
                                        ContentResolver cr, Bundle output, String tableName,
                                        String itemName){
        ArrayList<String> uuidList = new ArrayList();
        for (IModel model : modelList){
            if (model.isSelected()) {
                uuidList.add(model.get_UUID());
            }
        }

        ArrayList<ContentProviderOperation> ops =
                new ArrayList<>();

        //loop all selected item
        for (String uuid : uuidList) {
            String[] selectionArgs = {uuid};
            ops.add(ContentProviderOperation
                    .newDelete(ModelConst.uriCustomBuilder(tableName))
                    .withSelection(selection, selectionArgs)
                    .build());
         }

        try {
            ContentProviderResult results[] = cr.applyBatch(PBSAccountInfo.ACCOUNT_AUTHORITY, ops);
            for(ContentProviderResult result : results) {
                boolean resultFlag = false;
                if(result.uri != null){
                    resultFlag = ModelConst.getURIResult(result.uri);
                } else {
                    if (result.count != null && result.count !=0) {
                        resultFlag = true;
                    }
                }

                if (!resultFlag) {
                    output.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                    output.putString(PandoraConstant.ERROR, "Fail to delete selected "+ itemName + "(s).");
                    return output;
                }
            }
            output.putString(PandoraConstant.TITLE, PandoraConstant.RESULT);
            output.putString(PandoraConstant.RESULT, "Successfully deleted selected " +itemName+"(s)");
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        } catch (OperationApplicationException e) {
            Log.e(TAG, e.getMessage());
        }
        return output;
    }

    /**
     * Method to check whether the list has any checked selection.
     * @param list
     * @return
     */
    public static boolean isSelected(ObservableArrayList<IModel> list){
        boolean isSelected = false;
        for (IModel model : list){
            if (model.isSelected()) {
                isSelected = true;
            }
        }
        return isSelected;
    }

    public static void dispayResultMessage(Bundle result, PandoraMain pContext) {
        String resultTitle = result.getString(PandoraConstant.TITLE);
        String title;
        String text;
        if (resultTitle != null && !result.isEmpty()) {
            title = resultTitle;
            text = result.getString(resultTitle);
        } else {
            title = PandoraConstant.RESULT;
            text = "Project Task is up to date";
        }
        PandoraHelper.showAlertMessage(pContext,
                text,
                title, PandoraConstant.OK_BUTTON, null);
    }

    /**
     * Method to replace all "\\n" character in text database to "\n"/system line seperator
     * @param string
     * @return
     */
    public static String parseNewLine(String string){
        if (string != null && !string.isEmpty()) {
            if (string.contains("\\n"))
            return string.replace("\\n", System.getProperty("line.separator"));
        }
        return null;
    }

    /**
     * Removed "\" that has been added to escaped character.
     * @param string
     * @return
     */
    public static String parseEscapedChar(String string) {
        if (string != null && !string.isEmpty()) {
            if (string.contains("\\"))
                return string.replace("\\", "");
        }
        return null;
    }

    /**
     * Add the fragment to undo stack.
     */
    public static void addToStack(final FragmentManager fm) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.addToBackStack(fm.getClass().getName());
        fragmentTransaction.commit();
    }

    public static void setFontSize(TextView titleFont, float size) {
       titleFont.setTextSize(size);
    }

    public static void redirectToFragment(Fragment targetFragment, FragmentActivity fragmentActivity){
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack();
        fragmentManager.popBackStack();
        Fragment frag = targetFragment;
        frag.setRetainInstance(true);
        fragmentTransaction.replace(R.id.container_body, frag);
        fragmentTransaction.addToBackStack(frag.getClass().getName());
        fragmentTransaction.commit();
    }


    public static String getDeviceID(Activity act) {
        TelephonyManager tm = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        Log.d("Information", "Device Id = " + deviceId);
        return deviceId;
    }

}
