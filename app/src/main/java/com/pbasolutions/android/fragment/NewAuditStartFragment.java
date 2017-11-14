package com.pbasolutions.android.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAttendanceController;
import com.pbasolutions.android.controller.PBSSurveyController;
import com.pbasolutions.android.model.MSurvey;
import com.pbasolutions.android.model.ModelConst;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NewAuditStartFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final int START = 500;
    PBSAttendanceController attendanceCont;
    PBSSurveyController auditCont;
    ContentResolver cr;
    protected Spinner projLocationSpinner;
    protected EditText etName;
    protected Spinner templateSpinner;
    protected ArrayAdapter templateAdapter;
    protected MSurvey audit;
    protected TextView atSurveyDateTrx;

    /**
     * Constructor method.
     */
    public NewAuditStartFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        auditCont = new PBSSurveyController(getActivity());
        attendanceCont = new PBSAttendanceController(getActivity());
        cr = getActivity().getContentResolver();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.audit_start, container, false);
        setUI(rootView);
        setUIListener();

        return rootView;
    }

    void setUI(View rootView) {
        projLocationSpinner = (Spinner) rootView.findViewById(R.id.atProjLocation);
        etName = (EditText) rootView.findViewById(R.id.atSurveyName);
        templateSpinner = (Spinner) rootView.findViewById(R.id.atTemplate);
        atSurveyDateTrx = (TextView) rootView.findViewById(R.id.atSurveyDateTrx);
        TextView tvTemplate = (TextView) rootView.findViewById(R.id.tvSurveyTemplate);
        TextView tvName = (TextView) rootView.findViewById(R.id.tvSurveyName);
        PandoraHelper.setAsterisk(tvTemplate);
        PandoraHelper.setAsterisk(tvName);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        atSurveyDateTrx.setText(sdf.format(date));

        ArrayAdapter projLocNameAdapter = PandoraHelper.addListToSpinner(getActivity(), projLocationSpinner, getProjLocList());
        if (projLocNameAdapter.getCount() > 0) {
            PandoraMain pandoraMain = ((PandoraMain)getActivity());
            String projLocID = pandoraMain.getGlobalVariable().getC_projectlocation_id();

            for (int i = 0; i < projLocNameAdapter.getCount(); i++) {
                SpinnerPair pair = (SpinnerPair) projLocNameAdapter.getItem(i);
                if (projLocID.equalsIgnoreCase(pair.getKey())) {
                    projLocationSpinner.setSelection(i);
                    break;
                }
            }
        }

        List<SpinnerPair> list = getTemplateList();
        SpinnerPair pair = new SpinnerPair("", "--- Please select ---");
        list.add(0, pair);
        templateAdapter = PandoraHelper.addListToSpinner(getActivity(), templateSpinner, list);
    }

    void setUIListener() {
        templateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerPair newPair = (SpinnerPair) templateAdapter.getItem(position);
                PBSSurveyController.templateUUID = newPair.getKey();
                if (/*etName.getText().toString().equals("") &&*/ !newPair.getKey().equals("")) {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    sdf.setTimeZone(TimeZone.getDefault());
                    String now = sdf.format(date);
                    etName.setText(newPair.getValue() + " " + now);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        atSurveyDateTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PandoraHelper.promptDateTimePicker(atSurveyDateTrx, getActivity());
                final Calendar calendar = Calendar.getInstance();
                int  mYear = calendar.get(Calendar.YEAR);
                int  mMonth = calendar.get(Calendar.MONTH);
                int  mDay = calendar.get(Calendar.DAY_OF_MONTH);
                final int  mHour = calendar.get(Calendar.HOUR_OF_DAY);
                final int  mMinute = calendar.get(Calendar.MINUTE);

                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
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

                                TimePickerDialog timedlg = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        String timeStr = String.format("%02d-%02d-%04d %02d:%02d", m_nDay, m_nMonth + 1, m_nYear, hourOfDay, minute);
                                        atSurveyDateTrx.setText(timeStr);
                                    }
                                }, mHour, mMinute, false);
                                timedlg.show();
                            }
                        }, mYear, mMonth, mDay);
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
                calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));
                dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());

                calendar.add(Calendar.MONTH, -1);
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
                calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
                dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());
                dpd.show();
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
                startAudit();
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
        Bundle result = auditCont.triggerEvent(PBSSurveyController.GET_TEMPLATES_EVENT,
                        input, new Bundle(), null);
        return result.getParcelableArrayList(PBSSurveyController.ARG_TEMPLATES);
    }

    protected void startAudit() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        SpinnerPair projLocPair = (SpinnerPair) projLocationSpinner.getSelectedItem();
        SpinnerPair templatePair = (SpinnerPair) templateSpinner.getSelectedItem();
        if (etName.getText().toString().equals("")) {
            PandoraHelper.showWarningMessage(getActivity(), "Please enter the Audit name");
            etName.requestFocus();
            imm.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT);
            return;
        }
        if (templatePair.getKey() == null || templatePair.getKey().equals("")) {
            PandoraHelper.showWarningMessage(getActivity(), getString(
                    R.string.no_list_error, getString(R.string.label_template)));
            return;
        }

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dt.setTimeZone(TimeZone.getDefault());
        String now = dt.format(date);
        PBSSurveyController.name = etName.getText().toString();
        PBSSurveyController.projLocUUID = ModelConst.mapUUIDtoColumn(ModelConst.C_PROJECT_LOCATION_TABLE, ModelConst.C_PROJECTLOCATION_ID_COL, projLocPair.getKey(), ModelConst.C_PROJECTLOCATION_UUID_COL, cr);
        PBSSurveyController.templateUUID = templatePair.getKey();
        PBSSurveyController.dateStart = now;
        try {
            date = sdf.parse(atSurveyDateTrx.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        PBSSurveyController.dateTrx = dt.format(date);

        ((PandoraMain) getActivity()).
                displayView(PandoraMain.FRAGMENT_NEW_AUDIT, false);
    }
}
