package com.pbasolutions.android.fragment;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.pbasolutions.android.PandoraContext;
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
    PBSAttendanceController attendanceCont;
    PBSSurveyController surveyCont;
    ContentResolver cr;
    protected Spinner projLocationSpinner;
    protected EditText etName;
    protected Spinner templateSpinner;
    protected Button startButton;
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
        startButton = (Button) rootView.findViewById(R.id.atStart);

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

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSurvey();
            }
        });
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
        SpinnerPair spinnerPair = (SpinnerPair) templateSpinner.getSelectedItem();
        if (spinnerPair.getKey() == null) {
            PandoraHelper.showWarningMessage(getActivity(), getString(
                    R.string.no_list_error, getString(R.string.label_template)));
            return;
        }

        Date date = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dt.setTimeZone(TimeZone.getDefault());
        String now = dt.format(date);
        PBSSurveyController.name = etName.getText().toString();
        PBSSurveyController.templateUUID = spinnerPair.getKey();
        PBSSurveyController.dateStart = now;

        ((PandoraMain) getActivity()).
                displayView(PandoraMain.FRAGMENT_NEW_SURVEY, false);
    }
}
