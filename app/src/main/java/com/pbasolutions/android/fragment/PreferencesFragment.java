package com.pbasolutions.android.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by pbadell on 8/28/15.
 */
public class PreferencesFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "PreferencesFragment";
    private PandoraMain context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (PandoraMain) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preferences, container, false);
        //this one is not enable for user view.
//        rootView.findViewById(R.id.exportLogButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                exportLog(v);
//            }
//        });
//
//        rootView.findViewById(R.id.exportDataButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                exportData(v);
//            }
//        });

        TextView phoneID = (TextView) rootView.findViewById(R.id.phoneID);
        phoneID.setText(PandoraHelper.getDeviceID(context));

        final CheckBox isFullComment = (CheckBox) rootView.findViewById(R.id.displayFullCommentRB);
        rootView.findViewById(R.id.displayFullCommentRB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getGlobalVariable().setIsFullComment(isFullComment.isChecked());
            }
        });

        TextView version = (TextView) rootView.findViewById(R.id.version);
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void exportLog(View view) {
        try {
            String fileName = "logcat_" + System.currentTimeMillis() + ".txt";
            File outputFile = new File(Environment.getExternalStorageDirectory(), fileName);
            @SuppressWarnings("unused")
            Process process = Runtime.getRuntime().exec("logcat -f " + outputFile.getAbsolutePath());
            Toast.makeText(getActivity(), "Log Exported!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
    }

    /**
     * to remove only for testing purpose.
     * @param view
     */
    public void exportData(View view) {
        String dbName  = "cfc.db";
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/" + "com.pbasolutions.android.syncAdapter"
                + "/databases/" + dbName;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, dbName);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(getActivity(), "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            Log.e(TAG, PandoraConstant.ERROR + PandoraConstant.SPACE + e.getMessage());
        }
    }
}
