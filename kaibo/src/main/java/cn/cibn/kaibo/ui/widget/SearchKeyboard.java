package cn.cibn.kaibo.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.databinding.ItemKeyboardBinding;


/**
 *
 */
public class SearchKeyboard extends FrameLayout {
    private VerticalGridView mRecyclerView;
    private List<String> keys = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0");
    private List<Keyboard> keyboardList = new ArrayList<>();
    private OnSearchKeyListener searchKeyListener;
    private OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View itemView, boolean hasFocus) {
            if (null != itemView && itemView != mRecyclerView) {
                itemView.setSelected(hasFocus);
            }
        }
    };

    public SearchKeyboard(@NonNull Context context) {
        this(context, null);
    }

    public SearchKeyboard(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchKeyboard(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_keyborad, this);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setNumColumns(6);
//        GridLayoutManager manager = new GridLayoutManager(getContext(), 6);
//        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View child) {
                if (child.isFocusable() && null == child.getOnFocusChangeListener()) {
                    child.setOnFocusChangeListener(focusChangeListener);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            keyboardList.add(new Keyboard(1, keys.get(i)));
        }
        final KeyboardAdapter adapter = new KeyboardAdapter();
        mRecyclerView.setAdapter(adapter);
        adapter.submitList(keyboardList);

        adapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<Keyboard>() {
            @Override
            public void onItemClick(Keyboard item) {
                if (searchKeyListener != null) {
                    searchKeyListener.onSearchKey(item.getKey());
                }
            }
        });

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (mRecyclerView.getChildCount() > 0) {
                    mRecyclerView.getChildAt(0).requestFocus();
                }
            }
        });
    }

    static class Keyboard {
        private int itemType;
        private String key;

        private Keyboard(int itemType, String key) {
            this.itemType = itemType;
            this.key = key;
        }

        public int getItemType() {
            return itemType;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof Keyboard)) {
                return false;
            }
            Keyboard another = (Keyboard) obj;
            return another.itemType == this.itemType && another.key == this.key;
        }
    }

    private static class KeyboardAdapter extends ListBindingAdapter<Keyboard, ItemKeyboardBinding> {

        public KeyboardAdapter() {
            super(new KeyDiff());
        }

        @Override
        public ItemKeyboardBinding createBinding(ViewGroup parent) {
            return ItemKeyboardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        }

        @Override
        public void onBindViewHolder(Keyboard data, ItemKeyboardBinding binding, int position) {
            binding.keyName.setText(data.key);
        }
    }

    private static class KeyDiff extends DiffUtil.ItemCallback<Keyboard> {

        @Override
        public boolean areItemsTheSame(@NonNull Keyboard oldItem, @NonNull Keyboard newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Keyboard oldItem, @NonNull Keyboard newItem) {
            return newItem.equals(oldItem);
        }
    }

    public void setOnSearchKeyListener(OnSearchKeyListener listener) {
        searchKeyListener = listener;
    }

    public interface OnSearchKeyListener {
        void onSearchKey(String key);
    }
}