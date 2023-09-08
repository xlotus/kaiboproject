package cn.cibn.kaibo.ui.settings;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.frame.adapter.ListBindingAdapter;

import java.util.List;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentQuestionsBinding;
import cn.cibn.kaibo.databinding.ItemProblemBinding;
import cn.cibn.kaibo.model.ModelQuestions;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.SettingMethod;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class QuestionsFragment extends KbBaseFragment<FragmentQuestionsBinding> {

    private ProblemsAdapter problemAdapter;

    public static QuestionsFragment createInstance() {
        return new QuestionsFragment();
    }

    @Override
    protected FragmentQuestionsBinding createBinding(LayoutInflater inflater) {
        return FragmentQuestionsBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        problemAdapter = new ProblemsAdapter();
        binding.recyclerProblems.setAdapter(problemAdapter);
    }

    @Override
    protected void initData() {
        reqQuestions();
//        List<ModelQuestions.Item> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            ModelQuestions.Item item = new ModelQuestions.Item();
//            item.setId("" + i);
//            item.setTitle("为什么无法参加新用户首页限时特惠活动？");
//            item.setAnswer("1、手机要求没有在大众点评购买过。\n" +
//                    "2：登陆的账号要求没有大众点评购买记录。\n" +
//                    "3、付款的账号要求没有在大众点评使用过。\n" +
//                    "\n" +
//                    "大众点评新用户专享福利：\n" +
//                    "1、大众点评新用户专享新人10元优惠券免费领，下载大众点评APP，注册登录即可获得新人优惠券\n" +
//                    "2、大众点评新用户专享免费吃喝玩乐！新用户在大众点评手机客户端参与“免费吃喝玩乐”活动，0元免费领\n" +
//                    "3、大众点评新用户专享1分钱刮大奖");
//            list.add(item);
//        }
//        problemAdapter.submitList(list);
    }

    @Override
    protected void updateView() {

    }

    @Override
    public void requestFocus() {
        if (binding != null) {
            binding.recyclerProblems.post(new Runnable() {
                @Override
                public void run() {
                    problemAdapter.requestFocus();
                }
            });
        }
    }

    private void reqQuestions() {
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<List<ModelQuestions>> model;

            @Override
            public void execute() throws Exception {
                model = SettingMethod.getInstance().reqQuestions();
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess() && model.getData() != null) {
                    problemAdapter.submitList(model.getData());
                }
            }
        });
    }

    private static class ProblemsAdapter extends ListBindingAdapter<ModelQuestions, ItemProblemBinding> {
        private View lastSelectedView = null;

        private ModelQuestions openedItem;

        public ProblemsAdapter() {
            super(new ProblemDiffCallback());
        }

        @Override
        public ItemProblemBinding createBinding(ViewGroup parent) {
            return ItemProblemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        }

        @Override
        public void onBindViewHolder(ModelQuestions data, ItemProblemBinding binding, int position) {
            binding.tvProblemTitle.setText(data.getTitle());
            binding.tvProblemAnswer.setText(data.getAnswer());
            boolean opened = data == openedItem;
            binding.tvProblemAnswer.setVisibility(opened ? View.VISIBLE : View.GONE);
            setStyle(binding, binding.getRoot().hasFocus(), opened);
            if (position == 0) {
                lastSelectedView = binding.getRoot();
            }
        }

        @Override
        protected void onItemClick(View view, ModelQuestions item) {
            if (openedItem == item) {
                openedItem = null;
            } else {
                openedItem = item;
            }
            notifyDataSetChanged();
        }

        @Override
        protected void onItemFocusChanged(ItemProblemBinding binding, boolean hasFocus) {
            boolean opened = openedItem == binding.getRoot().getTag();
            setStyle(binding, hasFocus, opened);
        }

        public void requestFocus() {
            if (lastSelectedView != null) {
                lastSelectedView.requestFocus();
            }
        }

        private void setStyle(ItemProblemBinding binding, boolean focus, boolean opened) {
            if (focus) {
                if (ConfigModel.getInstance().isGrayMode()) {
                    binding.tvProblemTitle.setBackgroundResource(opened ? R.drawable.shape_top4_p_gray : R.drawable.shape_4_p_gray);
                } else {
                    binding.tvProblemTitle.setBackgroundResource(opened ? R.drawable.shape_top4_p : R.drawable.shape_4_p);
                }
            } else {
                binding.tvProblemTitle.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    private static class ProblemDiffCallback extends DiffUtil.ItemCallback<ModelQuestions> {

        @Override
        public boolean areItemsTheSame(@NonNull ModelQuestions oldItem, @NonNull ModelQuestions newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull ModelQuestions oldItem, @NonNull ModelQuestions newItem) {
            return oldItem.equals(newItem);
        }
    }

}
