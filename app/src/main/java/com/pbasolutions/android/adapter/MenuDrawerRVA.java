package com.pbasolutions.android.adapter;

/**
 * Originally by Ravi Tamada on 12-03-2015.
 * Created by pbadell on 7/15/15.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pbasolutions.android.model.NavDrawerItem;
import com.pbasolutions.android.R;
import java.util.Collections;
import java.util.List;

public class MenuDrawerRVA extends RecyclerView.Adapter<MenuDrawerRVA.MyViewHolder> {
    /**
     * List of data.
     */
    List<NavDrawerItem> data = Collections.emptyList();
    /**
     * Layout Inflater.
     */
    private LayoutInflater inflater;

    /**
     * Constructor
     * @param context
     * @param data
     */
    public MenuDrawerRVA(Context context, List<NavDrawerItem> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    /**
     * Delete from drawer list.
     * @param position
     */
    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}