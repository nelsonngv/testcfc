package com.pbasolutions.android.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSSurveyController;
import com.pbasolutions.android.model.MSurvey;
import com.pbasolutions.android.model.ModelConst;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by pbadell on 10/5/15.
 */
public class NewSurveyStartFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final int START = 500;
    PBSAttendanceController attendanceCont;
    PBSSurveyController surveyCont;
    ContentResolver cr;
    protected Spinner projLocationSpinner;
    protected EditText etName;
    protected Spinner templateSpinner;
    protected ArrayAdapter templateAdapter;
    protected MSurvey survey;

    /**
     * Constructor method.
     */
    public NewSurveyStartFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        surveyCont = new PBSSurveyController(getActivity());
        attendanceCont = new PBSAttendanceController(getActivity());
        cr = getActivity().getContentResolver();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.survey_start, container, false);
        setUI(rootView);
        setUIListener();

        return rootView;
    }

    void setUI(View rootView) {
        projLocationSpinner = (Spinner) rootView.findViewById(R.id.atProjLocation);
        etName = (EditText) rootView.findViewById(R.id.atSurveyName);
        templateSpinner = (Spinner) rootView.findViewById(R.id.atTemplate);
        TextView tvName = (TextView) rootView.findViewById(R.id.tvSurveyName);
        PandoraHelper.setAsterisk(tvName);

        ArrayAdapter projLocNameAdapter = PandoraHelper.addListToSpinner(getActivity(), projLocationSpinner, getProjLocList());
        if (projLocNameAdapter.getCount() > 0) {
            PandoraMain pandoraMain = PandoraMain.instance;
            String projLocID = pandoraMain.getGlobalVariable().getC_projectlocation_id();

            for (int i = 0; i < projLocNameAdapter.getCount(); i++) {
                SpinnerPair pair = (SpinnerPair) projLocNameAdapter.getItem(i);
                if (projLocID.equalsIgnoreCase(pair.getKey())) {
                    projLocationSpinner.setSelection(i);
                    break;
                }
            }
        }

        templateAdapter = PandoraHelper.addListToSpinner(getActivity(), templateSpinner, getTemplateList());
    }

    void setUIListener() {
        templateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerPair newPair = (SpinnerPair) templateAdapter.getItem(position);
                PBSSurveyController.templateUUID = newPair.getKey();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        MenuItem add;
        add = menu.add(0, START, 1, getString(R.string.label_button_start));
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        add.setIcon(R.drawable.ic_done);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case START: {
                startSurvey();
                return true;
            }
            default: return false;
        }
    }

    public List<SpinnerPair> getProjLocList() {
        Bundle input = new Bundle();
        Bundle result = attendanceCont.triggerEvent(PBSAttendanceController.GET_PROJECTLOCATIONS_EVENT,
                        input, new Bundle(), null);
        return result.getParcelableArrayList(PBSAttendanceController.ARG_PROJECTLOCATIONS);
    }

    public List<SpinnerPair> getTemplateList() {
        Bundle input = new Bundle();
        Bundle result = surveyCont.triggerEvent(PBSSurveyController.GET_TEMPLATES_EVENT,
                        input, new Bundle(), null);
        return result.getParcelableArrayList(PBSSurveyController.ARG_TEMPLATES);
    }

    protected void startSurvey() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        SpinnerPair projLocPair = (SpinnerPair) projLocationSpinner.getSelectedItem();
        SpinnerPair templatePair = (SpinnerPair) templateSpinner.getSelectedItem();
        if (etName.getText().toString().equals("")) {
            PandoraHelper.showWarningMessage(getActivity(), "Please enter the Survey name");
            etName.requestFocus();
            imm.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT);
            return;
        }
        if (templatePair.getKey() == null) {
            PandoraHelper.showWarningMessage(getActivity(), getString(
                    R.string.no_list_error, getString(R.string.label_template)));
            return;
        }

        Date date = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dt.setTimeZone(TimeZone.getDefault());
        String now = dt.format(date);
        PBSSurveyController.name = etName.getText().toString();
        PBSSurveyController.projLocUUID = ModelConst.mapUUIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE, ModelConst.C_PROJECTLOCATION_ID_COL, projLocPair.getKey(), ModelConst.C_PROJECTLOCATION_UUID_COL, cr);
        PBSSurveyController.templateUUID = templatePair.getKey();
        PBSSurveyController.dateStart = now;

        ((PandoraMain) getActivity()).
                displayView(PandoraMain.FRAGMENT_NEW_SURVEY, false);
    }
}
