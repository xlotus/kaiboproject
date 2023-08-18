package cn.cibn.kaibo.data;

import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.core.lang.thread.TaskHelper;

import java.util.Stack;

import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.model.ModelLive;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.LiveMethod;

public class RecommendModel {
    public static final int PAGE_FIRST = 1;
    private static RecommendModel instance = new RecommendModel();

    private Stack<ModelLive.Item> historyStack = new Stack<>(); //历史栈
    private Stack<ModelLive.Item> backupStack = new Stack<>(); //上翻时，从historyStack出栈，放入此备份栈

    private int requestingPage = -1;

    private RecommendModel() {

    }

    public static RecommendModel getInstance() {
        return instance;
    }

    public boolean hasData() {
        return !historyStack.isEmpty() || !backupStack.isEmpty();
    }

    public ModelLive.Item getNext() {
        if (!backupStack.empty()) {
            return backupStack.pop();
        }
        reqLiveList(PAGE_FIRST + historyStack.size());
        return null;
    }

    public ModelLive.Item getPrev() {
        if (historyStack.size() == 1) {
            return null;
        }
        backupStack.push(historyStack.pop());
        return historyStack.peek();
    }

    public ModelLive.Item getCurrent() {
        if (!historyStack.isEmpty()) {
            return historyStack.peek();
        }
        return null;
    }

    public void addHistory(ModelLive.Item item) {
        historyStack.push(item);
    }

    public void reqLiveList(int page) {
        if (page == requestingPage) {
            return;
        }

        requestingPage = page;
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<ModelLive> modelLive;

            @Override
            public void execute() throws Exception {
                modelLive = LiveMethod.getInstance().getLiveList(page, 1);
            }

            @Override
            public void callback(Exception e) {
                if (modelLive != null && modelLive.getData() != null && !modelLive.getData().getList().isEmpty()) {
                    ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_REQUEST_PLAY, modelLive.getData().getList().get(0));
                }
                requestingPage = -1;
            }
        });
    }
}
