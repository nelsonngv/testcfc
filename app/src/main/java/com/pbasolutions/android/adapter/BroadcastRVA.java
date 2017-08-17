package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.databinding.NoteListitemBinding;
import com.pbasolutions.android.fragment.BroadcastDetailsFragment;
import com.pbasolutions.android.fragment.PBSDetailsFragment;
import com.pbasolutions.android.listener.FragmentListOnItemClickListener;
import com.pbasolutions.android.model.IModel;
import com.pbasolutions.android.model.MNote;

import java.util.List;

/**
 * Created by pbadell on 10/8/15.
 */
public class BroadcastRVA extends RecyclerView.Adapter<BroadcastRVA.BroadcastVH>{

    private Context mContext;
    private ObservableArrayList<MNote> notes;
    private LayoutInflater inflater;
    private List<String> checkedNoteIDs;

    public BroadcastRVA(Context mContext, ObservableArrayList<MNote> notes, LayoutInflater inflater) {
        this.mContext = mContext;
        this.notes = notes;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public BroadcastRVA.BroadcastVH onCreateViewHolder(ViewGroup parent, int viewType) {
        NoteListitemBinding binding = NoteListitemBinding.inflate(inflater);
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.note_listitem, null);

        // create a new view
        return new BroadcastVH(binding, view, new IViewHolderOnClicks(){
            @Override
            public void onCheckbox(CheckBox cb, int pos) {
                MNote note = (MNote) cb.getTag();
                note.setIsSelected(cb.isChecked());
                notes.get(pos).setIsSelected(cb.isChecked());
            }

            @Override
            public void onText(TextView tvCaller, int pos) {
            //    TextView _uuid = (TextView) view.findViewById(R.id._UUID);
                ObservableArrayList<IModel> modelList = (ObservableArrayList) notes;
                Fragment fragment = new BroadcastDetailsFragment();
                Object tag = tvCaller.getTag();
                if (tvCaller!=null) {
                    String _UUID = tvCaller.getTag().toString();
                     if (_UUID != null && !_UUID.isEmpty()) {
                         ((PBSDetailsFragment) fragment).set_UUID(_UUID);
                         ((PBSDetailsFragment) fragment).setModelList(modelList);
                         if (fragment != null) {
                             FragmentManager fragmentManager = ((PandoraMain)mContext).getSupportFragmentManager();
                             FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                             fragment.setRetainInstance(true);
                             fragmentTransaction.replace(R.id.container_body, fragment);
                             fragmentTransaction.addToBackStack(fragment.getClass().getName());
                             fragmentTransaction.commit();
                             ((PandoraMain) mContext).getSupportActionBar().setTitle(mContext.getString(R.string.title_broadcastdetails));
                         }
                     }
                }
                new FragmentListOnItemClickListener(modelList, new BroadcastDetailsFragment(),
                        (FragmentActivity)mContext, mContext.getString(R.string.title_broadcastdetails));
            }
        });
    }

    @Override
    public void onBindViewHolder(BroadcastVH holder, final int position) {
        final  MNote note = notes.get(position);
        holder.cbox.setChecked(note.isSelected());
        holder.cbox.setTag(note);
        holder.msgs.setTag(note.get_UUID());
        holder.vBinding.setNote(note);
    }

    public ObservableArrayList<MNote> getNotes() {
        return notes;
    }
    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (notes != null)
            return notes.size();
        else return 0;
    }

    public class BroadcastVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        NoteListitemBinding vBinding;
        TextView uuid;
        CheckBox cbox;
        TextView msgs;
        private IViewHolderOnClicks listeners;

        public BroadcastVH(NoteListitemBinding binding, View view, IViewHolderOnClicks listeners ) {
            super(binding.getRoot());
            this.vBinding = binding;
            this.listeners = listeners;
            View bindView = binding.getRoot();
            this.uuid = (TextView) bindView.findViewById(R.id._UUID);
            this.msgs = (TextView) bindView.findViewById(R.id.textViewNote);
            this.cbox = (CheckBox) bindView.findViewById(R.id.deleteNote);
            msgs.setOnClickListener(this);
            cbox.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if (v instanceof CheckBox){
                listeners.onCheckbox((CheckBox)v, getPosition());
            } else {
                listeners.onText((TextView)v, getPosition());
            }
        }

    }

    /**
     *
     */
    public  interface IViewHolderOnClicks{
         void onCheckbox(CheckBox cbCaller, int pos);
         void onText(TextView tvCaller, int pos);
    }

}
