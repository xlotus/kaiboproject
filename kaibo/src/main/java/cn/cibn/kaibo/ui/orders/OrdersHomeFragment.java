package cn.cibn.kaibo.ui.orders;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.RecyclerView;

import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.core.utils.ui.SafeToast;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.adapter.MenuAdapter;
import cn.cibn.kaibo.adapter.OrderAdapter;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentOrdersHomeBinding;
import cn.cibn.kaibo.model.MenuItem;
import cn.cibn.kaibo.model.ModelOrder;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.model.OrderAction;
import cn.cibn.kaibo.network.OrderMethod;
import cn.cibn.kaibo.ui.BaseStackFragment;

public class OrdersHomeFragment extends BaseStackFragment<FragmentOrdersHomeBinding> implements View.OnClickListener {
    private static final String TAG = "OrdersFragment";
    private static final int PAGE_FIRST = 1;
    private OrderDetailFragment orderDetailFragment;

    private OrderAdapter orderAdapter;
    private MenuAdapter menuAdapter;

    private int orderStatus = 0;

    private List<ModelOrder.Item> orderList = new ArrayList<>();
    private int curPage;
    private int total;
    private int loadingPage;

    public static OrdersHomeFragment createInstance(int orderStatus) {
        Bundle args = new Bundle();
        args.putInt("orderStatus", orderStatus);
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
                orderStatus = item.getId();
                reqOrderList(PAGE_FIRST);
            }
        });

        orderAdapter = new OrderAdapter();
        subBinding.recyclerOrderList.setNumColumns(2);
        subBinding.recyclerOrderList.setAdapter(orderAdapter);
        orderAdapter.submitList(orderList);
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
                    reqConfirm(item);
                }
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            orderStatus = args.getInt("orderStatus", 0);
        }

        subBinding.recyclerOrderMenu.post(new Runnable() {
            @Override
            public void run() {
                if (subBinding.recyclerOrderMenu.getChildCount() > orderStatus) {
                    subBinding.recyclerOrderMenu.getChildAt(orderStatus).requestFocus();
                }
            }
        });
        updateView();
    }

    @Override
    protected void updateView() {
        if (subBinding == null) {
            return;
        }
        if (ConfigModel.getInstance().isGrayMode()) {
            subBinding.btnGoHome.setBackgroundResource(R.drawable.bg_menu_item_search_selector_gray);
        } else {
            subBinding.btnGoHome.setBackgroundResource(R.drawable.bg_menu_item_search_selector);
        }
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
        reqOrderList(PAGE_FIRST);
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
            orderStatus = 0;
            reqOrderList(PAGE_FIRST);
        } else if (id == subBinding.btnPageNeedSend.getId()) {
            orderStatus = 1;
            reqOrderList(PAGE_FIRST);
        } else if (id == subBinding.btnPageNeedReceive.getId()) {
            orderStatus = 2;
            reqOrderList(PAGE_FIRST);
        } else if (id == subBinding.btnPageFinished.getId()) {
            orderStatus = 3;
            reqOrderList(PAGE_FIRST);
        } else if (id == subBinding.btnPageService.getId()) {
            orderStatus = 4;
            reqOrderList(PAGE_FIRST);
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

    private void reqOrderList(int page) {
        if (loadingPage == page) {
            return;
        }
        loadingPage = page;
        if (page == PAGE_FIRST) {
            total = 0;
            orderList.clear();
            orderAdapter.notifyDataSetChanged();
        }
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<ModelOrder> model;

            @Override
            public void execute() throws Exception {
                model = OrderMethod.getInstance().reqOrderList(orderStatus, page, 10);
            }

            @Override
            public void callback(Exception e) {
                loadingPage = -1;
                if (model != null && model.isSuccess() && model.getData() != null) {
                    curPage = page;
                    total = model.getData().getRow_count();
                    if (page == PAGE_FIRST) {
                        orderList.clear();
                    }
                    if (model.getData().getList() != null) {
                        orderList.addAll(model.getData().getList());
                    }
                    orderAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void reqConfirm(ModelOrder.Item item) {
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<String> model;
            @Override
            public void execute() throws Exception {
                model = OrderMethod.getInstance().reqOrderConfirm(item.getOrderId());
            }

            @Override
            public void callback(Exception e) {
                if (model != null) {
                    if (model.isSuccess()) {
//                        reqOrderList(PAGE_FIRST);
                        int position = orderList.indexOf(item);
                        orderList.remove(item);
                        orderAdapter.notifyItemRemoved(position);
                        SafeToast.showToast("已确认收货", Toast.LENGTH_SHORT);
                    } else {
                        SafeToast.showToast(model.getErrMsg(), Toast.LENGTH_SHORT);
                    }
                }
            }
        });
    }

    private OnChildViewHolderSelectedListener onChildViewHolderSelectedListener = new OnChildViewHolderSelectedListener() {
        @Override
        public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
            int count = orderAdapter.getItemCount();
            if (count - position < 4 && count < total) {
                reqOrderList(curPage + 1);
            }
        }
    };
}
