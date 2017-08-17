package com.pbasolutions.android.model;

/**
 * Created by pbadell on 7/21/15.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.R;

public class CheckInVH extends RecyclerView.ViewHolder {

    public ImageView getImageStatusView() {
        return imageStatusView;
    }

    public void setImageStatusView(ImageView imageView) {
        this.imageStatusView = imageView;
    }

    public TextView getTextLocationView() {
        return textLocationView;
    }

    public void setTextLocationView(TextView textView) {
        this.textLocationView = textView;
    }

    ImageView imageStatusView;
    TextView textLocationView;
    TextView textDateView;
    TextView textCommentView;
    TextView textCheckinView;

    public TextView getTextCheckinView() {
        return textCheckinView;
    }

    public void setTextCheckinView(TextView textCheckinView) {
        this.textCheckinView = textCheckinView;
    }

    public TextView getTextCommentView() {
        return textCommentView;
    }

    public void setTextCommentView(TextView textCommentView) {
        this.textCommentView = textCommentView;
    }

    public TextView getTextDateView() {
        return textDateView;
    }

    public void setTextDateView(TextView textDateView) {
        this.textDateView = textDateView;
    }

    public CheckInVH(View view, Context context) {
        super(view);
        PandoraMain mainContext = (PandoraMain)context;

        this.textLocationView = (TextView) view.findViewById(R.id.location);
        this.imageStatusView = (ImageView) view.findViewById(R.id.image);
        this.textDateView = (TextView) view.findViewById(R.id.date);
        this.textCommentView = (TextView) view.findViewById(R.id.comment);
        if (!mainContext.getGlobalVariable().isFullComment()){
            this.textCommentView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        }
        this.textCheckinView = (TextView) view.findViewById(R.id.checkinuuid);
    }
}