package com.pbasolutions.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pbasolutions.android.R;
import com.pbasolutions.android.controller.PBSBroadcastController;
import com.pbasolutions.android.databinding.NoteDetailsBinding;
import com.pbasolutions.android.model.MNote;

/**
 * Created by pbadell on 10/8/15.
 */
public class BroadcastDetailsFragment extends PBSDetailsFragment {
    /**
     * Controller.
     */
    private PBSBroadcastController broadCont ;
    /**
     * Message details binding. **auto generated class based on the xml name.
     */
    private NoteDetailsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        broadCont = new PBSBroadcastController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_details, container, false);
        binding =  NoteDetailsBinding.bind(rootView);
        binding.setNote(getNote());
        return rootView;
    }

    public MNote getNote() {
        Bundle inputBundle = new Bundle();
        inputBundle.putString(broadCont.ARG_NOTE_UUID, _UUID);
        inputBundle.putSerializable(broadCont.NOTE_LIST, modelList);
        Bundle resultBundle = new Bundle();
        resultBundle = broadCont.triggerEvent(broadCont.GET_NOTE_EVENT, inputBundle, resultBundle, null);
        return (MNote)resultBundle.getSerializable(broadCont.ARG_NOTE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        MenuItem delete;
        delete = menu.add(0, BroadcastFragment.DELETE_NOTE_ID, 1, "Delete Note");
        delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        delete.setIcon(R.drawable.x);
    }

    @Override
    protected void setUI(View view) {

    }

    @Override
    protected void setUIListener() {

    }

    @Override
    protected void setValues() {

    }
}
