package cn.cibn.kaibo.ui.search;

import android.view.LayoutInflater;

import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.adapter.SearchGuessAdapter;
import cn.cibn.kaibo.databinding.FragmentSearchGuessBinding;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class SearchGuessFragment extends KbBaseFragment<FragmentSearchGuessBinding> {

    private SearchGuessAdapter adapter;
    private OnSearchGuessClickedListener listener;
    @Override
    protected FragmentSearchGuessBinding createBinding(LayoutInflater inflater) {
        return FragmentSearchGuessBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        adapter = new SearchGuessAdapter();
        adapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<String>() {
            @Override
            public void onItemClick(String item) {
                if (listener != null) {
                    listener.onSearchGuessClicked(item);
                }
            }
        });
        binding.recyclerSearchGuess.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("guess " + i);
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
            binding.recyclerSearchGuess.post(new Runnable() {
                @Override
                public void run() {
                    adapter.requestFocus();
                }
            });

        }
    }

    public void setOnGuessClickedListener(OnSearchGuessClickedListener listener) {
        this.listener = listener;
    }

    public interface OnSearchGuessClickedListener {
        void onSearchGuessClicked(String word);
    }
}
