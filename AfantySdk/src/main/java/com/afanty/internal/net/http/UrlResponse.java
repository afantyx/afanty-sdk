package com.afanty.internal.net.http;

import android.os.Build;

import com.afanty.utils.CommonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class UrlResponse {
    private Map<String, List<String>> headers;

    private String content;
    private int statusCode;
    private String statusMessage;

    public UrlResponse(Response response) throws IOException {
        headers = response.headers().toMultimap();
        statusCode = response.code();
        statusMessage = response.message();

        try (ResponseBody body = response.body()) {
            InputStream inputStream = body.byteStream();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                content = inputStreamToString(inputStream);
            }
        } catch (NullPointerException e) {
            throw new IOException("response body is null");
        } catch (Exception e) {
            throw new IOException("Exception occur in response body");
        }
    }

    public UrlResponse(HttpURLConnection conn) throws IOException {
        headers = conn.getHeaderFields();
        statusCode = conn.getResponseCode();
        statusMessage = conn.getResponseMessage();

        InputStream input = null;
        try {
            try {
                input = conn.getInputStream();
            } catch (IOException e) {
                input = conn.getErrorStream();
            }
            if (input != null)
                content = CommonUtils.inputStreamToString(input, true);
        } finally {
            CommonUtils.close(input);
        }
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UrlResponse [statusCode=").append(statusCode).append(", statusMessage=")
                .append(statusMessage).append(",content=").append(content).append("]");
        return builder.toString();
    }

    private String inputStreamToString(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            sb.append(line);
        br.close();
        return sb.toString();
    }

}
