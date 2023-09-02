package cn.cibn.kaibo.ui.me;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.adapter.AnchorAdapter;
import cn.cibn.kaibo.databinding.FragmentFollowBinding;
import cn.cibn.kaibo.model.ModelAnchor;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class FollowFragment extends KbBaseFragment<FragmentFollowBinding> {

    private AnchorAdapter adapter;

    public static FollowFragment createInstance() {
        return new FollowFragment();
    }

    @Override
    protected FragmentFollowBinding createBinding(LayoutInflater inflater) {
        return FragmentFollowBinding.inflate(inflater);
    }

    @Override
    protected void initView() {

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

    }

    @Override
    protected void initData() {
        List<ModelAnchor.Item> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ModelAnchor.Item item = new ModelAnchor.Item();
            item.setTitle("anchor" + i);
            item.setCover_img("https://img.cbnlive.cn/web/uploads/image/store_1/bd3ecde03cc241c818fccccffeac9a3e3528809e.jpg");
            list.add(item);
        }
        adapter.submitList(list);
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
}
