package fnozoszzt.pdd.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    private static String CHARSET = "UTF-8";

    public static String doGet(String host, String path, Map<String, String> headers, Map<String, String> querys) throws Exception {
        HttpClient httpClient = getHtttpClient();
        String url = buildUrl(host, path, querys);

        HttpGet request = new HttpGet(url);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        HttpResponse response = httpClient.execute(request);

        return getBody(url, response);
    }

    public static String doPost(String host, String path, Map<String, String> headers, Map<String, String> querys, Map<String, String> bodys)
            throws Exception {
        HttpClient httpClient = getHtttpClient();
        String url = buildUrl(host, path, querys);

        HttpPost request = new HttpPost(url);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }
        if (bodys != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

            for (String key : bodys.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, bodys.get(key)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
            formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            request.setEntity(formEntity);
        }

        HttpResponse response = httpClient.execute(request);
        return getBody(url, response);
    }

    public static String doPost(String host, String path, Map<String, String> headers, Map<String, String> querys, String body) throws Exception {
        HttpClient httpClient = getHtttpClient();
        String url = buildUrl(host, path, querys);

        HttpPost request = new HttpPost(url);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }
        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, "utf-8"));
        }

        HttpResponse response = httpClient.execute(request);
        return getBody(url, response);
    }


    public static String doPost(String host, String path, Map<String, String> headers, Map<String, String> querys, byte[] body) throws Exception {
        HttpClient httpClient = getHtttpClient();
        String url = buildUrl(host, path, querys);

        HttpPost request = new HttpPost(url);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }
        if (body != null) {
            request.setEntity(new ByteArrayEntity(body));
        }

        HttpResponse response = httpClient.execute(request);
        return getBody(url, response);
    }

    public static String doPut(String host, String path, Map<String, String> headers, Map<String, String> querys, String body) throws Exception {
        HttpClient httpClient = getHtttpClient();
        String url = buildUrl(host, path, querys);

        HttpPut request = new HttpPut(url);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }
        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, "utf-8"));
        }

        HttpResponse response = httpClient.execute(request);
        return getBody(url, response);
    }

    public static String doPut(String host, String path, Map<String, String> headers, Map<String, String> querys, byte[] body) throws Exception {
        HttpClient httpClient = getHtttpClient();
        String url = buildUrl(host, path, querys);

        HttpPut request = new HttpPut(url);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }
        if (body != null) {
            request.setEntity(new ByteArrayEntity(body));
        }

        HttpResponse response = httpClient.execute(request);
        return getBody(url, response);
    }

    public static String doDelete(String host, String path, Map<String, String> headers, Map<String, String> querys) throws Exception {
        HttpClient httpClient = getHtttpClient();
        String url = buildUrl(host, path, querys);

        HttpDelete request = new HttpDelete(url);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        HttpResponse response = httpClient.execute(request);
        return getBody(url, response);
    }

    private static String getBody(String url, HttpResponse httpResponse) throws Exception {

        if (httpResponse == null) {
            throw new Exception("httpresponse is null, request is " + url);
        }

        String body = new JSONObject().toJSONString();
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            body = EntityUtils.toString(entity, CHARSET);
        }

        int httpCode = httpResponse.getStatusLine().getStatusCode();
        if (httpCode / HttpURLConnection.HTTP_OK != 1) {
            throw new Exception(String.format("http code is %d, request is %s, response is %s",
                    httpCode, url, body));
        }

        return body;
    }

    private static String buildUrl(String host, String path, Map<String, String> querys) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();
        if (!StringUtils.isBlank(host)) {
            sbUrl.append(host);
        }
        if (!StringUtils.isBlank(path)) {
            sbUrl.append(path);
        }
        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append("=");
                        sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }
        return sbUrl.toString();
    }


    private static HttpClient getHtttpClient() {
        HttpClient httpClient = HttpConnectionManager.getHttpClient();

        return httpClient;
    }

}