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
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.tv.lib.core.Logger;
import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.core.lang.ObjectStore;
import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.core.utils.Utils;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.adapter.SearchGuessAdapter;
import cn.cibn.kaibo.adapter.SearchHistoryAdapter;
import cn.cibn.kaibo.adapter.VideoGridAdapter;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.databinding.FragmentSearchBinding;
import cn.cibn.kaibo.db.AppDatabase;
import cn.cibn.kaibo.db.SearchHistory;
import cn.cibn.kaibo.model.ModelGuess;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.LiveMethod;
import cn.cibn.kaibo.ui.BaseStackFragment;
import cn.cibn.kaibo.ui.video.VideoListDataSource;
import cn.cibn.kaibo.ui.widget.FocusFrameLayout;
import cn.cibn.kaibo.ui.widget.SearchKeyboard;
import cn.cibn.kaibo.viewmodel.PlayerViewModel;

public class SearchFragment extends BaseStackFragment<FragmentSearchBinding> implements Handler.Callback, View.OnClickListener {
    private static final String TAG = "SearchFragment";
    public static final int PAGE_FIRST = 1;
    private static final int WHAT_GUESS = 1;

    private Handler handler;

    private String inputValue = "";

//    private SearchHistoryFragment historyFragment;
//    private SearchResultFragment resultFragment;
//    private KbBaseFragment<?> middleFragment;

    private SearchHistoryAdapter historyAdapter;
    private SearchGuessAdapter guessAdapter;
    private VideoGridAdapter resultAdapter;
    private PlayerViewModel playerViewModel;
    private SearchVideoSource searchVideoSource;

    private Animator moveRightAnimator;
    private Animator moveLeftAnimator;

    private int lastFocusPart;

    private List<ModelGuess.Item> guessList = new ArrayList<>();
    private int guessTotal;
    private int curGuessPage;
    private int guessLoadingPage;

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
    protected void onActivityCreated() {
        super.onActivityCreated();
        if (getActivity() != null) {
            playerViewModel = new ViewModelProvider(getActivity()).get(PlayerViewModel.class);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        subBinding.searchRoot.setSmoothScrollingEnabled(true);
        subBinding.btnSearchGoHome.setOnClickListener(this);
        initAnimator();
        initKeyboardView();
        initSearchHistoryView();
        initSearchGuessView();
        initSearchResultView();

        showHistory();
        hideResult();
    }

    @Override
    protected void initData() {
        searchVideoSource = new SearchVideoSource();
        searchVideoSource.setOnReadyListener(new VideoListDataSource.OnReadyListener() {
            @Override
            public void onSourceReady() {
                if (resultAdapter != null) {
                    resultAdapter.notifyDataSetChanged();
                }

//                subBinding.searchRoot.smoothScrollTo(Utils.getScreenWidth(ObjectStore.getContext()), 0);
//                subBinding.recyclerSearchResult.post(() -> {
//                    subBinding.recyclerSearchResult.requestFocus();
//                });
            }
        });
        if (playerViewModel != null) {
            playerViewModel.videoListDataSource.setValue(searchVideoSource);
        }
        resultAdapter.submitList(searchVideoSource.getLiveList());
    }

    @Override
    protected void updateView() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (subBinding != null) {
            subBinding.recyclerSearchMiddle.removeOnChildViewHolderSelectedListener(guessOnChildViewHolderSelectedListener);
        }
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
        if (msg.what == WHAT_GUESS) {
            reqSearchNames(PAGE_FIRST);
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
                    handler.sendEmptyMessageDelayed(WHAT_GUESS, 500);
                }
            }
            if (TextUtils.isEmpty(inputValue)) {
                showHistory();
            } else {
                showGuess();
            }
        } else if (id == subBinding.btnSearchGoHome.getId()) {
            goHome();
        }
    }

    @Override
    protected boolean isFullScreen() {
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void addChangedListenerKey(ArrayList<String> keys) {
        super.addChangedListenerKey(keys);
        keys.add(ChangedKeys.CHANGED_COVER_UPDATE);
    }

    @Override
    public void onListenerChange(String key, Object data) {
        if (ChangedKeys.CHANGED_COVER_UPDATE.equals(key)) {
            if (resultAdapter != null) {
                resultAdapter.notifyDataSetChanged();
            }
            return;
        }
        super.onListenerChange(key, data);
    }

    private void initKeyboardView() {
        int size = getResources().getDimensionPixelSize(R.dimen.dp_16);
        Drawable drawable = getResources().getDrawable(R.drawable.svg_backspace);
        drawable.setBounds(0, 0, size, size);
        subBinding.btnDelete.setCompoundDrawables(drawable, null, null, null);
        drawable = getResources().getDrawable(R.drawable.svg_delete);
        drawable.setBounds(0, 0, size, size);
        subBinding.btnClear.setCompoundDrawables(drawable, null, null, null);
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
                    handler.sendEmptyMessageDelayed(WHAT_GUESS, 500);
                }
                if (TextUtils.isEmpty(inputValue)) {
                    showHistory();
                } else {
                    showGuess();
                }
            }
        });

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
    }

    private void initSearchHistoryView() {
        historyAdapter = new SearchHistoryAdapter();
        historyAdapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<SearchHistory>() {
            @Override
            public void onItemClick(SearchHistory item) {
                ModelGuess.Item key = new ModelGuess.Item();
                key.setTag(item.getHistory());
                key.setInitials(item.getPinyin());
                reqSearch(key);
                subBinding.fflSearchResult.requestFocus();
            }
        });
    }

    private void initSearchGuessView() {
        guessAdapter = new SearchGuessAdapter();
        guessAdapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<ModelGuess.Item>() {
            @Override
            public void onItemClick(ModelGuess.Item item) {
                reqSearch(item);
            }
        });
        guessAdapter.submitList(guessList);
    }

    private void initSearchResultView() {
        int w = Utils.getScreenWidth(ObjectStore.getContext());
        ViewGroup.LayoutParams lp = subBinding.fflSearchResult.getLayoutParams();
        lp.width = w;
        subBinding.fflSearchResult.setLayoutParams(lp);
        subBinding.fflSearchResult.setOnFocusChangeListener(new FocusFrameLayout.OnFocusChangeListener() {
            @Override
            public void onFocusEnter() {
                subBinding.searchRoot.smoothScrollTo(Utils.getScreenWidth(ObjectStore.getContext()), 0);
            }

            @Override
            public void onFocusLeave() {

            }
        });

        resultAdapter = new VideoGridAdapter();
        subBinding.recyclerSearchResult.setAdapter(resultAdapter);
        subBinding.recyclerSearchResult.setNumColumns(4);
        resultAdapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<ModelLive.Item>() {
            @Override
            public void onItemClick(ModelLive.Item item) {
                if (getActivity() != null) {
                    ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_REQUEST_SUB_PLAY, item);
                    Bundle result = new Bundle();
                    result.putString("page", "subPlay");
                    getActivity().getSupportFragmentManager().setFragmentResult("menu", result);
                }
            }
        });

        if (playerViewModel != null) {
            playerViewModel.playingVideo.observe(getViewLifecycleOwner(), new Observer<ModelLive.Item>() {
                @Override
                public void onChanged(ModelLive.Item item) {
                    searchVideoSource.setCurLiveId(item.getId());
                }
            });
        }
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

    private void reqSearchNames(int page) {
        if (guessLoadingPage == page) {
            return;
        }
        if (TextUtils.isEmpty(inputValue)) {
            return;
        }
        guessLoadingPage = page;
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<ModelGuess> model;

            @Override
            public void execute() throws Exception {
                model = LiveMethod.getInstance().getGuessTag(inputValue, page, 20);
            }

            @Override
            public void callback(Exception e) {
                guessLoadingPage = -1;
                if (model != null && model.isSuccess() && model.getData() != null) {
                    curGuessPage = page;
                    guessTotal = model.getData().getRow_count();
                    if (page == PAGE_FIRST) {
                        guessList.clear();
                    }
                    if (model.getData().getList() != null) {
                        guessList.addAll(model.getData().getList());
                    }
//                    guessAdapter.submitList(guessList);
                    guessAdapter.notifyDataSetChanged();
                    if (!guessList.isEmpty()) {
                        reqSearch(guessList.get(0));
                    }
                    subBinding.recyclerSearchMiddle.post(() -> {
                        if (subBinding.recyclerSearchMiddle.getChildCount() > 0) {
                            subBinding.recyclerSearchMiddle.getChildAt(0).setSelected(true);
                        }
                    });
                }
            }
        });
    }

    private void reqSearch(ModelGuess.Item key) {
        subBinding.tvResultTitleKey.setText("\"@" + key.getTag() + "\"");
        showResult();
        searchVideoSource.setKey(key.getInitials());
        searchVideoSource.reqLiveList();
        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {
                AppDatabase.getInstance(mContext).searchHistoryDao().deleteByKey(key.getTag());

                SearchHistory sh = new SearchHistory();
                sh.setHistory(key.getTag());
                sh.setPinyin(key.getInitials());
                AppDatabase.getInstance(mContext).searchHistoryDao().insert(sh);
            }

            @Override
            public void callback(Exception e) {
                updateHistoryData();
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
        subBinding.recyclerSearchMiddle.removeOnChildViewHolderSelectedListener(guessOnChildViewHolderSelectedListener);
        ViewGroup.LayoutParams lp = subBinding.fflSearchMiddle.getLayoutParams();
        lp.width = Utils.getScreenWidth(ObjectStore.getContext()) - ObjectStore.getContext().getResources().getDimensionPixelSize(R.dimen.drawer_menu_width);
        subBinding.fflSearchMiddle.setLayoutParams(lp);
        updateHistoryData();
    }

    private void updateHistoryData() {
        TaskHelper.exec(new TaskHelper.Task() {
            List<SearchHistory> historyList;

            @Override
            public void execute() throws Exception {
                historyList = AppDatabase.getInstance(mContext).searchHistoryDao().getAll();
            }

            @Override
            public void callback(Exception e) {
                if (historyAdapter != null && historyList != null) {
                    historyAdapter.submitList(historyList);
                }
            }
        });
    }

    private void showGuess() {
        if (getResources().getString(R.string.search_guess).equals(subBinding.tvMiddleTitle.getText().toString())) {
            return;
        }
        subBinding.tvMiddleTitle.setText(R.string.search_guess);
        subBinding.recyclerSearchMiddle.setAdapter(guessAdapter);
        subBinding.recyclerSearchMiddle.setNumColumns(1);
        subBinding.recyclerSearchMiddle.addOnChildViewHolderSelectedListener(guessOnChildViewHolderSelectedListener);
        ViewGroup.LayoutParams lp = subBinding.fflSearchMiddle.getLayoutParams();
        lp.width = ObjectStore.getContext().getResources().getDimensionPixelSize(R.dimen.dp_279);
        subBinding.fflSearchMiddle.setLayoutParams(lp);
        guessList.clear();
        guessAdapter.notifyDataSetChanged();
    }

    private void showResult() {
//        subBinding.fflSearchResult.setVisibility(View.VISIBLE);
        subBinding.tvResultTitleTail.setVisibility(View.VISIBLE);
    }

    private void hideResult() {
//        subBinding.fflSearchResult.setVisibility(View.GONE);
        subBinding.tvResultTitleKey.setText("暂无搜索结果");
        subBinding.tvResultTitleTail.setVisibility(View.GONE);
    }

    private static class SearchVideoSource extends VideoListDataSource {
        private String key;

        public void setKey(String key) {
            this.key = key;
        }

        @Override
        public void reqLiveList() {
            TaskHelper.exec(new TaskHelper.Task() {
                ModelWrapper<ModelLive> model;
                @Override
                public void execute() throws Exception {
                    model = LiveMethod.getInstance().reqSearch(key, 1, 10);
//                    List<ModelLive.Item> list = new ArrayList<>();
//                    for (int i = 0; i < 10; i++) {
//                        ModelLive.Item item = new ModelLive.Item();
//                        item.setId("11");
//                        item.setTitle("Video " + i);
//                        item.setBack_img("https://img.cbnlive.cn/web/uploads/image/store_1/bd3ecde03cc241c818fccccffeac9a3e3528809e.jpg");
//                        item.setPlay_addr("http://1500005830.vod2.myqcloud.com/43843ec0vodtranscq1500005830/3afba03a387702294394228636/adp.10.m3u8");
//                        list.add(item);
//                    }
//                    live.setList(list);
                }

                @Override
                public void callback(Exception e) {
                    if (model != null && model.isSuccess() && model.getData() != null) {
                        updateLiveList(model.getData());
                    }
                }
            });
        }
    }

    private OnChildViewHolderSelectedListener guessOnChildViewHolderSelectedListener = new OnChildViewHolderSelectedListener() {
        @Override
        public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
            int count = guessAdapter.getItemCount();
            if (count - position < 4 && count < guessTotal) {
                reqSearchNames(curGuessPage + 1);
            }
        }
    };
}
