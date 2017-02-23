package com.pbasolutions.android.fragment;

import android.app.Activity;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.MovementLineRVA;
import com.pbasolutions.android.adapter.SpinnerPair;
import com.pbasolutions.android.controller.PBSAssetController;
import com.pbasolutions.android.databinding.AssetNewMovementBinding;
import com.pbasolutions.android.listener.SpinnerOnItemSelected;
import com.pbasolutions.android.model.MMovement;
import com.pbasolutions.android.model.MMovementLine;

import java.util.List;

/**
 * Created by pbadell on 12/3/15.
 */
public abstract class AbstractMovementFragment extends PBSDetailsFragment{

    private static final String EVENT_DATE = "EVENT_DATE";

    protected PandoraMain context;
    protected PandoraContext appContext;

    protected TextView documentNo;
    protected TextView projectLocation;
    protected Spinner toProjectLocation;
    protected SpinnerOnItemSelected toProjectLocationItem;
    protected ArrayAdapter toProjectLocationAdapter;
    protected TextView movementDate;
    protected CheckBox lineCheckBox;
    boolean isToComplete = false;


    protected Button save;

    protected String _UUID;

    RecyclerView recyclerView;
    protected ObservableArrayList<MMovementLine> lines;


    protected PBSAssetController assetCont;
    protected MMovement movement;
    MovementLineRVA viewAdapter;

    protected static final int ADD_LINE_ID = 1;
    protected static final int REMOVE_LINE_ID = 2;
    protected static final int MOVE_LINE_ID = 3;
    protected static final int COMPLETE_MOVE_ID = 4;
    protected MenuItem move;
    protected MenuItem removeLine;
    protected MenuItem addLine;


    /**
     * Message details binding. **auto generated class based on the xml name.
     */
    private AssetNewMovementBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetCont = new PBSAssetController(getActivity());
        context = (PandoraMain)getActivity();
        appContext = context.getGlobalVariable();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.asset_new_movement,
                container, false);
        setUI(rootView);
        setUIListener();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.movementline_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new AsyncTask<Void, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected Bundle doInBackground(Void... params) {
                Bundle input = new Bundle();
                populateMovement();
                populateLines();

                return input;
            }

            @Override
            protected void onPostExecute(Bundle result) {
                super.onPostExecute(result);
                setValues();
                viewAdapter = new MovementLineRVA(getActivity(), lines, LayoutInflater.from(getActivity()));
                recyclerView.setAdapter(viewAdapter);
                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute();

        return rootView;
    }

    /**
     *
     * @param rootView
     */
    protected void setUI(View rootView) {
        Activity act = getActivity();
        toProjectLocation = (Spinner) rootView
                .findViewById(R.id.toProjectLocation);
        projectLocation = (TextView) rootView
                .findViewById(R.id.projectLocation);
        toProjectLocationAdapter = PandoraHelper
                .addListToSpinner(act, toProjectLocation,
                        getProjectLocationsTo());
        movementDate = (TextView) rootView
                .findViewById(R.id.movementDate);
        documentNo = (TextView) rootView.findViewById(R.id.documentNo);
        lineCheckBox = (CheckBox)rootView.findViewById(R.id.removeLine);
    }

    protected abstract List<SpinnerPair> getProjectLocationsTo();


    /**
     *
     */
    protected void setUIListener() {
        setOnItemSelectedListener();
        setOnClickListener(movementDate, EVENT_DATE);
    }

    /**
     *
     */
    private void setOnItemSelectedListener() {
        toProjectLocationItem = new SpinnerOnItemSelected(toProjectLocation,
                new SpinnerPair());
        toProjectLocation.setOnItemSelectedListener(toProjectLocationItem);
    }

    /**
     *
     * @param object
     * @param event
     */
    private void setOnClickListener(final View object, final String event) {
        object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (event) {
                    case EVENT_DATE: {
                        PandoraHelper.promptDatePicker((TextView) object, getActivity());
                        break;
                    }
                    default:
                        break;
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case ADD_LINE_ID: {
                saveMovement();
                addLine();
                return  true;
            }

            case REMOVE_LINE_ID: {
                removeLine();
                return  true;
            }

            case MOVE_LINE_ID: {
                saveMovement();
                move();
                return  true;
            }
            case COMPLETE_MOVE_ID:{
                move();
            }
            default:return false;
        }
    }

    /**
     *
     */
    protected abstract void move();

    /**
     *
     */
    protected abstract void saveMovement();

    protected abstract void addLine();

    protected abstract void removeLine();

    protected abstract void populateLines();

    protected abstract void populateMovement();

    protected abstract void setValues();

    public String get_UUID() {
        return _UUID;
    }

    @Override
    public void set_UUID(String _UUID) {
        this._UUID = _UUID;
    }
}
