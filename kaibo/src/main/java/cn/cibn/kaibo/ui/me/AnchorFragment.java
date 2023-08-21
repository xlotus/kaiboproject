package cn.cibn.kaibo.ui.me;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.adapter.VideoListAdapter;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.databinding.FragmentAnchorBinding;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.ui.BaseStackFragment;

public class AnchorFragment extends BaseStackFragment<FragmentAnchorBinding> {

    private VideoListAdapter adapter;
    private boolean inPlay;

    @Override
    protected FragmentAnchorBinding createSubBinding(LayoutInflater inflater) {
        return FragmentAnchorBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        super.initView();
        adapter = new VideoListAdapter();
        adapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<ModelLive.Item>() {
            @Override
            public void onItemClick(ModelLive.Item item) {
                ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_REQUEST_SUB_PLAY, item);
                inPlay = true;
                updateView();
            }
        });
        subBinding.recyclerAnchorVideoList.setNumColumns(4);
        subBinding.recyclerAnchorVideoList.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        List<ModelLive.Item> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ModelLive.Item item = new ModelLive.Item();
            item.setId("1" + i);
            item.setTitle("Video " + i);
            item.setBack_img("https://img.cbnlive.cn/web/uploads/image/store_1/bd3ecde03cc241c818fccccffeac9a3e3528809e.jpg");
            item.setPlay_addr("http://1500005830.vod2.myqcloud.com/43843ec0vodtranscq1500005830/3afba03a387702294394228636/adp.10.m3u8");
            list.add(item);
        }
        adapter.submitList(list);
        subBinding.recyclerAnchorVideoList.post(new Runnable() {
            @Override
            public void run() {
                adapter.requestFocus();
            }
        });
    }
    @Override
    protected void updateView() {
        subBinding.recyclerAnchorVideoList.setVisibility(inPlay ? View.GONE : View.VISIBLE);
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        if (adapter != null) {
            adapter.requestFocus();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inPlay) {
                ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_REQUEST_RESTORE_PLAY);
                inPlay = false;
                updateView();
                requestFocus();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
