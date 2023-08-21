package cn.cibn.kaibo.ui.me;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.adapter.AnchorAdapter;
import cn.cibn.kaibo.databinding.FragmentFollowBinding;
import cn.cibn.kaibo.model.ModelAnchor;
import cn.cibn.kaibo.ui.BaseStackFragment;

public class FollowFragment extends BaseStackFragment<FragmentFollowBinding> {

    private AnchorAdapter adapter;

    @Override
    protected FragmentFollowBinding createSubBinding(LayoutInflater inflater) {
        return FragmentFollowBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        super.initView();
        subBinding.btnAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("page", "anchor");
                getParentFragmentManager().setFragmentResult("menu", bundle);
            }
        });
        subBinding.btnAnchor.requestFocus();

        adapter = new AnchorAdapter();
        subBinding.recyclerAnchorList.setNumColumns(4);
        subBinding.recyclerAnchorList.setAdapter(adapter);
        adapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<ModelAnchor.Item>() {
            @Override
            public void onItemClick(ModelAnchor.Item item) {
                Bundle bundle = new Bundle();
                bundle.putString("page", "anchor");
                getParentFragmentManager().setFragmentResult("menu", bundle);
            }
        });

    }

    @Override
    protected void initData() {
        List<ModelAnchor.Item> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
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
}
