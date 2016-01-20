package com.pbasolutions.android.adapter;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pbasolutions.android.databinding.TaskListItemBinding;
import com.pbasolutions.android.model.MProjectTask;

/**
 * Created by pbadell on 10/13/15.
 */
public class TaskRVA extends RecyclerView.Adapter<TaskRVA.TaskVH> {
    private Context mContext;
    private ObservableArrayList<MProjectTask> taskList;
    private LayoutInflater inflater;

    public TaskRVA(Context mContext, ObservableArrayList<MProjectTask> taskList, LayoutInflater inflater) {
        this.mContext = mContext;
        this.taskList = taskList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public TaskRVA.TaskVH onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskListItemBinding binding = TaskListItemBinding.inflate(inflater);
        TaskVH viewHolder = new TaskVH(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TaskVH holder, int position) {
        MProjectTask task = taskList.get(position);
        holder.vBinding.setProjTask(task);
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
        }
    }
}
