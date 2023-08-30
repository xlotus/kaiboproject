package cn.cibn.kaibo;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tv.lib.core.lang.thread.TaskHelper;

import cn.cibn.kaibo.model.ModelUser;
import cn.cibn.kaibo.settings.LiveSettings;

public class UserManager {
    private static UserManager instance = new UserManager();

    private String token;
    private ModelUser userInfo;

    private UserManager() {

    }

    public static UserManager getInstance() {
        return instance;
    }

    public void init() {
        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {
                token = LiveSettings.getToken();
                String userInfoStr = LiveSettings.getUserInfo();
                if (!TextUtils.isEmpty(userInfoStr)) {
                    userInfo = new Gson().fromJson(userInfoStr, ModelUser.class);
                }
            }

            @Override
            public void callback(Exception e) {

            }
        });
    }

    public void setToken(String token) {
        this.token = token;
        saveToken();
    }

    public void setUserInfo(ModelUser userInfo) {
        this.userInfo = userInfo;
        saveUserInfo();
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(token);
    }

    public ModelUser getUserInfo() {
        return userInfo;
    }

    public void clear() {
        this.token = "";

        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {
                LiveSettings.setToken("");
                LiveSettings.setUserInfo("{}");
            }

            @Override
            public void callback(Exception e) {

            }
        });
    }

    private void saveToken() {
        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {
                LiveSettings.setToken(token);
            }

            @Override
            public void callback(Exception e) {

            }
        });
    }

    private void saveUserInfo() {
        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {
                String userInfoStr = new Gson().toJson(userInfo);
                LiveSettings.setUserInfo(userInfoStr);
            }

            @Override
            public void callback(Exception e) {

            }
        });
    }
}
