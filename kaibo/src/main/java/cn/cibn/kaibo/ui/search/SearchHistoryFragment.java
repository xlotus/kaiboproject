package cn.cibn.kaibo.ui.search;

import android.view.LayoutInflater;

import androidx.leanback.app.SearchSupportFragment;

import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.adapter.SearchHistoryAdapter;
import cn.cibn.kaibo.databinding.FragmentSearchHistoryBinding;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class SearchHistoryFragment extends KbBaseFragment<FragmentSearchHistoryBinding> {

    private SearchHistoryAdapter adapter;
    private OnSearchHistoryClickedListener listener;
    @Override
    protected FragmentSearchHistoryBinding createBinding(LayoutInflater inflater) {
        return FragmentSearchHistoryBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        adapter = new SearchHistoryAdapter();
        adapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<String>() {
            @Override
            public void onItemClick(String item) {
                if (listener != null) {
                    listener.onSearchHistoryClicked(item);
                }
            }
        });
        binding.recyclerSearchHistory.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("History " + i);
        }
        adapter.submitList(list);
    }

    @Override
    protected void updateView() {

    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        if (adapter != null) {
            binding.recyclerSearchHistory.post(new Runnable() {
                @Override
                public void run() {
                    adapter.requestFocus();
                }
            });

        }
    }

    public void setOnHistoryClickedListener(OnSearchHistoryClickedListener listener) {
        this.listener = listener;
    }

    public interface OnSearchHistoryClickedListener {
        void onSearchHistoryClicked(String word);
    }
}
