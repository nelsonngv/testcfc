package com.pbasolutions.android.fragment;

import android.content.DialogInterface;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pbasolutions.android.PBSServerConst;
import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraContext;
import com.pbasolutions.android.PandoraHelper;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.adapter.BroadcastRVA;
import com.pbasolutions.android.controller.PBSBroadcastController;
import com.pbasolutions.android.listener.FragmentListOnItemClickListener;
import com.pbasolutions.android.listener.RecyclerItemClickListener;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MNote;

/**
 * Created by pbadell on 10/8/15.
 */
public class BroadcastFragment extends Fragment {
    /**
     * Class tag name.
     */
    private static final String TAG = "BroadcastFragment";
    /**
     * Controller.
     */
    private PBSBroadcastController broadCont;
    /**
     * Context.
     */
    private PandoraContext context;
    /**
     * Recycler View adapter.
     */
    private BroadcastRVA viewAdapter;
    /**
     * Broadcast list.
     */
    private ObservableArrayList<MNote> broadCastList;

    /**
     * Toolbar menu id - sync.
     */
    public static final int SYNC_NOTE_ID = 400;
    /**
     * Toolbar menu id - delete.
     */
    public static final int DELETE_NOTE_ID = 500;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;

    /**
     * Constructor.
     */
    public BroadcastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        broadCont = new PBSBroadcastController(getActivity());
        context = ((PandoraMain) getActivity()).getGlobalVariable();
        ((PandoraMain) getActivity()).fragment = this;
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.broadcast_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshBroadcast(false);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshBroadcast(true);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case DELETE_NOTE_ID: {
                deleteNotes();
                return true;
            }
            default:return false;
        }
    }

    /**
     * Delete selected notes in list.
     */
    private void deleteNotes() {
        PandoraMain context = (PandoraMain) getActivity();
        //check if any message selected
        ObservableArrayList<MNote> list = viewAdapter.getNotes();
        if (!PandoraHelper.isSelected((ObservableArrayList)list)){
            PandoraHelper.showErrorMessage(context, getString(R.string.no_selected, "message(s)"));
            return;
        }
//        Bundle isDelete = PandoraHelper.showAlertMessage(context, getString(R.string.sure_to_delete, "messages")
//                , PandoraConstant.WARNING, PandoraConstant.YES_BUTTON, PandoraConstant.CANCEL_BUTTON);
            promptDeleteMessage(getString(R.string.sure_to_delete, "message(s)"), PandoraConstant.WARNING,
                    PandoraConstant.YES_BUTTON, PandoraConstant.CANCEL_BUTTON, list);
//        if (isDelete.getBoolean(PandoraHelper.OK_BUTTON_PRESSED)) {
//            Bundle input = new Bundle();
//            String selection = MNote.AD_NOTE_UUID_COL + "=?";
//            input.putString(broadCont.ARG_SELECTION, selection);
//            input.putSerializable(broadCont.NOTE_LIST, list);
//            Bundle result = broadCont.triggerEvent(broadCont.DELETE_NOTES_EVENT, input,
//                    new Bundle(), null);
//            String title = result.getString(PandoraConstant.TITLE);
//            PandoraHelper.showAlertMessage(context,
//                    result.getString(title),
//                    title, PandoraConstant.OK_BUTTON, null);
//            if (!PandoraConstant.ERROR.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))) {
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.detach(this).attach(this).commit();
//            }
//        }
    }

    /**
     * SnycNotes is caller to backend process to get latest broadcast list.
     */
    private String[] syncNotes() {
        PandoraMain pContext = (PandoraMain) getActivity();
        String title;
        String text;

        if (PBSServerConst.cookieStore != null) {
            //get latest broadcast list
            Bundle input = new Bundle();
            input.putString(broadCont.ARG_USER_ID, context.getAd_user_id());
            input.putString(broadCont.ARG_PROJLOC_UUID, context.getC_projectlocation_uuid());
            input.putString(PBSServerConst.PARAM_URL, context.getServer_url());
            Bundle result = broadCont.triggerEvent(broadCont.SYNC_NOTES_EVENT, input,
                    new Bundle(), null);

            String resultTitle = result.getString(PandoraConstant.TITLE);

            if (resultTitle != null && !result.isEmpty()) {
                title = resultTitle;
                text = result.getString(resultTitle);
            } else {
                title = PandoraConstant.RESULT;
                text = "Broadcast is up to date";
            }
        } else {
            title = PandoraConstant.RESULT;
            text = getString(R.string.error_logged_out_sync, getString(R.string.broadcast_messages));
        }

        return new String[] { title, text};
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem delete;
        delete = menu.add(0, DELETE_NOTE_ID, 1, "Delete Note");
        delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        delete.setIcon(R.drawable.x);
    }

    /**
     * Add click listener on the recycler view. when ever user clicks the list item. it will navigate them to the item details.
     * @param rv
     */
    protected void addRecyclerViewListener(RecyclerView rv) {
        ObservableArrayList<IModel> modelList = (ObservableArrayList)broadCastList;
        rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new FragmentListOnItemClickListener(modelList, new BroadcastDetailsFragment(),
                        getActivity(), getString(R.string.title_broadcastdetails))));
    }

    /**
     * Get broadcast list from local db.
     * @return
     */
    public ObservableArrayList<MNote> getBroadCastList() {
        Bundle input = new Bundle();
        input.putString(broadCont.ARG_USER_UUID, context.getAd_user_uuid());
        Bundle result = broadCont.triggerEvent(broadCont.GET_NOTES_EVENT, input, new Bundle(), null);
        return (ObservableArrayList<MNote>)result.getSerializable(broadCont.NOTE_LIST);
    }

    private void promptDeleteMessage(String message, String title, String okButton,
                                     String cancelButton, final ObservableArrayList<MNote> list){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(title);
        if (okButton != null) {
            // Add the buttons
            builder.setPositiveButton(okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                 runDeleteNotes(list);
                }
            });
        }
        if (cancelButton != null) {
            builder.setNegativeButton(cancelButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                return;
                }
            });
        }
        // builder.but
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void runDeleteNotes( ObservableArrayList<MNote> list){
        Bundle input = new Bundle();
        String selection = MNote.AD_NOTE_UUID_COL + "=?";
        input.putString(broadCont.ARG_SELECTION, selection);
        input.putSerializable(broadCont.NOTE_LIST, list);
        Bundle result = broadCont.triggerEvent(broadCont.DELETE_NOTES_EVENT, input,
                new Bundle(), null);
        String title = result.getString(PandoraConstant.TITLE);
        PandoraHelper.showMessage(((PandoraMain)getActivity()),
                result.getString(title));
        if (!PandoraConstant.ERROR.equalsIgnoreCase(result.getString(PandoraConstant.TITLE))) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
    }

    protected void refreshBroadcast(final boolean showMsg) {
        new AsyncTask<Object, Void, String[]>() {
            protected LayoutInflater inflater;
            protected RecyclerView recyclerView;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((PandoraMain)getActivity()).showProgressDialog("Loading...");
            }

            @Override
            protected String[] doInBackground(Object... params) {
                inflater = (LayoutInflater) params[0];
                recyclerView = (RecyclerView) params[1];

                String[] message = null;
                if (context.getC_projectlocation_uuid() != null
                        && !context.getC_projectlocation_uuid().isEmpty()) {
                    message = syncNotes();
                } else {
                    PandoraHelper.getProjLocAvailable(getActivity(), false);
                }

                broadCastList = getBroadCastList();

                return message;
            }

            @Override
            protected void onPostExecute(String[] message) {
                super.onPostExecute(message);

                if (message != null && showMsg) {
                    PandoraHelper.showMessage((PandoraMain)getActivity(), message[1]);
                }

                viewAdapter = new BroadcastRVA(getActivity(),broadCastList, inflater);
                recyclerView.setAdapter(viewAdapter);

                ((PandoraMain)getActivity()).dismissProgressDialog();
            }
        }.execute(LayoutInflater.from(PandoraMain.instance), recyclerView);
    }
}
