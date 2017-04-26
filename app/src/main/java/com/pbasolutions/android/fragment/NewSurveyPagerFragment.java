package com.pbasolutions.android.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSSurveyController;
import com.pbasolutions.android.listener.PBABackKeyListener;
import com.pbasolutions.android.model.MSurvey;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NewSurveyPagerFragment extends PBSDetailsFragment implements PBABackKeyListener {
    /**
     * Class tag name.
     */
    private static final String TAG = "NewSurveyPagerFragment";
    private static final int PREV_ID = 1;
    private static final int NEXT_ID = 2;
    private static int NUM_PAGES = 1;
    private MenuItem prev;
    private MenuItem next;
    private Button complete;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private PBSSurveyController surveyCont;
    private MSurvey survey;
    private ArrayList<MSurvey> questions;
    private ArrayList<String> sections;

    /**
     * Constructor method.
     */
    public NewSurveyPagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.viewpager, container, false);
        surveyCont = new PBSSurveyController(getActivity());

        new processData().execute();
        new setAdapterTask().execute();
        setUI(rootView);
        setUIListener();

        return rootView;
    }

    @Override
    public boolean onBackKeyPressed() {
        return false;
    }

    @Override
    protected void setUI(View view) {
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(R.id.pager);
        complete = (Button) view.findViewById(R.id.atComplete);

        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
    }

    @Override
    protected void setUIListener() {
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                prev.setEnabled(mPager.getCurrentItem() > 0 ? true : false);
                prev.getIcon().setAlpha(mPager.getCurrentItem() > 0 ? 255 : 64);
                next.setEnabled(mPager.getCurrentItem() < NUM_PAGES - 1 ? true : false);
                next.getIcon().setAlpha(mPager.getCurrentItem() < NUM_PAGES - 1 ? 255 : 64);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (_UUID != null && !_UUID.equals("")) {
            complete.setVisibility(View.GONE);
        } else {
            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PandoraHelper.hideSoftKeyboard();
                    ArrayList<View> spinnerList = getAllChildren(mPager, Spinner.class);
                    ArrayList<View> etList = getAllChildren(mPager, EditText.class);
                    ArrayList<MSurvey> answerList = new ArrayList<>();

                    for (int i = 0; i < etList.size(); i++) {
                        Spinner spinner = (Spinner) spinnerList.get(i);
                        EditText et = (EditText) etList.get(i);
                        String uuid = spinner.getTag().toString();
                        if (spinner.getSelectedItemPosition() == 0) {
                            promptAlert(spinner, uuid);
                            return;
                        }
                        if (et.getText().toString().equals("")) {
                            promptAlert(et, uuid);
                            return;
                        }
                        MSurvey answer = new MSurvey();
                        answer.setC_SurveyTemplateQuestion_UUID(uuid);
                        answer.setAmt(((SpinnerPair) spinner.getSelectedItem()).getKey());
                        answer.setRemarks(et.getText().toString());
                        answerList.add(answer);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("answers", answerList);
                    Fragment fragment = new NewSurveySignFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragment.setArguments(bundle);
                    fragment.setRetainInstance(true);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    fragmentTransaction.commit();
                    PandoraMain.instance.getSupportActionBar().setTitle(getString(R.string.title_sign_survey));
                }
            });
        }
    }

    @Override
    protected void setValues() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        prev = menu.add(0, PREV_ID, 0, "Previous");
        prev.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        prev.setIcon(R.drawable.navigate_before);

        next = menu.add(0, NEXT_ID, 1, "Next");
        next.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        next.setIcon(R.drawable.navigate_next);

        prev.setEnabled(mPager.getCurrentItem() > 0 ? true : false);
        prev.getIcon().setAlpha(mPager.getCurrentItem() > 0 ? 255 : 64);
        next.setEnabled(mPager.getCurrentItem() < NUM_PAGES - 1 ? true : false);
        next.getIcon().setAlpha(mPager.getCurrentItem() < NUM_PAGES - 1 ? 255 : 64);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case PREV_ID: {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
                break;
            }
            case NEXT_ID: {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                break;
            }
            default: break;
        }
        prev.setEnabled(mPager.getCurrentItem() > 0 ? true : false);
        prev.getIcon().setAlpha(mPager.getCurrentItem() > 0 ? 255 : 64);
        next.setEnabled(mPager.getCurrentItem() < NUM_PAGES - 1 ? true : false);
        next.getIcon().setAlpha(mPager.getCurrentItem() < NUM_PAGES - 1 ? 255 : 64);
        return true;
    }

    private class setAdapterTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mPager.setAdapter(mPagerAdapter);
        }
    }

    private class processData extends AsyncTask<Bundle,Void,Bundle> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((PandoraMain)getActivity()).showProgressDialog("Loading...");
        }

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle input = new Bundle();
            Bundle output = new Bundle();
            if (_UUID != null && !_UUID.equals("")) {
                input.putString(PBSSurveyController.ARG_SURVEY_UUID, _UUID);
                output = surveyCont.triggerEvent(surveyCont.GET_SURVEY_EVENT, input, output, null);
            } else {
                input.putString(PBSSurveyController.ARG_TEMPLATE_UUID, PBSSurveyController.templateUUID);
                output = surveyCont.triggerEvent(surveyCont.GET_QUESTIONS_EVENT, input, output, null);
            }
            return output;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            super.onPostExecute(result);
            ((PandoraMain)getActivity()).dismissProgressDialog();
            survey = (MSurvey) result.getSerializable(PBSSurveyController.ARG_SURVEY);
            questions = (ArrayList<MSurvey>) result.getSerializable(PBSSurveyController.ARG_QUESTIONS);
            sections = (ArrayList<String>) result.getSerializable(PBSSurveyController.ARG_SECTIONS);
            if (sections.size() == 0) {
                PandoraHelper.showWarningMessage(getActivity(), "This template does not have any questions.");
                PandoraMain.instance.getSupportFragmentManager().popBackStack();
                return;
            }
            NUM_PAGES = sections.size();
            mPager.setOffscreenPageLimit(NUM_PAGES);

            if (_UUID == null || _UUID.equals(""))
                survey.setName(PBSSurveyController.name);
            PandoraMain.instance.getSupportActionBar().setTitle(survey.getName());
        }
    }

    private ArrayList<View> getAllChildren(View v, Class targetObj) {
        ArrayList<View> result = new ArrayList<>();
        ViewGroup viewGroup = (ViewGroup) v;

        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            if (targetObj.isInstance(child)) {
                result.add(child);
            } else if (child instanceof ViewGroup)
                result.addAll(getAllChildren(child, targetObj));
        }
        return result;
    }

    private void promptAlert(Object obj, String uuid) {
        PandoraHelper.showWarningMessage(getActivity(), "Please answer all the questions");
        if (obj instanceof Spinner) {
            mPager.setCurrentItem(getQuestionPageNo(uuid));
            ((Spinner) obj).requestFocus();
        }
        else if (obj instanceof EditText) {
            mPager.setCurrentItem(getQuestionPageNo(uuid));
            ((EditText) obj).requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(((EditText) obj), InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private int getQuestionPageNo(String question_uuid) {
        for (int j = 0; j < questions.size(); j++) {
            MSurvey question = questions.get(j);
            if (question.getC_SurveyTemplateQuestion_UUID().equals(question_uuid)) {
                for (int k = 0; k < sections.size(); k++) {
                    if (sections.get(k).equals(question.getSectionname())) {
                        return k;
                    }
                }
            }
        }
        return 0;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            Fragment f = new NewSurveyFragment();
            args.putInt("pos", position);
//            args.putSerializable(PBSSurveyController.ARG_SURVEY, survey);
            args.putSerializable(PBSSurveyController.ARG_SECTIONS, sections);
            args.putSerializable(PBSSurveyController.ARG_QUESTIONS, questions);
            f.setArguments(args);
            return f;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}