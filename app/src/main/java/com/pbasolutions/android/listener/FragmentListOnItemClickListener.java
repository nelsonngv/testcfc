package com.pbasolutions.android.listener;

import android.databinding.ObservableArrayList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;
import com.pbasolutions.android.fragment.PBSDetailsFragment;
import com.pbasolutions.android.model.IModel;

/**
 * This class intent to handle on event where user clicked on an item in fragment/recycler view list.
 * Created by pbadell on 10/30/15.
 */
public class FragmentListOnItemClickListener implements View.OnClickListener {

    private ObservableArrayList<IModel> modelList;
    private PBSDetailsFragment targetFragment;
    private FragmentActivity fragmentActivity;
    private String title;

    public void onItemClick(View view, int position){
        TextView _uuid = (TextView) view.findViewById(R.id._UUID);
        TextView _id = (TextView) view.findViewById(R.id._ID);
        Fragment fragment = targetFragment;
        if (_uuid != null){
            ((PBSDetailsFragment) fragment).set_UUID((String) _uuid.getText());
            if (_id != null) {
                ((PBSDetailsFragment) fragment).set_ID(Integer.parseInt(_id.getText().toString()));
            }
            ((PBSDetailsFragment) fragment).setModelList(modelList);
            if (fragment != null) {
                FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragment.setRetainInstance(true);
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
                fragmentTransaction.commit();
                ((PandoraMain) fragmentActivity).getSupportActionBar().setTitle(title);
            }
        }
    }

    public FragmentListOnItemClickListener(ObservableArrayList<IModel> modelList, PBSDetailsFragment targetFragment, FragmentActivity fragmentActivity, String title) {
        this.modelList = modelList;
        this.targetFragment = targetFragment;
        this.fragmentActivity = fragmentActivity;
        this.title = title;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        String _UUID = "";
        int _ID = 0;
        TextView _uuid = (TextView) v.findViewById(R.id._UUID);
        TextView _id = (TextView) v.findViewById(R.id._ID);
        if (_uuid == null) {
            //try to get view tag.
            Object tag = v.getTag();
            if (tag != null) {
                _UUID = tag.toString();
            }
        } else {
            _UUID = (String) _uuid.getText();
        }

        if (_id != null) {
            _ID = Integer.parseInt(_id.getText().toString());
        }
        Fragment fragment = targetFragment;
        if (_UUID != null && !_UUID.isEmpty()) {
            ((PBSDetailsFragment) fragment).set_UUID(_UUID);
            ((PBSDetailsFragment) fragment).setModelList(modelList);
            ((PBSDetailsFragment) fragment).set_ID(_ID);
            if (fragment != null) {
                FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragment.setRetainInstance(true);
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
                fragmentTransaction.commit();
                ((PandoraMain) fragmentActivity).getSupportActionBar().setTitle(title);
            }
        }
    }
}
