package cn.cibn.kaibo.ui.orders;

import android.view.LayoutInflater;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentOrderDetailBinding;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelOrder;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class OrderDetailFragment extends KbBaseFragment<FragmentOrderDetailBinding> {

    private String title;
    private String qrcodeTitle;
    private ModelOrder.Item order;

    public static OrderDetailFragment createInstance() {
        return new OrderDetailFragment();
    }

    @Override
    protected FragmentOrderDetailBinding createBinding(LayoutInflater inflater) {
        return FragmentOrderDetailBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        updateView();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void updateView() {
        if (binding == null) {
            return;
        }
        binding.tvOrderDetailTitle.setText(title);
        binding.tvQrcodeName.setText(qrcodeTitle);
        if (order != null) {
            binding.tvGoodsName.setText(order.getName());
            binding.tvGoodsNum.setText("x" + order.getNum());
            binding.tvGoodsPrice.setText(order.getPrice());
            ImageLoadHelper.loadImage(binding.ivGoodsCover, order.getCover_pic(), (int) mContext.getResources().getDimension(R.dimen.dp_2), ConfigModel.getInstance().isGrayMode());
        }
        if (ConfigModel.getInstance().isGrayMode()) {
            binding.tvGoodsPriceSign.setTextColor(getResources().getColor(R.color.color_mode_gray));
            binding.tvGoodsPrice.setTextColor(getResources().getColor(R.color.color_mode_gray));
            binding.bgOrderQrcode.setBackgroundResource(R.drawable.bg_shop_qrcode_gray);
        } else {
            binding.tvGoodsPriceSign.setTextColor(0xFF1933);
            binding.tvGoodsPrice.setTextColor(0xFF1933);
            binding.bgOrderQrcode.setBackgroundResource(R.drawable.bg_shop_qrcode);
        }
    }

    public void setData(String title, String qrcodeTitle, ModelOrder.Item order) {
        this.title = title;
        this.qrcodeTitle = qrcodeTitle;
        this.order = order;
        updateView();
    }
}
