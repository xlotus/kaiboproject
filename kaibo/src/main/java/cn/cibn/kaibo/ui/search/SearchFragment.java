package cn.cibn.kaibo.ui.search;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tv.lib.core.Logger;
import com.tv.lib.core.lang.ObjectStore;
import com.tv.lib.core.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.adapter.SearchHistoryAdapter;
import cn.cibn.kaibo.databinding.FragmentSearchBinding;
import cn.cibn.kaibo.ui.KbBaseFragment;
import cn.cibn.kaibo.ui.widget.FocusFrameLayout;
import cn.cibn.kaibo.ui.widget.SearchKeyboard;

public class SearchFragment extends KbBaseFragment<FragmentSearchBinding> implements Handler.Callback, View.OnClickListener {
    private static final String TAG = "SearchFragment";
    private static final int WHAT_SEARCH = 1;

    private Handler handler;

    private String inputValue = "";

//    private SearchHistoryFragment historyFragment;
//    private SearchResultFragment resultFragment;
//    private KbBaseFragment<?> middleFragment;

    private SearchHistoryAdapter historyAdapter;
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
    protected FragmentSearchBinding createBinding(LayoutInflater inflater) {
        return FragmentSearchBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        binding.searchRoot.setSmoothScrollingEnabled(true);
        binding.btnClear.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
        binding.searchKeyboard.setOnSearchKeyListener(new SearchKeyboard.OnSearchKeyListener() {
            @Override
            public void onSearchKey(String key) {
                inputValue += key;
                binding.etSearch.setText(inputValue);
                binding.etSearch.setSelection(inputValue.length());
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(WHAT_SEARCH, 500);
                }
            }
        });
        showHistory();
        showResult();
        initAnimator();

        binding.fflSearchInput.setOnFocusChangeListener(new FocusFrameLayout.OnFocusChangeListener() {
            @Override
            public void onFocusEnter() {
                Logger.d(TAG, "lastFocusPart = " + lastFocusPart);
                binding.searchRoot.smoothScrollTo(0, 0);

            }

            @Override
            public void onFocusLeave() {
//                lastFocusPart = 1;
            }
        });
        binding.fflSearchMiddle.setOnFocusChangeListener(new FocusFrameLayout.OnFocusChangeListener() {
            @Override
            public void onFocusEnter() {
//                if (lastFocusPart == 1) {
//                    moveLeftAnimator.start();
//                } else if (lastFocusPart == 3) {
//                    moveRightAnimator.start();
//                }
//                middleFragment.requestFocus();
            }

            @Override
            public void onFocusLeave() {
//                lastFocusPart = 2;
            }
        });
        binding.fflSearchResult.setOnFocusChangeListener(new FocusFrameLayout.OnFocusChangeListener() {
            @Override
            public void onFocusEnter() {
//                moveLeftAnimator.start();
//                resultFragment.requestFocus();
            }

            @Override
            public void onFocusLeave() {
//                lastFocusPart = 3;
            }
        });

        historyAdapter = new SearchHistoryAdapter();
        binding.recyclerSearchHistory.setAdapter(historyAdapter);
        resultAdapter = new SearchHistoryAdapter();
        binding.recyclerSearchResult.setAdapter(resultAdapter);
        binding.recyclerSearchResult.setNumColumns(4);
        int w = Utils.getScreenWidth(ObjectStore.getContext());
        ViewGroup.LayoutParams lp = binding.fflSearchResult.getLayoutParams();
        lp.width = w;
        binding.fflSearchResult.setLayoutParams(lp);
    }

    @Override
    protected void initData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            list.add("LIST " + i);
        }
        historyAdapter.submitList(list);
        resultAdapter.submitList(list);
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
        if (id == binding.btnClear.getId()) {
            this.inputValue = "";
            binding.etSearch.setText("");
        } else if (id == binding.btnDelete.getId()) {
            if (!this.inputValue.isEmpty()) {
                inputValue = inputValue.substring(0, inputValue.length() - 1);
                binding.etSearch.setText(inputValue);
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(WHAT_SEARCH, 500);
                }
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Bundle bundle = new Bundle();
            bundle.putString("page", "back");
            getParentFragmentManager().setFragmentResult("menu", bundle);
            return true;
        }
        return false;
    }

    private void initAnimator() {
        float translationX = mContext.getResources().getDimension(R.dimen.dp_469);
        moveRightAnimator = ObjectAnimator.ofFloat(binding.searchRoot, "x", -translationX, 0);
        moveRightAnimator.setDuration(500);
        moveRightAnimator.setInterpolator(new DecelerateInterpolator());
        moveLeftAnimator = ObjectAnimator.ofFloat(binding.searchRoot, "x", 0, -translationX);
        moveLeftAnimator.setDuration(500);
        moveLeftAnimator.setInterpolator(new DecelerateInterpolator());
    }

    private void reqSearchNames() {

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
