package com.pbasolutions.android.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by pbadell on 7/24/15.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
   private FragmentListOnItemClickListener mListener;

    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, FragmentListOnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    /**
     * Silently observe and/or take over touch events sent to the RecyclerView
     * before they are handled by either the RecyclerView itself or its child views.
     * <p/>
     * <p>The onInterceptTouchEvent methods of each attached OnItemTouchListener will be run
     * in the order in which each listener was added, before any other touch processing
     * by the RecyclerView itself or child views occurs.</p>
     *
     * @param rv
     * @param e  MotionEvent describing the touch event. All coordinates are in
     *           the RecyclerView's coordinate system.
     * @return true if this OnItemTouchListener wishes to begin intercepting touch events, false
     * to continue with the current behavior and continue observing future events in
     * the gesture.
     */
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, rv.getChildPosition(childView));
            return true;
        }
        return false;
    }

    /**
     * Process a touch event as part of a gesture that was claimed by returning true from
     * a previous call to {@link #onInterceptTouchEvent}.
     *
     * @param rv
     * @param e  MotionEvent describing the touch event. All coordinates are in
     */
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }
}
