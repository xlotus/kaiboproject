package cn.cibn.kaibo.utils;

import android.text.TextUtils;

import com.tencent.mm.opensdk.diffdev.DiffDevOAuthFactory;
import com.tencent.mm.opensdk.diffdev.OAuthErrCode;
import com.tencent.mm.opensdk.diffdev.OAuthListener;
import com.tv.lib.core.Logger;
import com.tv.lib.core.algo.ShaUtil;
import com.tv.lib.core.lang.StringUtils;
import com.tv.lib.core.lang.thread.TaskHelper;

import cn.cibn.kaibo.model.ModelTicket;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.LiveMethod;

public class LoginHelper {
    private static final String TAG = "LoginHelper";
    private static final String APP_ID = "wx32fc2e022e5e3c3b"; //android.app.ttshop
    private static final String SCOPE = "snsapi_userinfo";

    public static void login(OAuthListener oAuthListener) {
        TaskHelper.exec(new TaskHelper.Task() {
            String ticket = "";
            @Override
            public void execute() throws Exception {
                ModelWrapper<ModelTicket> model = LiveMethod.getInstance().getTicket();
                if (model.isSuccess()) {
                    ticket = model.getData().getTicket();
                }
            }

            @Override
            public void callback(Exception e) {
                if (TextUtils.isEmpty(ticket)) {
                    if (oAuthListener != null) {
                        oAuthListener.onAuthFinish(OAuthErrCode.WechatAuth_Err_NormalErr, "");
                    }
                } else {
                    wxLogin(ticket, oAuthListener);
                }
            }
        });
    }

    private static void wxLogin(String ticket, OAuthListener oAuthListener) {
//        stopAuth();

        String noncestr = StringUtils.randomString(16);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = getSignature(noncestr, timestamp, ticket);
        Logger.d(TAG, "wxLogin " + noncestr + " | " + timestamp + " | " + signature);
        DiffDevOAuthFactory.getDiffDevOAuth().auth(APP_ID, SCOPE, noncestr, timestamp, signature, oAuthListener);
    }

    private static String getSignature(String noncestr, String timestamp, String sdk_ticket) {
        StringBuilder buf = new StringBuilder();
        buf.append("appid=").append(APP_ID);
        buf.append("&noncestr=").append(noncestr);
        buf.append("&sdk_ticket=").append(sdk_ticket);
        buf.append("&timestamp=").append(timestamp);
        String signature = "";
        try {
            signature = ShaUtil.SHA1_Hex(buf.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    public static void stopAuth() {
        DiffDevOAuthFactory.getDiffDevOAuth().stopAuth();
        DiffDevOAuthFactory.getDiffDevOAuth().removeAllListeners();
    }
}
