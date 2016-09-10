package pl.assolution.rocks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Andrzej on 2016-08-31.
 */
public class RecyclerViewOnItemClickListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener listener;
    private GestureDetector gestureDetector;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public RecyclerViewOnItemClickListener(Context context, final RecyclerView rv , final OnItemClickListener listener ) {
        this.listener = listener;
        this.gestureDetector = new GestureDetector(context ,new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View view = rv.findChildViewUnder(e.getX(), e.getY());

                if (view != null && listener != null) {
                    listener.onItemLongClick(view, rv.getChildAdapterPosition(view));
                }
                super.onLongPress(e);
            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        Boolean isClick = false;
        View view = rv.findChildViewUnder(e.getX(), e.getY());
        if(view != null && listener != null && gestureDetector.onTouchEvent(e)) {
            listener.onItemClick(view, rv.getChildAdapterPosition(view));
            isClick = true;
        }
        return isClick;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }


}
