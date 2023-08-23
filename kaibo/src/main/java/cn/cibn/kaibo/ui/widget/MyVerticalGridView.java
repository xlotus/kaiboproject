package cn.cibn.kaibo.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;

import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;

import com.tv.lib.core.Logger;

import java.util.ArrayList;

/**拦截过快移动 支持边界向右向下找寻焦点*/
public class MyVerticalGridView extends VerticalGridView {
    private static final String TAG = "MyVerticalGridView";
    private long cacheTime;
    private int lastFocusPos = -1;
    private int numColumns = 1;

    //最后一次聚焦的位置
    private int mLastFocusPosition = 0;
    private View mLastFocusView = null;

    private OnChildViewHolderSelectedListener myListener = new OnChildViewHolderSelectedListener() {
        @Override
        public void onChildViewHolderSelected(RecyclerView parent, ViewHolder child, int position, int subposition) {
            super.onChildViewHolderSelected(parent, child, position, subposition);
            lastFocusPos = position;
            Log.d(TAG, "onChildViewHolderSelected: " + lastFocusPos);
        }

        @Override
        public void onChildViewHolderSelectedAndPositioned(RecyclerView parent, ViewHolder child, int position, int subposition) {
            super.onChildViewHolderSelectedAndPositioned(parent, child, position, subposition);
        }
    };

    @Override
    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        super.setNumColumns(numColumns);
    }

    public MyVerticalGridView(Context context) {
        super(context);
        initView(context);
    }


    public MyVerticalGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyVerticalGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        addOnChildViewHolderSelectedListener(myListener);
    }

    @Override
    public View focusSearch(View focused, int direction) {
//        View nextFocusView = FocusFinder.getInstance().findNextFocus(this, focused, direction);
//        if (nextFocusView == null && lastFocusPos != -1 && lastFocusPos < getAdapter().getItemCount() - 1) {
//            switch (direction) {
//                case View.FOCUS_DOWN:
//                case View.FOCUS_RIGHT:
//                    int nextPos = (getChildCount() / numColumns) * numColumns;
//                    Log.d(TAG, "focusSearch: getChildCount" + getChildCount() + "nextPos" + nextPos);
//                    return getChildAt(nextPos);
//            }
//        }
//        return super.focusSearch(focused, direction);
        if (focused == null) {
            return getChildAt(lastFocusPos);
        }
        int p = getChildAdapterPosition(focused);
        switch (direction) {
            case View.FOCUS_RIGHT:
                if (p < getAdapter().getItemCount() - 1) {
                    return getChildAt(p + 1);
                }
                break;
            case View.FOCUS_LEFT:
                if (p > 0) {
                    return getChildAt(p - 1);
                }
                break;
        }
        return super.focusSearch(focused, direction);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        boolean canScrollHorizontally = super.canScrollHorizontally(direction);
        Log.d(TAG, "canScrollHorizontally: " + canScrollHorizontally);
        return canScrollHorizontally;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        boolean canScrollVertically = super.canScrollVertically(direction);
        Log.d(TAG, "canScrollHorizontally: " + canScrollVertically);
        return canScrollVertically;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (action != KeyEvent.ACTION_DOWN) {
                    cacheTime = 0;
                    break;
                }
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - cacheTime >= 300) {
                    cacheTime = currentTimeMillis;
                    break;
                }
                return true;
        }

        return super.dispatchKeyEvent(event);
    }

    /**
     * 通过ViewParent#requestChildFocus通知父控件即将获取焦点
     *
     * @param child   下一个要获得焦点的recyclerview item
     * @param focused 当前聚焦的view
     */
    @Override
    public void requestChildFocus(View child, View focused) {
        if (null != child) {
            Logger.i(TAG, "nextchild = " + child + ",focused = " + focused);
            if (!hasFocus()) {
                //recyclerview 子view 重新获取焦点，调用移入焦点的事件监听
//                if (mFocusGainListener != null) {
//                    mFocusGainListener.onFocusGain(child, focused);
//                }
            }

            //执行过super.requestChildFocus之后hasFocus会变成true
            super.requestChildFocus(child, focused);
            //取得获得焦点的item的position
            mLastFocusView = focused;
            mLastFocusPosition = getChildViewHolder(child).getAdapterPosition();
            Logger.i(TAG, "focusPos = " + mLastFocusPosition);

            //计算控制recyclerview 选中item的居中从参数
//            if (mSelectedItemCentered) {
//                mSelectedItemOffsetStart = !isVertical() ? (getFreeWidth() - child.getWidth()) : (getFreeHeight() - child.getHeight());
//                mSelectedItemOffsetStart /= 2;
//                mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
//            }
        }
//        Logger.i(TAG, "mSelectedItemOffsetStart = " + mSelectedItemOffsetStart);
//        Logger.i(TAG, "mSelectedItemOffsetEnd = " + mSelectedItemOffsetEnd);
    }

    /**
     * 实现焦点记忆的关键代码
     * <p>
     * root.addFocusables会遍历root的所有子view和孙view,然后调用addFocusable把isFocusable的view添加到focusables
     */
    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        Logger.i(TAG, "views = " + views);
        Logger.i(TAG, "lastFocusView = " + mLastFocusView + " mLastFocusPosition = " + mLastFocusPosition);
        if (this.hasFocus() || mLastFocusView == null) {
            //在recyclerview内部焦点切换
            super.addFocusables(views, direction, focusableMode);
        } else {
            //将当前的view放到Focusable views列表中，再次移入焦点时会取到该view,实现焦点记忆功能
            views.add(getLayoutManager().findViewByPosition(mLastFocusPosition));
            Logger.i(TAG, "views.add(lastFocusView)");
        }
    }
}


