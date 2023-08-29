package cn.cibn.kaibo.ui.video;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.tv.lib.core.utils.ui.SafeToast;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.ui.base.BaseDialog;

public class VideoOperateDialog extends BaseDialog implements View.OnClickListener {

    public static void show(FragmentManager manager) {
        VideoOperateDialog dialog = new VideoOperateDialog();
        dialog.setFragmentManager(manager);
        dialog.show();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_video_operate;
    }

    @Override
    protected void initView(View parent) {
        parent.findViewById(R.id.btn_play_follow).setOnClickListener(this);
        parent.findViewById(R.id.btn_like).setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected String getShowTag() {
        return "VideoOperateDialog";
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_play_follow) {
            SafeToast.showToast("关注", Toast.LENGTH_SHORT);
        } else if (id == R.id.btn_like) {
            SafeToast.showToast("点赞", Toast.LENGTH_SHORT);
            Bundle bundle = new Bundle();
            bundle.putString("page", "opLike");
            getParentFragmentManager().setFragmentResult("menu", bundle);
            dismiss();
        }
    }


}
