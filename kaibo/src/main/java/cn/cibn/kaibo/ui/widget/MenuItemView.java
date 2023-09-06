package cn.cibn.kaibo.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
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
        super(context, attrs, defStyleAttr);
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

                if (a.hasValue(R.styleable.MenuItemView_name_color)) {
                    int nameColor = a.getColor(R.styleable.MenuItemView_name_color, Color.WHITE);
                    tvName.setTextColor(nameColor);
                }

                if (a.hasValue(R.styleable.MenuItemView_font_size)) {
                    int fontSize = a.getDimensionPixelSize(R.styleable.MenuItemView_font_size, 15);
                    tvName.setTextSize(fontSize);
                }

                if (a.hasValue(R.styleable.MenuItemView_icon_padding)) {
                    int padding = a.getDimensionPixelSize(R.styleable.MenuItemView_icon_padding, 10);
                    MarginLayoutParams mlp = (MarginLayoutParams) tvName.getLayoutParams();
                    mlp.leftMargin = padding;
                    tvName.setLayoutParams(mlp);
                }

                a.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setOnFocusChangeListener(this);
    }

    public void setMenuName(String menuName) {
        if (tvName != null && !TextUtils.isEmpty(menuName)) {
            tvName.setText(menuName);
        }
    }
}
