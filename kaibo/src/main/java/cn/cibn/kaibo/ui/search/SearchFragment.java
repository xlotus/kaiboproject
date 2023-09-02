package cn.cibn.kaibo.ui.search;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tv.lib.core.Logger;
import com.tv.lib.core.lang.ObjectStore;
import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.core.utils.Utils;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.adapter.SearchGuessAdapter;
import cn.cibn.kaibo.adapter.SearchHistoryAdapter;
import cn.cibn.kaibo.databinding.FragmentSearchBinding;
import cn.cibn.kaibo.db.AppDatabase;
import cn.cibn.kaibo.db.SearchHistory;
import cn.cibn.kaibo.ui.BaseStackFragment;
import cn.cibn.kaibo.ui.widget.FocusFrameLayout;
import cn.cibn.kaibo.ui.widget.SearchKeyboard;

public class SearchFragment extends BaseStackFragment<FragmentSearchBinding> implements Handler.Callback, View.OnClickListener {
    private static final String TAG = "SearchFragment";
    private static final int WHAT_SEARCH = 1;

    private Handler handler;

    private String inputValue = "";

//    private SearchHistoryFragment historyFragment;
//    private SearchResultFragment resultFragment;
//    private KbBaseFragment<?> middleFragment;

    private SearchHistoryAdapter historyAdapter;
    private SearchGuessAdapter guessAdapter;
    private SearchHistoryAdapter resultAdapter;

    private Animator moveRightAnimator;
    private Animator moveLeftAnimator;

    private int lastFocusPart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(this);
    }

    @Override
    protected FragmentSearchBinding createSubBinding(LayoutInflater inflater) {
        return FragmentSearchBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        super.initView();
        int size = getResources().getDimensionPixelSize(R.dimen.dp_16);
        Drawable drawable = getResources().getDrawable(R.drawable.baseline_backspace_24);
        drawable.setBounds(0, 0, size, size);
        subBinding.btnDelete.setCompoundDrawables(drawable, null, null, null);
        drawable = getResources().getDrawable(R.drawable.baseline_delete_24);
        drawable.setBounds(0, 0, size, size);
        subBinding.btnClear.setCompoundDrawables(drawable, null, null, null);

        subBinding.searchRoot.setSmoothScrollingEnabled(true);
        subBinding.btnClear.setOnClickListener(this);
        subBinding.btnDelete.setOnClickListener(this);
        subBinding.searchKeyboard.setOnSearchKeyListener(new SearchKeyboard.OnSearchKeyListener() {
            @Override
            public void onSearchKey(String key) {
                inputValue += key;
                subBinding.etSearch.setText(inputValue);
                subBinding.etSearch.setSelection(inputValue.length());
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(WHAT_SEARCH, 500);
                }
                if (TextUtils.isEmpty(inputValue)) {
                    showHistory();
                } else {
                    showGuess();
                }
            }
        });
        initAnimator();

        subBinding.fflSearchInput.setOnFocusChangeListener(new FocusFrameLayout.OnFocusChangeListener() {
            @Override
            public void onFocusEnter() {
                Logger.d(TAG, "lastFocusPart = " + lastFocusPart);
                subBinding.searchRoot.smoothScrollTo(0, 0);

            }

            @Override
            public void onFocusLeave() {
//                lastFocusPart = 1;
            }
        });

        historyAdapter = new SearchHistoryAdapter();
        guessAdapter = new SearchGuessAdapter();
        resultAdapter = new SearchHistoryAdapter();
        subBinding.recyclerSearchResult.setAdapter(resultAdapter);
        subBinding.recyclerSearchResult.setNumColumns(4);
        int w = Utils.getScreenWidth(ObjectStore.getContext());
        ViewGroup.LayoutParams lp = subBinding.fflSearchResult.getLayoutParams();
        lp.width = w;
        subBinding.fflSearchResult.setLayoutParams(lp);


        historyAdapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<String>() {
            @Override
            public void onItemClick(String item) {
                reqSearch(item);
            }
        });
        guessAdapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<String>() {
            @Override
            public void onItemClick(String item) {
                reqSearch(item);
            }
        });

        showHistory();
        showResult();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void updateView() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == WHAT_SEARCH) {
            reqSearchNames();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == subBinding.btnClear.getId()) {
            this.inputValue = "";
            subBinding.etSearch.setText("");
            showHistory();
        } else if (id == subBinding.btnDelete.getId()) {
            if (!this.inputValue.isEmpty()) {
                inputValue = inputValue.substring(0, inputValue.length() - 1);
                subBinding.etSearch.setText(inputValue);
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(WHAT_SEARCH, 500);
                }
            }
            if (TextUtils.isEmpty(inputValue)) {
                showHistory();
            } else {
                showGuess();
            }
        }
    }

    @Override
    protected boolean isFullScreen() {
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private void initAnimator() {
        float translationX = mContext.getResources().getDimension(R.dimen.dp_469);
        moveRightAnimator = ObjectAnimator.ofFloat(subBinding.searchRoot, "x", -translationX, 0);
        moveRightAnimator.setDuration(500);
        moveRightAnimator.setInterpolator(new DecelerateInterpolator());
        moveLeftAnimator = ObjectAnimator.ofFloat(subBinding.searchRoot, "x", 0, -translationX);
        moveLeftAnimator.setDuration(500);
        moveLeftAnimator.setInterpolator(new DecelerateInterpolator());
    }

    private void reqSearchNames() {
        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {

            }

            @Override
            public void callback(Exception e) {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 40; i++) {
                    list.add("Guess " + i);
                }
                guessAdapter.submitList(list);
            }
        });
    }

    private void reqSearch(String key) {
        subBinding.tvResultTitleKey.setText("\"@" + key + "\"");
        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {
                int count = AppDatabase.getInstance(mContext).searchHistoryDao().existCount(key);
                if (count < 1) {
                    SearchHistory sh = new SearchHistory();
                    sh.setHistory(key);
                    AppDatabase.getInstance(mContext).searchHistoryDao().insert(sh);
                }
            }

            @Override
            public void callback(Exception e) {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 40; i++) {
                    list.add("LIST " + i);
                }
                resultAdapter.submitList(list);
                subBinding.searchRoot.smoothScrollTo(Utils.getScreenWidth(ObjectStore.getContext()), 0);
                subBinding.recyclerSearchResult.post(() -> {
                    subBinding.recyclerSearchResult.requestFocus();
                });
            }
        });
    }

    private void showHistory() {
//        if (historyFragment == null) {
//            historyFragment = new SearchHistoryFragment();
//            historyFragment.setOnHistoryClickedListener(new SearchHistoryFragment.OnSearchHistoryClickedListener() {
//                @Override
//                public void onSearchHistoryClicked(String word) {
//                    hideKeyboard();
//                }
//            });
//        }
//        getChildFragmentManager().beginTransaction().replace(R.id.layout_search_middle, historyFragment).commit();
//        middleFragment = historyFragment;
        subBinding.tvMiddleTitle.setText(R.string.search_history);
        subBinding.recyclerSearchMiddle.setAdapter(historyAdapter);
        subBinding.recyclerSearchMiddle.setNumColumns(3);
        ViewGroup.LayoutParams lp = subBinding.fflSearchMiddle.getLayoutParams();
        lp.width = Utils.getScreenWidth(ObjectStore.getContext()) - ObjectStore.getContext().getResources().getDimensionPixelSize(R.dimen.drawer_menu_width);
        subBinding.fflSearchMiddle.setLayoutParams(lp);
        TaskHelper.exec(new TaskHelper.Task() {
            List<String> historyList;

            @Override
            public void execute() throws Exception {
                historyList = new ArrayList<>();
                List<SearchHistory> list = AppDatabase.getInstance(mContext).searchHistoryDao().getAll();
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        historyList.add(list.get(i).getHistory());
                    }
                }
            }

            @Override
            public void callback(Exception e) {
                historyAdapter.submitList(historyList);
            }
        });
    }

    private void showGuess() {
        subBinding.tvMiddleTitle.setText(R.string.search_guess);
        subBinding.recyclerSearchMiddle.setAdapter(guessAdapter);
        subBinding.recyclerSearchMiddle.setNumColumns(1);
        ViewGroup.LayoutParams lp = subBinding.fflSearchMiddle.getLayoutParams();
        lp.width = ObjectStore.getContext().getResources().getDimensionPixelSize(R.dimen.dp_279);
        subBinding.fflSearchMiddle.setLayoutParams(lp);
    }

    private void showResult() {
//        if (resultFragment == null) {
//            resultFragment = new SearchResultFragment();
//        }
//        getChildFragmentManager().beginTransaction().replace(R.id.layout_search_result, resultFragment).commit();
    }

    private void hideKeyboard() {
        moveLeftAnimator.start();

//        binding.layoutSearchInput.setVisibility(View.GONE);
    }
}
