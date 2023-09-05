package cn.cibn.kaibo.ui.video;

import android.view.View;

import androidx.fragment.app.FragmentManager;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.player.VideoType;
import cn.cibn.kaibo.ui.base.BaseDialog;

public class VideoOperateDialog extends BaseDialog implements View.OnClickListener {

    private ModelLive.Item liveItem;
    private VideoOpListener opListener;

    public static VideoOperateDialog show(FragmentManager manager, ModelLive.Item item) {
        VideoOperateDialog dialog = new VideoOperateDialog();
        dialog.setFragmentManager(manager);
        dialog.liveItem = item;
        dialog.show();
        return dialog;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_video_operate;
    }

    @Override
    protected void initView(View parent) {
        View btnLike = parent.findViewById(R.id.btn_like);
        parent.findViewById(R.id.btn_play_follow).setOnClickListener(this);
        btnLike.setOnClickListener(this);
        if (liveItem != null && liveItem.getType() != VideoType.SHORT.getValue()) {
            btnLike.setVisibility(View.GONE);
        }
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
//            if (getActivity() != null) {
//                Bundle bundle = new Bundle();
//                bundle.putString("page", "opFollow");
//                getActivity().getSupportFragmentManager().setFragmentResult("menu", bundle);
//                dismiss();
//            }
            if (opListener != null) {
                opListener.onFollowClick(liveItem);
                dismiss();
            }
        } else if (id == R.id.btn_like) {
//            if (getActivity() != null) {
//                Bundle bundle = new Bundle();
//                bundle.putString("page", "opLike");
//                getActivity().getSupportFragmentManager().setFragmentResult("menu", bundle);
//                dismiss();
//            }
            if (opListener != null) {
                opListener.onGiveClick(liveItem);
                dismiss();
            }
        }
    }

    public void setOpListener(VideoOpListener opListener) {
        this.opListener = opListener;
    }

    public interface VideoOpListener {
        void onFollowClick(ModelLive.Item item);
        void onGiveClick(ModelLive.Item item);
    }
}
