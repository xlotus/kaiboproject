package cn.cibn.kaibo.network;

import android.text.TextUtils;

import com.tv.lib.core.net.HttpUtils;
import com.tv.lib.core.net.UrlResponse;
import com.tv.lib.core.utils.CloseUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Map;

public class Connection {
    public static final String GET = "GET";
    public static final String POST = "POST";

    protected Object connect(String method, String url, Map<String, String> params) throws ConnectionException {
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            UrlResponse response = null;
            int connectTimeout = 30000;
            int readTimeout = 30000;
            if (GET.equals(method)) {
                String fullUrl = buildParams(url, params);
                response = HttpUtils.get(fullUrl, null, 30000, 30000);
            } else if (POST.equalsIgnoreCase(method)) {
                response = HttpUtils.posFormData(url, null, params, connectTimeout, readTimeout);
            }
            if (response != null && response.getStatusCode() == Constants.CODE_SUCCESS && response.getContent() != null) {
                JSONObject jsonObject = new JSONObject(response.getContent());
                int resCode = jsonObject.optInt("code");
                if (resCode != 0) {
                    throw new ConnectionException(resCode, jsonObject.optString("msg"));
                }
                return jsonObject.get("data").toString();
            }
        }
        catch (UnknownHostException e) {
//            e.printStackTrace();
            throw new ConnectionException(Constants.CODE_FAIL, "no net work");
        }
        catch (Exception e) {
//            e.printStackTrace();
            throw new ConnectionException(Constants.CODE_FAIL, "unknown error");
        } finally {
            // 关闭输入、输出流
            CloseUtils.close(inputStream);
            CloseUtils.close(baos);
        }
        return "";
    }

    protected String buildParams(String baseUrl, Map<String, String> params) {
        if (TextUtils.isEmpty(baseUrl)) {
            return "";
        }
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        StringBuilder builder = new StringBuilder(baseUrl);
        if (!baseUrl.contains("?")) {
            builder.append("?");
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!TextUtils.isEmpty(entry.getKey()) && !TextUtils.isEmpty(entry.getValue())) {
                if (builder.charAt(builder.length() - 1) != '?') {
                    builder.append('&');
                }
                builder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        return builder.toString();
    }

}
