package cn.cibn.kaibo.ui.orders;

import android.view.LayoutInflater;

import cn.cibn.kaibo.databinding.FragmentOrderDetailBinding;
import cn.cibn.kaibo.model.ModelOrder;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class OrderDetailFragment extends KbBaseFragment<FragmentOrderDetailBinding> {

    public static OrderDetailFragment createInstance() {
        return new OrderDetailFragment();
    }

    @Override
    protected FragmentOrderDetailBinding createBinding(LayoutInflater inflater) {
        return FragmentOrderDetailBinding.inflate(inflater);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void updateView() {

    }

    public void setData(String title, String qrCodeTitle, ModelOrder.Item order) {
        binding.tvOrderDetailTitle.setText(title);
        binding.tvQrcodeName.setText(qrCodeTitle);
        binding.tvGoodsName.setText(order.getName());
        binding.tvGoodsNum.setText("x" + order.getNum());
        binding.tvGoodsPrice.setText("¥ " + order.getPrice());
    }
}
