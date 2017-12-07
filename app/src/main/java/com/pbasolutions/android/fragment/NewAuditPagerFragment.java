package com.pbasolutions.android.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.control.CustomViewPager;
import com.pbasolutions.android.controller.PBSSurveyController;
import com.pbasolutions.android.listener.PBABackKeyListener;
import com.pbasolutions.android.model.MSurvey;

import java.util.ArrayList;

public class NewAuditPagerFragment extends PBSDetailsFragment implements PBABackKeyListener {
    /**
     * Class tag name.
     */
    private static final String TAG = "NewAuditPagerFragment";
    private static final int PREV_ID = 1;
    private static final int NEXT_ID = 2;
    private static final int COOMPLETE_ID = 3;
    private static int NUM_PAGES = 1;
    private MenuItem prev;
    private MenuItem next;
    private MenuItem complete;
    private CustomViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private PBSSurveyController auditCont;
    public static MSurvey audit;
    private ArrayList<MSurvey> questions;
    private ArrayList<String> sections;
    private Toast toast;
    private int currPage = 0;
    private boolean triggeredByButton = false;
    public static EditText etAuditRemarks;

    /**
     * Constructor method.
     */
    public NewAuditPagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.viewpager_audit, container, false);
        auditCont = new PBSSurveyController(getActivity());

        new processData().execute();
        new setAdapterTask().execute();
        setUI(rootView);
        setUIListener();

        return rootView;
    }

    @Override
    protected void setUI(View view) {
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (CustomViewPager) view.findViewById(R.id.pager);
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
                updateMenuItem();
                if (_UUID == null || _UUID.equals("")) {
                    if (triggeredByButton) {
                        triggeredByButton = false;
                    } else {
                        PandoraHelper.hideSoftKeyboard(getActivity());
                        ArrayList<View> spinnerList = getAllChildren(mPager.getChildAt(currPage), Spinner.class);
                        for (int i = 0; i < spinnerList.size(); i++) {
                            Spinner spinner = (Spinner) spinnerList.get(i);
                            String uuid = spinner.getTag().toString();
                            if (spinner.getSelectedItemPosition() == 0) {
                                promptAlert(spinner, uuid);
                                mPager.setCurrentItem(currPage, true);
                                return;
                            }
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Please click OK to proceed. You will not be able to go back after proceed.")
                                .setTitle(PandoraConstant.WARNING)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        currPage++;
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        triggeredByButton = true;
                                        mPager.setCurrentItem(currPage, true);
                                    }
                                })
                                .setCancelable(false);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        if (_UUID == null || _UUID.equals(""))
            prev.setVisible(false);

        next = menu.add(0, NEXT_ID, 1, "Next");
        next.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        next.setIcon(R.drawable.navigate_next);

        complete = menu.add(0, COOMPLETE_ID, 2, "Complete");
        complete.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        complete.setIcon(R.drawable.ic_done);

        if (_UUID != null && !_UUID.equals("")) {
            complete.setVisible(false);
            complete.setEnabled(false);
        }
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
                if (_UUID != null && !_UUID.equals("")) {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                } else {
                    PandoraHelper.hideSoftKeyboard(getActivity());
                    ArrayList<View> spinnerList = getAllChildren(mPager.getChildAt(mPager.getCurrentItem()), Spinner.class);
                    for (int i = 0; i < spinnerList.size(); i++) {
                        Spinner spinner = (Spinner) spinnerList.get(i);
                        String uuid = spinner.getTag().toString();
                        if (spinner.getSelectedItemPosition() == 0) {
                            promptAlert(spinner, uuid);
                            return true;
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Please click OK to proceed. You will not be able to go back after proceed.")
                            .setTitle(PandoraConstant.WARNING)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    currPage++;
                                    triggeredByButton = true;
                                    mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;
            }
            case COOMPLETE_ID: {
                PandoraHelper.hideSoftKeyboard(getActivity());
                ArrayList<View> spinnerList = getAllChildren(mPager, Spinner.class);
                ArrayList<View> etList = getAllChildren(mPager, EditText.class);
                final ArrayList<MSurvey> answerList = new ArrayList<>();

                for (int i = 0; i < spinnerList.size(); i++) {
                    Spinner spinner = (Spinner) spinnerList.get(i);
                    EditText et = (EditText) etList.get(i);
                    String uuid = spinner.getTag().toString();
                    if (spinner.getSelectedItemPosition() == 0) {
                        promptAlert(spinner, uuid);
                        return true;
                    }
//                        if (et.getText().toString().equals("")) {
//                            promptAlert(et, uuid);
//                            return;
//                        }
                    MSurvey answer = new MSurvey();
                    answer.setC_SurveyTemplateQuestion_UUID(uuid);
                    answer.setAmt(((SpinnerPair) spinner.getSelectedItem()).getKey());
                    answer.setRemarks(et.getText().toString());
                    answerList.add(answer);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Please click OK to proceed. You will not be able to go back after proceed.")
                        .setTitle(PandoraConstant.WARNING)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                for (int i = 0; i < questions.size(); i++) {
                                    answerList.get(i).setSectionname(questions.get(i).getSectionname());
                                }

                                triggeredByButton = true;
                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList("answers", answerList);
                                bundle.putStringArrayList("sections", sections);
                                bundle.putString(MSurvey.DATETRX_COL, PBSSurveyController.dateTrx);
                                bundle.putString(MSurvey.REMARKS_COL, etAuditRemarks.getText().toString());
                                Fragment fragment = new NewAuditSignFragment();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragment.setArguments(bundle);
                                fragment.setRetainInstance(true);
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.container_body, fragment);
                                fragmentTransaction.addToBackStack(fragment.getClass().getName());
                                fragmentTransaction.commit();
                                ((PandoraMain)getActivity()).getSupportActionBar().setTitle(audit.getName());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            default: break;
        }
        if (id == PREV_ID || id == NEXT_ID)
            updateMenuItem();
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
                output = auditCont.triggerEvent(PBSSurveyController.GET_SURVEY_EVENT, input, output, null);
            } else {
                input.putString(PBSSurveyController.ARG_TEMPLATE_UUID, PBSSurveyController.templateUUID);
                output = auditCont.triggerEvent(PBSSurveyController.GET_QUESTIONS_EVENT, input, output, null);
            }
            return output;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            super.onPostExecute(result);
            try {
                ((PandoraMain)getActivity()).dismissProgressDialog();
                audit = (MSurvey) result.getSerializable(PBSSurveyController.ARG_SURVEY);
                questions = (ArrayList<MSurvey>) result.getSerializable(PBSSurveyController.ARG_QUESTIONS);
                sections = (ArrayList<String>) result.getSerializable(PBSSurveyController.ARG_SECTIONS);
                if (sections == null || sections.size() == 0) {
                    PandoraHelper.showWarningMessage(getActivity(), "This template does not have any questions.");
                    getActivity().getSupportFragmentManager().popBackStack();
                    return;
                }
                NUM_PAGES = sections.size();
                if (_UUID != null && !_UUID.equals(""))
                    NUM_PAGES++;
                mPager.setOffscreenPageLimit(NUM_PAGES);
                updateMenuItem();

                if (_UUID == null || _UUID.equals("")) {
                    audit.setName(PBSSurveyController.name);
                    mPager.setAllowedSwipeDirection(CustomViewPager.SwipeDirection.none);
                }
                else mPager.setAllowedSwipeDirection(CustomViewPager.SwipeDirection.all);
                ((PandoraMain)getActivity()).getSupportActionBar().setTitle(audit.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        if (toast != null) toast.cancel();
        toast = Toast.makeText(getActivity(), "Please rate all the questions", Toast.LENGTH_SHORT);
        toast.show();
//        PandoraHelper.showWarningMessage(getActivity(), "Please rate all the questions");
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

    private void updateMenuItem() {
        prev.setEnabled(mPager.getCurrentItem() > 0);
        prev.getIcon().setAlpha(mPager.getCurrentItem() > 0 ? 255 : 64);
        next.setEnabled(mPager.getCurrentItem() < NUM_PAGES - 1);
        next.getIcon().setAlpha(mPager.getCurrentItem() < NUM_PAGES - 1 ? 255 : 64);
        complete.setEnabled(NUM_PAGES == mPager.getCurrentItem() + 1);
        complete.getIcon().setAlpha(NUM_PAGES == mPager.getCurrentItem() + 1 ? 255 : 64);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            Fragment f = null;
            if (_UUID != null && !_UUID.equals("") && position == 0) {
                f = new NewAuditSummaryFragment();
                args.putBoolean("isViewing", true);
                args.putParcelableArrayList("answers", questions);
                args.putStringArrayList("sections", sections);
            } else {
                f = new NewAuditFragment();
                if (_UUID != null && !_UUID.equals(""))
                    args.putInt("pos", position - 1);
                else args.putInt("pos", position);
//                args.putSerializable(PBSSurveyController.ARG_SURVEY, audit);
                args.putSerializable(PBSSurveyController.ARG_SECTIONS, sections);
                args.putSerializable(PBSSurveyController.ARG_QUESTIONS, questions);
            }
            f.setArguments(args);
            return f;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public boolean onBackKeyPressed() {
        if (_UUID == null || _UUID.equals(""))
            return false;
        else return true;
    }
}