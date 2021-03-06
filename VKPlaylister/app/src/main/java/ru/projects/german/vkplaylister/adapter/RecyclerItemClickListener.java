package ru.projects.german.vkplaylister.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by root on 15.10.15.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    public static abstract class OnItemClickListener {
        public boolean onItemClick(View view, int position) {return true;}
        public boolean onItemLongPress(View view, int position) {return true;}
    }

    private GestureDetector gestureDetector;
    private OnItemClickListener listener;

    private View lastChildView;
    private int lastPosition;

    private SparseArray<OnItemClickListener> childListeners;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        this.listener = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return RecyclerItemClickListener.this.listener.onItemClick(lastChildView, lastPosition);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                RecyclerItemClickListener.this.listener.onItemLongPress(lastChildView, lastPosition);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        lastChildView = rv.findChildViewUnder(e.getX(), e.getY());
        if (lastChildView != null) {
            lastPosition = rv.getChildAdapterPosition(lastChildView);
        }
        if (lastChildView != null && listener != null) {
            gestureDetector.onTouchEvent(e);
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
