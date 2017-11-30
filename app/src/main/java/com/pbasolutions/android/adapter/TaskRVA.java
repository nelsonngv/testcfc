package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pbasolutions.android.databinding.TaskListItemBinding;
import com.pbasolutions.android.model.MProjectTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by pbadell on 10/13/15.
 */
public class TaskRVA extends RecyclerView.Adapter<TaskRVA.TaskVH> {
    private Context mContext;
    private ObservableArrayList<MProjectTask> taskList;
    private LayoutInflater inflater;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdf2 = new SimpleDateFormat("EE, dd-MM-yyyy");

    public TaskRVA(Context mContext, ObservableArrayList<MProjectTask> taskList) {
        this.mContext = mContext;
        this.taskList = taskList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public TaskRVA.TaskVH onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskListItemBinding binding = TaskListItemBinding.inflate(inflater);
        return new TaskVH(binding);
    }

    @Override
    public void onBindViewHolder(TaskVH holder, int position) {
        MProjectTask task = taskList.get(position);
        try {
            task.setDateAssigned(sdf2.format(sdf.parse(task.getDateAssigned())));
            task.setDueDate(sdf2.format(sdf.parse(task.getDueDate())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.vBinding.setProjTask(task);
        if (taskList.size() == 1)
            holder.itemView.setPadding(0, 10, 0, 10);
        else {
            if (position == 0) holder.itemView.setPadding(0, 10, 0, 0);
            if (position == taskList.size() - 1) holder.itemView.setPadding(0, 0, 0, 10);
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (taskList != null)
            return taskList.size();
        else return 0;
    }

    public class TaskVH extends RecyclerView.ViewHolder {
        TaskListItemBinding vBinding;
        public TaskVH(TaskListItemBinding binding) {
            super(binding.getRoot());
            this.vBinding = binding;
            itemView.setClickable(true);
        }
    }
}
