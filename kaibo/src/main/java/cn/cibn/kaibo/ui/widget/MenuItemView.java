package cn.cibn.kaibo.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.cibn.kaibo.R;

public class MenuItemView extends LinearLayout implements View.OnFocusChangeListener {

    private View viewIcon;
    private TextView tvName;

    public MenuItemView(Context context) {
        this(context, null);
    }

    public MenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MenuItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            viewIcon.setPressed(true);
            tvName.setPressed(true);
        } else {
            viewIcon.setPressed(false);
            tvName.setPressed(false);
        }
    }

    private void initView(Context context, AttributeSet attrs) {
        setGravity(Gravity.CENTER);
        setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        LayoutInflater.from(context).inflate(R.layout.layout_menu_item, this, true);
        viewIcon = findViewById(R.id.view_menu_icon);
        tvName = findViewById(R.id.tv_menu_name);

        if (attrs != null) {
            try {
                final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuItemView, 0, 0);

                Drawable menuIcon = a.getDrawable(R.styleable.MenuItemView_menu_icon);
                if (menuIcon != null) {
                    viewIcon.setBackground(menuIcon);
                }

                int iconSize = a.getDimensionPixelSize(R.styleable.MenuItemView_icon_size, -1);
                if (iconSize > 0) {
                    ViewGroup.LayoutParams lp = viewIcon.getLayoutParams();
                    lp.width = iconSize;
                    lp.height = iconSize;
                    viewIcon.setLayoutParams(lp);
                }

                String menuName = a.getString(R.styleable.MenuItemView_menu_name);
                tvName.setText(menuName);


                a.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setOnFocusChangeListener(this);
    }
}