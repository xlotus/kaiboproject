package cn.cibn.kaibo.ui.me;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.RecyclerView;

import com.tv.lib.core.Logger;
import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.adapter.AnchorAdapter;
import cn.cibn.kaibo.databinding.FragmentFollowBinding;
import cn.cibn.kaibo.model.ModelAnchor;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.UserMethod;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class FollowFragment extends KbBaseFragment<FragmentFollowBinding> {
    private static final String TAG = "FollowFragment";

    private static final int PAGE_FIRST = 1;

    private AnchorAdapter adapter;

    private List<ModelAnchor.Item> followList = new ArrayList<>();
    private int total;
    private int curPage;
    private int loadingPage = -1;

    public static FollowFragment createInstance() {
        return new FollowFragment();
    }

    @Override
    protected FragmentFollowBinding createBinding(LayoutInflater inflater) {
        return FragmentFollowBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        Logger.d(TAG, "initView");
        adapter = new AnchorAdapter();
        binding.recyclerAnchorList.setNumColumns(3);
        binding.recyclerAnchorList.setAdapter(adapter);
        adapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<ModelAnchor.Item>() {
            @Override
            public void onItemClick(ModelAnchor.Item item) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "anchor");
                bundle.putSerializable("data", item);
                getParentFragmentManager().setFragmentResult("meGroup", bundle);
            }
        });

        binding.recyclerAnchorList.addOnChildViewHolderSelectedListener(onChildViewHolderSelectedListener);
    }

    @Override
    protected void initData() {
        loadingPage = -1;
        total = 0;
        followList.clear();
        reqFollowList(PAGE_FIRST);
    }
    @Override
    protected void updateView() {

    }

    @Override
    public void requestFocus() {
        if (adapter != null) {
            adapter.requestFocus();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.d(TAG, "onDestroyView");
        if (binding != null) {
            binding.recyclerAnchorList.removeOnChildViewHolderSelectedListener(onChildViewHolderSelectedListener);
        }
    }

    private void reqFollowList(int page) {
        if (page == loadingPage) {
            return;
        }
        loadingPage = page;
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<ModelAnchor> model;

            @Override
            public void execute() throws Exception {
                model = UserMethod.getInstance().getFollowList(page, 10);
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess() && model.getData() != null) {
                    curPage = loadingPage;
                    total = model.getData().getRow_count();
                    if (loadingPage == PAGE_FIRST) {
                        followList.clear();
                    }
                    if (model.getData().getList() != null) {
                        followList.addAll(model.getData().getList());
                    }
                    adapter.submitList(followList);
                }
            }
        });
    }

    private OnChildViewHolderSelectedListener onChildViewHolderSelectedListener = new OnChildViewHolderSelectedListener() {
        @Override
        public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
            int count = adapter.getItemCount();
            if (count - position < 6 && count < total) {
                reqFollowList(curPage + 1);
            }
        }
    };
}
