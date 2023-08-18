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
    protected Object connect(String method, String url) throws ConnectionException {
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
//            URL u = new URL(url);
//            URLConnection urlConnection = u.openConnection();
//            HttpURLConnection connection = (HttpURLConnection) urlConnection;
//            // 设置连接超时时间, 值必须大于0，设置为0表示不超时 单位为“毫秒”
//            connection.setConnectTimeout(30000);
//            // 设置读超时时间, 值必须大于0，设置为0表示不超时 单位毫秒
//            connection.setReadTimeout(60000);
//            connection.setRequestMethod(method);
//            // 设置请求类型为 application/json
//            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            // 设置可接受的数据类型
//            connection.setRequestProperty("Accept", "*/*");
//            // 设置保持长链接
//            connection.setRequestProperty("Connection", "Keep-Alive");
//
//            connection.connect();
//
//            int code = connection.getResponseCode();
//            if (code != 200) {
//                throw new ConnectionException(code, "connection error");
//            }
//
//            // 获取输入流
//            inputStream = connection.getInputStream();
//            // 定义一个临时字节输出流
//            baos = new ByteArrayOutputStream();
//            // 开始读取数据
//            byte[] buffer = new byte[256];
//            int len = 0;
//            while ((len = inputStream.read(buffer)) > 0) {
//                baos.write(buffer, 0, len);
//            }
//            String content = new String(baos.toByteArray(), StandardCharsets.UTF_8);
//            String content = "{\"code\":0,\"data\":{\"list\":[{\"id\":\"3\",\"title\":\"直播间测试\",\"cover_img\":\"http://imgduoshanghu.oeob.net/web/uploads/image/store_1/872b8a80d1fad1e252db4ee8c207b7f27ea25b47.png\",\"back_img\":\"http://imgduoshanghu.oeob.net/web/uploads/image/store_1/872b8a80d1fad1e252db4ee8c207b7f27ea25b47.png\",\"anchor_id\":\"24\",\"play_addr\":\"http://ggplay.oeob.net/live/2384c09464e29cd7b28fb61161da5d6a.flv?txSecret=a9905e2c02acebdd2cfb435eea60a136&txTime=642BEB17\",\"name\":\"暗示法1\",\"header_bg\":\"\"}],\"row_count\":\"1\",\"page_count\":1}}";
            UrlResponse response = HttpUtils.get(url, null, 30000, 30000);
            if (response.getStatusCode() == Constants.CODE_SUCCESS && response.getContent() != null) {
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
