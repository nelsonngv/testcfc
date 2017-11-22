package com.pbasolutions.android.fragment;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pbasolutions.android.BuildConfig;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraController;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.account.PBSAccountInfo;
import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.controller.PBSAuthenticatorController;
import com.pbasolutions.android.json.PBSLoginJSON;
import com.pbasolutions.android.json.PBSProjLocJSON;

/**
 * Created by pbadell on 7/29/15.
 */
public class LoginFragment extends Fragment {
    /**
     * Android account manager.
     */
    public AccountManager accountManager;
    /**
     * Application authentication token type. mean the type of the account. eg for sync? for ??
     */
    private String authTokenType;
    /**
     * Authentication controller, will handle all events in authentication activity.
     */
    private PBSAuthenticatorController authController;

    private String accountName;
    private String accountPassword;
    private String serverURL;
    private String deviceID;
    private PandoraMain context;

    /**
     *
     */
    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ((PandoraMain)getActivity());
        authController = new PBSAuthenticatorController(getActivity());
        accountManager = AccountManager.get(getActivity());
        //Retrieve extended data from the intent if Login activity called
        // from Account Manager framework.
        accountName = getActivity().getIntent()
                .getStringExtra(PBSAuthenticatorController.ARG_ACCOUNT_NAME);
        authTokenType = getActivity().getIntent()
                .getStringExtra(PBSAuthenticatorController.ARG_AUTH_TYPE);
        if (authTokenType == null)
            authTokenType = PBSAccountInfo.AUTHTOKEN_TYPE_FULL_ACCESS;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.act_login, container, false);
        rootView.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        return rootView;
    }

    public void onStart() {
        super.onStart();
        if ( context.getGlobalVariable() != null) {
            if (context.getGlobalVariable().getAd_user_name() != null) {
                ((TextView) getView().findViewById(R.id.accountName))
                        .setText(context.getGlobalVariable().getAd_user_name());
            }
            if (context.getGlobalVariable().getAd_user_password() != null) {
                ((TextView) getView().findViewById(R.id.accountPassword))
                        .setText(context.getGlobalVariable().getAd_user_password());
            }
            if (context.getGlobalVariable().getServer_url() != null) {
                ((TextView) getView().findViewById(R.id.serverURL))
                        .setText(context.getGlobalVariable().getServer_url());
            }
        }
    }

    /**
     * Trigger event to Submit the username and password to server
     * for authentication if theres no authentication token or account
     * created yet in the Account Manager.
     */
    public void submit() {
        PandoraHelper.hideSoftKeyboard(getActivity());
        accountName = ((TextView) getActivity().findViewById(R.id.accountName))
                .getText().toString();
        accountPassword = ((TextView) getActivity().findViewById(R.id.accountPassword))
                .getText().toString();
        serverURL = ((TextView) getActivity().findViewById(R.id.serverURL))
                .getText().toString();
        deviceID = PandoraHelper.getDeviceID(getActivity());

        if (deviceID == null || deviceID.isEmpty()) {
            PandoraHelper.showWarningMessage(getActivity(),
                    "Unable to obtain device ID. Please contact the administrator.");
            return;
        }

        if (accountName.isEmpty() || accountPassword.isEmpty() || serverURL.isEmpty()) {
            PandoraHelper.showWarningMessage(getActivity(),
                    "Please fill up all the fields");
            return;
        }

        // complement server URL
        if (serverURL.startsWith("http://"))
            serverURL = new StringBuilder(serverURL).insert(4, "s").toString();
        else if (!serverURL.startsWith("https://"))
            serverURL = "https://" + serverURL;

        final Bundle bundle = new Bundle();

        bundle.putString(PBSAuthenticatorController.ARG_ACCOUNT_NAME, accountName);
        bundle.putString(PBSAuthenticatorController.ARG_ACCOUNT_TYPE,
                PBSAccountInfo.ACCOUNT_TYPE);
        bundle.putString(PBSAuthenticatorController.USER_PASS_ARG, accountPassword);
        bundle.putString(PBSAuthenticatorController.ARG_AUTH_TYPE,
                PBSAccountInfo.AUTHTOKEN_TYPE_SYNC);
        bundle.putString(PBSAuthenticatorController.SERIAL_ARG, deviceID);
        bundle.putString(PBSAuthenticatorController.SERVER_URL_ARG, serverURL);

        new LoginAsyncTask().execute(bundle);
    }

    public boolean isServer(String serverURL) {
        final Bundle bundle = new Bundle();
        bundle.putString(PBSServerConst.VERSION, serverURL);
        Bundle resultBundle = new Bundle();
        resultBundle = authController.triggerEvent(
                PBSAuthenticatorController.AUTHENTICATE_SERVER, bundle, resultBundle, null);
        //            commented by danny 20160121. will process on out side
//            PandoraHelper.showAlertMessage((PandoraMain)getActivity(),
//                    resultBundle.getString(resultBundle.getString(PandoraConstant.TITLE)),
//                    resultBundle.getString(PandoraConstant.TITLE), PandoraConstant.OK_BUTTON, null);
        return resultBundle.getBoolean(PBSServerConst.RESULT);
    }

    private class LoginAsyncTask extends AsyncTask<Bundle, Void, Bundle> {
        @Override
        protected void onPreExecute() {
            context.showProgressDialog("Connecting...");
        }

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle resultBundle = new Bundle();
            if (isServer(serverURL)) {
                resultBundle = authController.triggerEvent(PBSAuthenticatorController.SUBMIT_LOGIN,
                        params[0], resultBundle, null);
            } else {
                resultBundle.putBoolean(PBSServerConst.RESULT, false);
                resultBundle.putString(PandoraConstant.TITLE, PandoraConstant.ERROR);
                resultBundle.putString(PandoraConstant.ERROR, "The specified server is unavailable. Please try again later.");
            }
            return resultBundle;
        }

        protected void onPostExecute(Bundle resultBundle) {
            context.dismissProgressDialog();
            if (resultBundle.getString(PandoraConstant.ERROR) != null) {
                PandoraHelper.showMessage(getActivity(),
                        resultBundle.getString(resultBundle.getString(PandoraConstant.TITLE)));
                return;
            }

            final PBSLoginJSON loginJSON = (PBSLoginJSON)
                    resultBundle.getSerializable(PBSAuthenticatorController.PBS_LOGIN_JSON);
            if (loginJSON != null) {
                if (loginJSON.getSuccess().equals("TRUE")) {
                    // final PandoraContext globalVariable = ((PandoraMain)getActivity())
                    // .getGlobalVariable();
                    if (context.getGlobalVariable() == null)
                    {
                        context.setGlobalVariable(new PandoraContext());
                    }
                    if (getActivity() == null)
                        return;
                    ((PandoraMain)getActivity()).drawerFragment.resetUsername(accountName);
                    ((PandoraMain)getActivity()).drawerFragment.invalidateView();
                    context.getGlobalVariable().setAd_user_name(accountName);
                    context.getGlobalVariable().setAd_user_id(loginJSON.getUserID());
                    context.getGlobalVariable().setAd_user_password(accountPassword);
                    context.getGlobalVariable().setSerial(deviceID);
                    context.getGlobalVariable().setRoleJSON(loginJSON.getRoles());
                    context.getGlobalVariable().setIsAuth(true);
                    context.getGlobalVariable().setServer_url(serverURL);
                    context.getGlobalVariable().setAuth_token(loginJSON.getToken());
                    SharedPreferences prefs = context.getSharedPreferences(
                            BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
                    prefs.edit().putString("serverURL", serverURL).commit();

                    PandoraController cont = new PandoraController(context);
                    Bundle input = new Bundle();
                    input.putString(PBSAssetController.ARG_AD_USER_ID, context.getGlobalVariable().getAd_user_id());
                    Bundle result = cont.triggerEvent(PandoraController.GET_PROJLOC_EVENT, input, new Bundle(), null);
                    PBSProjLocJSON[] projLoc = (PBSProjLocJSON[]) result.getSerializable(PandoraController.ARG_PROJECT_LOCATION_JSON);
                    context.getGlobalVariable().setProjLocJSON(projLoc);

                    PandoraHelper.getProjLocAvailable(getActivity(), false);

                    PandoraHelper.setAccountData(getActivity());
                    //set the apps to only start auto sync after successfully send the role to Server.
                    PandoraHelper.setAutoSync(getActivity(), context.getGlobalVariable().getAd_user_name(),
                            PBSAccountInfo.ACCOUNT_TYPE);

                    Fragment accountFrag = new AccountFragment();
                    Bundle inputFragment = new Bundle();
                    inputFragment.putInt("INDEX", 1);
                    accountFrag.setArguments(inputFragment);
                    accountFrag.setRetainInstance(true);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, accountFrag);
                    fragmentTransaction.addToBackStack(accountFrag.getClass().getName());
                    fragmentTransaction.commit();

                    ((PandoraMain) getActivity()).getSupportActionBar().setTitle("Account");
                } else {
                    PandoraHelper.showAlertMessage((PandoraMain) getActivity(),
                            "Invalid username or password", PandoraConstant.ERROR, "Retry", "Ok");
                }

            }
        }
    }

}