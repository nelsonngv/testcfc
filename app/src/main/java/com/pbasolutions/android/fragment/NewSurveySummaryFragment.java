package com.pbasolutions.android.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.R;
import com.pbasolutions.android.controller.PBSSurveyController;
import com.pbasolutions.android.model.MSurvey;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Created by pbadell on 10/5/15.
 */
public class NewSurveySummaryFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final int NEXT_ID = 1;
    private MenuItem next;
    private LinearLayout mainLL;
    private ArrayList<MSurvey> answerList;
    private ArrayList<String> sectionList;
    private Bundle bundle;
    private EditText etSurveyRemarks;
    private boolean isViewing;

    /**
     * Constructor method.
     */
    public NewSurveySummaryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.survey_summary, container, false);
        setUI(rootView);

        bundle = getArguments();
        if (bundle != null) {
            isViewing = bundle.getBoolean("isViewing", false);
            answerList = bundle.getParcelableArrayList("answers");
            sectionList = bundle.getStringArrayList("sections");
            ArrayList<Pair> ratings = new ArrayList<>();
            int totalRating = 0;
            for (int i = 0; i < sectionList.size(); i++) {
                String sectionName = sectionList.get(i);
                int count = 0, rating = 0;
                for (int j = 0; j < answerList.size(); j++) {
                    MSurvey answer = answerList.get(j);
                    if (sectionName.equals(answer.getSectionname())) {
                        rating += Integer.parseInt(answer.getAmt());
                        count++;
                    }
                }
                if (!sectionName.equalsIgnoreCase("null"))
                    ratings.add(new Pair<>(sectionName, rating + "/" + (count * 10)));
                totalRating += rating;
            }
            ratings.add(new Pair<>("Total", totalRating + "/" + (answerList.size() * 10)));

            for (int i = 0; i < ratings.size(); i++) {
                Pair rating = ratings.get(i);
                LinearLayout subLL = new LinearLayout(getActivity(), null, R.style.PTableVirtRow);
                LinearLayout ratingLL = new LinearLayout(getActivity());
                TextView tvSection = new TextView(getActivity(), null, R.style.TableInfoLabel);
                TextView tvRating = new TextView(getActivity(), null, R.style.TableValue);
                TextView tvAvgRating = new TextView(getActivity(), null, R.style.TableValue);
                TextView tvPercRating = new TextView(getActivity(), null, R.style.TableValue);
                subLL.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 50, 0, 50);
                subLL.setLayoutParams(layoutParams);

                String[] ratingPair = rating.second.toString().split("/");
                tvSection.setTypeface(null, Typeface.BOLD);
                tvSection.setText(rating.first.toString());
                tvRating.setText(getString(R.string.label_rating) + rating.second.toString());
                tvAvgRating.setText(getString(R.string.label_average) + (ratingPair[0].equals("0") ? "0" : new BigDecimal(ratingPair[0]).divide(new BigDecimal(ratingPair[1]).divide(new BigDecimal(10), 2, RoundingMode.HALF_UP), 2, RoundingMode.HALF_UP).toString()) + "/10");
                tvPercRating.setText(getString(R.string.label_percentage) + (ratingPair[0].equals("0") ? "0" : new BigDecimal(ratingPair[0]).divide(new BigDecimal(ratingPair[1]).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP), 2, RoundingMode.HALF_UP).toString()) + "%");

                subLL.addView(tvSection);
                subLL.addView(tvRating);
                subLL.addView(tvAvgRating);
                subLL.addView(tvPercRating);
//                subLL.addView(tvSection);
//                subLL.addView(ratingLL);
                mainLL.addView(subLL);
            }

            LinearLayout subLL = new LinearLayout(getActivity(), null, R.style.PTableVirtRow);
            TextView tvSurveyRemarks = new TextView(getActivity(), null, R.style.TableInfoLabel);
            etSurveyRemarks = new EditText(getActivity());
            subLL.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 50, 0, 50);
            subLL.setLayoutParams(layoutParams);

            tvSurveyRemarks.setTypeface(null, Typeface.BOLD);
            tvSurveyRemarks.setText(getString(R.string.label_remarks));
            etSurveyRemarks.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            etSurveyRemarks.setFilters(new InputFilter[] {new InputFilter.LengthFilter(2000)});
            etSurveyRemarks.setMaxLines(5);
            etSurveyRemarks.setVerticalScrollBarEnabled(true);
            etSurveyRemarks.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (etSurveyRemarks.getLineCount() > 5)
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            if (isViewing) {
                etSurveyRemarks.setEnabled(false);
                etSurveyRemarks.setText(NewAuditPagerFragment.audit.getRemarks());
                etSurveyRemarks.setMaxLines(Integer.MAX_VALUE);
            }

            subLL.addView(tvSurveyRemarks);
            subLL.addView(etSurveyRemarks);
            mainLL.addView(subLL);
        }

        return rootView;
    }

    void setUI(View rootView) {
        mainLL = (LinearLayout) rootView.findViewById(R.id.mainLL);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isViewing) {
            menu.clear();
            next = menu.add(0, NEXT_ID, 1, "Next");
            next.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            next.setIcon(R.drawable.ic_done);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isViewing) {
            int id = item.getItemId();
            switch (id) {
                case NEXT_ID: {
                    next();
                    break;
                }
                default:
                    break;
            }
        }
        return true;
    }

    protected void next() {
        PandoraHelper.hideSoftKeyboard(getActivity());
        bundle.putString(MSurvey.DATETRX_COL, PBSSurveyController.dateTrx);
        bundle.putString(MSurvey.REMARKS_COL, etSurveyRemarks.getText().toString());
        Fragment fragment = new NewSurveySignFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.commit();
//        PandoraMain.instance.getSupportActionBar().setTitle(getString(R.string.title_sign_survey));
    }
}
