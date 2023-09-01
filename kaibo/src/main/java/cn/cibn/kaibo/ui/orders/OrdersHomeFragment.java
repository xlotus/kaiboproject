package cn.cibn.kaibo.ui.orders;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.GravityCompat;

import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.adapter.MenuAdapter;
import cn.cibn.kaibo.adapter.OrderAdapter;
import cn.cibn.kaibo.databinding.FragmentOrdersHomeBinding;
import cn.cibn.kaibo.model.MenuItem;
import cn.cibn.kaibo.model.ModelOrder;
import cn.cibn.kaibo.model.OrderAction;
import cn.cibn.kaibo.ui.BaseStackFragment;

public class OrdersHomeFragment extends BaseStackFragment<FragmentOrdersHomeBinding> implements View.OnClickListener {

    private OrderDetailFragment orderDetailFragment;

    private OrderAdapter orderAdapter;
    private MenuAdapter menuAdapter;

    private int page = 0;

    public static OrdersHomeFragment createInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("page", page);
        OrdersHomeFragment fragment = new OrdersHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentOrdersHomeBinding createSubBinding(LayoutInflater inflater) {
        return FragmentOrdersHomeBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        super.initView();
        subBinding.btnGoHome.setOnClickListener(this);
        subBinding.btnPageNeedPay.setOnClickListener(this);
        subBinding.btnPageNeedSend.setOnClickListener(this);
        subBinding.btnPageNeedReceive.setOnClickListener(this);
        subBinding.btnPageFinished.setOnClickListener(this);
        subBinding.btnPageService.setOnClickListener(this);

        orderDetailFragment = OrderDetailFragment.createInstance();
        getChildFragmentManager().beginTransaction().replace(subBinding.orderNaviRight.getId(), orderDetailFragment).commit();

        menuAdapter = new MenuAdapter();
        subBinding.recyclerOrderMenu.setAdapter(menuAdapter);
        menuAdapter.setOnItemClickListener(new ListBindingAdapter.OnItemClickListener<MenuItem>() {
            @Override
            public void onItemClick(MenuItem item) {
                page = item.getId();
                reqOrderList();
            }
        });

        orderAdapter = new OrderAdapter();
        subBinding.recyclerOrderList.setNumColumns(2);
        subBinding.recyclerOrderList.setAdapter(orderAdapter);
        orderAdapter.setOrderActionListener(new OrderAdapter.OrderActionListener() {
            @Override
            public void onOrderAction(ModelOrder.Item item, OrderAction action) {
                if (action.getId() == OrderAction.VIEW_LOGISTICS.getId()) {
                    if (!subBinding.drawerOrders.isDrawerOpen(GravityCompat.END)) {
                        subBinding.drawerOrders.openDrawer(GravityCompat.END);
                    }
                    orderDetailFragment.setData("查看物流", "微信扫码查看物流", item);
                } else if (action.getId() == OrderAction.PAY.getId()) {
                    if (!subBinding.drawerOrders.isDrawerOpen(GravityCompat.END)) {
                        subBinding.drawerOrders.openDrawer(GravityCompat.END);
                    }
                    orderDetailFragment.setData("立即付款", "微信扫码去付款", item);
                } else if (action.getId() == OrderAction.RETURN.getId()) {
                    if (!subBinding.drawerOrders.isDrawerOpen(GravityCompat.END)) {
                        subBinding.drawerOrders.openDrawer(GravityCompat.END);
                    }
                    orderDetailFragment.setData("售后", "微信扫码寄回商品", item);
                } else if (action.getId() == OrderAction.CONFIRM_RECEIPT.getId()) {

                }
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            page = args.getInt("page", 0);
        }

        subBinding.recyclerOrderMenu.post(new Runnable() {
            @Override
            public void run() {
                if (subBinding.recyclerOrderMenu.getChildCount() > page) {
                    subBinding.recyclerOrderMenu.getChildAt(page).requestFocus();
                }
            }
        });
    }

    @Override
    protected void updateView() {

    }

    @Override
    protected void initData() {
        List<MenuItem> menus = new ArrayList<>();
        menus.add(new MenuItem(0, getString(R.string.order_need_pay)));
        menus.add(new MenuItem(1, getString(R.string.order_need_send)));
        menus.add(new MenuItem(2, getString(R.string.order_need_receive)));
        menus.add(new MenuItem(3, getString(R.string.order_finished)));
        menus.add(new MenuItem(4, getString(R.string.order_service)));
        menuAdapter.submitList(menus);
        reqOrderList();
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
    }

    @Override
    protected boolean isFullScreen() {
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == subBinding.btnGoHome.getId()) {
            goHome();
        } else if (id == subBinding.btnPageNeedPay.getId()) {
            page = 0;
            reqOrderList();
        } else if (id == subBinding.btnPageNeedSend.getId()) {
            page = 1;
            reqOrderList();
        } else if (id == subBinding.btnPageNeedReceive.getId()) {
            page = 2;
            reqOrderList();
        } else if (id == subBinding.btnPageFinished.getId()) {
            page = 3;
            reqOrderList();
        } else if (id == subBinding.btnPageService.getId()) {
            page = 4;
            reqOrderList();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (subBinding.drawerOrders.isDrawerOpen(GravityCompat.END)) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_BACK) {
                subBinding.drawerOrders.closeDrawer(GravityCompat.END);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getChildFragmentManager().clearFragmentResultListener("meGroup");
    }

    private void reqOrderList() {
        TaskHelper.exec(new TaskHelper.Task() {
            List<ModelOrder.Item> orders;

            @Override
            public void execute() throws Exception {
                orders = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    ModelOrder.Item item = new ModelOrder.Item();
                    item.setId("" + i);
                    item.setName("namenamenamenamenamenamenamenamenamenamename" + i);
                    item.setPrice("100");
                    item.setAttr("[{\"num\":2}]");
                    item.setCover_pic("https://img.cbnlive.cn/web/uploads/image/store_1/503630a36565cb688fc94bb7380fd1fe9fb99cf5.jpg");
                    orders.add(item);
                }
            }

            @Override
            public void callback(Exception e) {
                orderAdapter.submitList(orders);
            }
        });
    }
}
