package com.pbasolutions.android.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSSurveyController;
import com.pbasolutions.android.model.MSurvey;

import java.util.ArrayList;
import java.util.List;

public class NewAuditFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "NewAuditFragment";
    private Button section;
    private LinearLayout mainLL;
    private int currPosition;
    private MSurvey audit;
    private ArrayList<MSurvey> questions;
    private ArrayList<String> sections;

    /**
     * Constructor method.
     */
    public NewAuditFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.survey_new, container, false);
        section = (Button) rootView.findViewById(R.id.atSection);
        mainLL = (LinearLayout) rootView.findViewById(R.id.mainLL);

        Bundle bundle = getArguments();
        if (bundle != null) {
            currPosition = bundle.getInt("pos");
//            audit = (MSurvey) bundle.getSerializable(PBSSurveyController.ARG_SURVEY);
            sections = (ArrayList<String>) bundle.getSerializable(PBSSurveyController.ARG_SECTIONS);
            questions = (ArrayList<MSurvey>) bundle.getSerializable(PBSSurveyController.ARG_QUESTIONS);

            String sectionName = sections.get(currPosition);
            if (sectionName == null || sectionName.equals("") || sectionName.equals("null"))
                section.setVisibility(View.GONE);
            else section.setText(getString(R.string.label_section) + sectionName + " (" + (currPosition + 1) + "/" + sections.size() + ")");
            List<SpinnerPair> ratingList = new ArrayList<>();
            SpinnerPair pair = new SpinnerPair();
            pair.setKey(null);
            pair.setValue("-- Please rate --");
            ratingList.add(pair);
            pair = new SpinnerPair();
            pair.setKey("-1");
            pair.setValue("N/A");
            ratingList.add(pair);
            for (int i = 0; i < 11; i++) {
                pair = new SpinnerPair();
                pair.setKey(String.valueOf(i));
                pair.setValue(String.valueOf(i));
                ratingList.add(pair);
            }

            for (int i = 0; i < questions.size(); i++) {
                MSurvey question = questions.get(i);
                if (sectionName.equals(question.getSectionname())) {
                    LinearLayout subLL = new LinearLayout(getActivity(), null, R.style.PTableVirtRow);
                    TextView tvQuestion = new TextView(getActivity(), null, R.style.TableInfoLabel);
                    TextView tvQuestionDesc = new TextView(getActivity(), null, R.style.TableValue);
                    Spinner rating = new Spinner(getActivity());
                    final EditText etRemarks = new EditText(getActivity());

                    rating.setId(Integer.parseInt(question.getC_SurveyTemplateQuestion_ID() + "1"));
                    etRemarks.setId(Integer.parseInt(question.getC_SurveyTemplateQuestion_ID() + "2"));
                    subLL.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 50, 0, 50);
                    subLL.setLayoutParams(layoutParams);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tvQuestion.setText(Html.fromHtml(question.getQuestion(), Html.FROM_HTML_MODE_COMPACT));
                        if (question.getQuestionDesc() != null && !question.getQuestionDesc().equals("") && !question.getQuestionDesc().equalsIgnoreCase("null"))
                            tvQuestionDesc.setText(Html.fromHtml(question.getQuestionDesc(), Html.FROM_HTML_MODE_COMPACT));
                        else tvQuestionDesc.setVisibility(View.GONE);
                    } else {
                        tvQuestion.setText(Html.fromHtml(question.getQuestion()));
                        if (question.getQuestionDesc() != null && !question.getQuestionDesc().equals("") && !question.getQuestionDesc().equalsIgnoreCase("null"))
                            tvQuestionDesc.setText(Html.fromHtml(question.getQuestionDesc()));
                        else tvQuestionDesc.setVisibility(View.GONE);
                    }
                    PandoraHelper.addListToSpinner(getActivity(), rating, ratingList);
                    rating.setTag(question.getC_SurveyTemplateQuestion_UUID());
                    rating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            TextView selectedText = (TextView) adapterView.getChildAt(0);
                            if (selectedText != null && selectedText.getText().equals("-- Please rate --")) {
                                selectedText.setTextColor(Color.RED);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    etRemarks.setTag(question.getC_SurveyTemplateQuestion_UUID());
                    etRemarks.setMinLines(1);
                    etRemarks.setMaxLines(5);
                    etRemarks.setFilters(new InputFilter[] {new InputFilter.LengthFilter(2000)});
                    etRemarks.setVerticalScrollBarEnabled(true);
                    etRemarks.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    if (question.getAmt() != null && !question.getAmt().equals("")) {
                        rating.setEnabled(false);
                        if (question.getAmt().equals("-1"))
                            rating.setSelection(1);
                        else rating.setSelection(Integer.parseInt(question.getAmt()) + 1);
                        etRemarks.setEnabled(false);
                        etRemarks.setText(question.getRemarks() != null && !question.getRemarks().isEmpty() && !question.getRemarks().equalsIgnoreCase("null") ? PandoraHelper.parseNewLine(question.getRemarks()) : "No remark");
                    }
                    else etRemarks.setHint("Remarks");
                    etRemarks.setOnTouchListener(new View.OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            if (etRemarks.getLineCount() > 5)
                                v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });

                    subLL.addView(tvQuestion);
                    subLL.addView(tvQuestionDesc);
                    subLL.addView(rating);
                    subLL.addView(etRemarks);
                    mainLL.addView(subLL);
                }
            }

            if (sections.size() - 1 == currPosition) {
                LinearLayout subLL = new LinearLayout(getActivity(), null, R.style.PTableVirtRow);
                TextView tvSurveyRemarks = new TextView(getActivity(), null, R.style.TableInfoLabel);
                NewAuditPagerFragment.etSurveyRemarks = new EditText(getActivity());
                subLL.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 50, 0, 50);
                subLL.setLayoutParams(layoutParams);

                tvSurveyRemarks.setTypeface(null, Typeface.BOLD);
                tvSurveyRemarks.setText(getString(R.string.label_surveyremarks));
                NewAuditPagerFragment.etSurveyRemarks.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                NewAuditPagerFragment.etSurveyRemarks.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2000)});
                NewAuditPagerFragment.etSurveyRemarks.setMaxLines(5);
                NewAuditPagerFragment.etSurveyRemarks.setVerticalScrollBarEnabled(true);
                NewAuditPagerFragment.etSurveyRemarks.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        if (NewAuditPagerFragment.etSurveyRemarks.getLineCount() > 5)
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
                if (NewAuditPagerFragment.audit != null && NewAuditPagerFragment.audit.getRemarks() != null) {
                    NewAuditPagerFragment.etSurveyRemarks.setEnabled(false);
                    NewAuditPagerFragment.etSurveyRemarks.setText(NewAuditPagerFragment.audit.getRemarks());
                    NewAuditPagerFragment.etSurveyRemarks.setMaxLines(Integer.MAX_VALUE);
                }

                subLL.addView(tvSurveyRemarks);
                subLL.addView(NewAuditPagerFragment.etSurveyRemarks);
                mainLL.addView(subLL);
            }
        }

        return rootView;
    }
}
