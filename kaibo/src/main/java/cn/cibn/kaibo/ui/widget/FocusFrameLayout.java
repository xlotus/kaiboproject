package cn.cibn.kaibo.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tv.lib.core.Logger;

import cn.cibn.kaibo.R;


/**
 * 监控盒子获得（孩子获得焦点）或失去焦点(所有孩子失去焦点)
 */
public class FocusFrameLayout extends FrameLayout {
    private static final String TAG = "FocusBorderView";
    private Context context;
    private ImageView focusBorderImg;
    //borderview动画
    private FocusValueAnimator animator;

    private View customRootView;

    //borderview样式
    private Drawable borderDrawableRes;
    //指定默认聚焦的id
    private int specifiedViewId;
    //borderview宽度
    private float borderHeight;
    //borderview高度
    private float borderWidth;

    private OnFocusChangeListener onFocusChangeListener;

    public FocusFrameLayout(Context context) {
        this(context, null);
    }

    public FocusFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView(context, attrs, defStyle);
    }

    public void initView(Context context, AttributeSet attrs, int defStyle) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FocusBorderView, defStyle, 0);
        borderDrawableRes = a.getDrawable(R.styleable.FocusBorderView_borderview_drawable_res);
        specifiedViewId = a.getResourceId(R.styleable.FocusBorderView_specified_focus_id, 0);
        borderHeight = a.getDimensionPixelOffset(R.styleable.FocusBorderView_borderview_height, 0);
        borderWidth = a.getDimensionPixelOffset(R.styleable.FocusBorderView_borderview_width, 0);
        setFocusable(true);
        addFocusBorder();
        animator = new FocusValueAnimator(this);
        getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                if (isFocused()) {
                    //找到用户指定focusBorderview内默认聚焦的view
                    focusSpecifiedView(specifiedViewId);
                }
                //判断是否自身被聚焦或者存在子view被聚焦
                if (hasFocus()) {
                    focusEnter();
                } else {
                    focusLeave();
                }
            }
        });

    }


    /**
     * 布局完之后，设置用户自定义的xml中的viewGroup与borderview的间距
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int childCount = getChildCount();
        if (childCount == 2) {
            //firstview is the borderview
            //second view is the first viewGroup that was found in the customer xml
            customRootView = getChildAt(1);
            if (customRootView instanceof ViewGroup) {
            } else {
                throw new RuntimeException("The FocusBorderView must container one and the only one ViewGroup");
            }
        } else {
            throw new RuntimeException("The FocusBorderView must container one and the only one ViewGroup");
        }
    }


    /**
     * 添加borderview
     **/
    private void addFocusBorder() {
        focusBorderImg = new ImageView(context);
        LayoutParams lp = new LayoutParams((int) borderWidth, (int) borderHeight);
        lp.gravity = Gravity.CENTER;
        focusBorderImg.setLayoutParams(lp);
        focusBorderImg.setBackgroundDrawable(borderDrawableRes);
        this.addView(focusBorderImg);
        focusBorderImg.setVisibility(INVISIBLE);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        View nextFocus = FocusFinder.getInstance().findNextFocus(this, focused, direction);
        //根据原生焦点流程默认找到的view，如果该方向上有可聚焦的view则找到该view
        //如果没有则会调用viewgroup的focusSearch方法，找到最近的另一个view或viewgroup
        View realNextFocus = super.focusSearch(focused, direction);
        if (nextFocus == null) {
            //当前focusBorderView内，在direction方向上已经没有可聚焦的view，说明焦点将进入另一个viewgroup
            if (realNextFocus != null) {
                focusLeave();
            } else {
                realNextFocus = focused;
            }
        }
        return realNextFocus;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animator.cancel();
    }


    /**
     * 聚焦到指定的view
     */
    private void focusSpecifiedView(int viewId) {
        View specifedView = null;
        if (customRootView != null) {
            specifedView = customRootView.findViewById(viewId);
            if (specifedView != null) {
                specifedView.requestFocus();
                Logger.i(TAG, "special view focus" + specifedView.toString());
            }
        }
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        focusEnter();
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    /**
     * 自身或者子view存在焦点
     */
    private void focusEnter() {
        Logger.i(TAG, "focusEnter");
        if (focusBorderImg != null && focusBorderImg.getVisibility() == INVISIBLE) {
            focusBorderImg.setVisibility(VISIBLE);
            animator.start();
            if (onFocusChangeListener != null) {
                onFocusChangeListener.onFocusEnter();
            }
        }
    }

    /**
     * 自身或者子view都不存在焦点
     */
    private void focusLeave() {
        if (focusBorderImg != null && focusBorderImg.getVisibility() == VISIBLE) {
            Logger.i(TAG, "focusLeave");
            focusBorderImg.setVisibility(INVISIBLE);
            animator.cancel();
            if (onFocusChangeListener != null) {
                onFocusChangeListener.onFocusLeave();
            }
        }
    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    public interface OnFocusChangeListener {
        void onFocusEnter();
        void onFocusLeave();
    }
}
