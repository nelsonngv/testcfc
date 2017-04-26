package com.pbasolutions.android.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
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

/**
 * Created by pbadell on 10/5/15.
 */
public class NewSurveyFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "NewSurveyFragment";
    private Button section;
    private LinearLayout mainLL;
    private int currPosition;
    private MSurvey survey;
    private ArrayList<MSurvey> questions;
    private ArrayList<String> sections;

    /**
     * Constructor method.
     */
    public NewSurveyFragment() {
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
//            survey = (MSurvey) bundle.getSerializable(PBSSurveyController.ARG_SURVEY);
            sections = (ArrayList<String>) bundle.getSerializable(PBSSurveyController.ARG_SECTIONS);
            questions = (ArrayList<MSurvey>) bundle.getSerializable(PBSSurveyController.ARG_QUESTIONS);

            String sectionName = sections.get(currPosition);
            section.setText("Section: " + sectionName);
            List<SpinnerPair> ratingList = new ArrayList<>();
            SpinnerPair pair = new SpinnerPair();
            pair.setKey(null);
            pair.setValue("-- Please rate --");
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
                    EditText etRemarks = new EditText(getActivity());

                    rating.setId(Integer.parseInt(question.getC_SurveyTemplateQuestion_ID()+"1"));
                    etRemarks.setId(Integer.parseInt(question.getC_SurveyTemplateQuestion_ID()+"2"));
                    subLL.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 50, 0, 50);
                    subLL.setLayoutParams(layoutParams);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tvQuestion.setText(Html.fromHtml(question.getQuestion(), Html.FROM_HTML_MODE_COMPACT));
                        tvQuestionDesc.setText(Html.fromHtml(question.getQuestionDesc(), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        tvQuestion.setText(Html.fromHtml(question.getQuestion()));
                        tvQuestionDesc.setText(Html.fromHtml(question.getQuestionDesc()));
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
                    etRemarks.setSingleLine(false);
                    etRemarks.setMaxLines(5);
                    etRemarks.setHorizontalScrollBarEnabled(true);
                    etRemarks.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    if (question.getRemarks() != null && !question.getRemarks().equals("") && question.getAmt() != null && !question.getAmt().equals("")) {
                        rating.setEnabled(false);
                        rating.setSelection(Integer.parseInt(question.getAmt()) + 1);
                        etRemarks.setEnabled(false);
                        etRemarks.setText(question.getRemarks());
                    }

                    subLL.addView(tvQuestion);
                    subLL.addView(tvQuestionDesc);
                    subLL.addView(rating);
                    subLL.addView(etRemarks);
                    mainLL.addView(subLL);
                }
            }
        }

        return rootView;
    }
}
