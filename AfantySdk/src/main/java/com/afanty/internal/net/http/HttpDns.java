package com.afanty.internal.net.http;

import static java.net.InetAddress.getAllByName;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.afanty.internal.internal.AdConstants;
import com.afanty.utils.ContextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Dns;

public class HttpDns implements Dns {

    public static final String DNS_SOURCE_DEFAULT = "def";
    public static final String DNS_SOURCE_CONFIG = "cloud";

    public HttpDns() {
    }

    @NonNull
    @Override
    public List<InetAddress> lookup(@NonNull String hostname) throws UnknownHostException {
        List<InetAddress> result = new ArrayList<>();
        try {
            List<DnsInfo> dnsInfoList = new ArrayList<>();
            if (dnsInfoList.size() <= 0) {
                return Dns.SYSTEM.lookup(hostname);
            }

            String ips = "";
            String source = DNS_SOURCE_DEFAULT;
            for (DnsInfo info : dnsInfoList) {
                if (info.getHost().contains(hostname)) {
                    ips = info.getIps();
                    source = info.getSource();
                    break;
                }
            }

            if (TextUtils.isEmpty(ips)) {
                return Dns.SYSTEM.lookup(hostname);
            }
            String[] ipsArray = ips.split(",");
            if (ipsArray.length == 0) {
                return Dns.SYSTEM.lookup(hostname);
            }
            for (String ip : ipsArray) {
                result.addAll(Arrays.asList(getAllByName(ip)));
            }
            Collections.shuffle(result);

            return result;
        } catch (Exception ignore) { }
        result.addAll(Dns.SYSTEM.lookup(hostname));
        return result;
    }

    /**
     * 获取云控dns解析配置
     */
    private List<DnsInfo> getDnsInfoList() {
        List<DnsInfo> dnsInfoList = new ArrayList<>();
        String defaultIpConfig = "";
        String jsonData = "";
        String source = DNS_SOURCE_CONFIG;

        if (TextUtils.isEmpty(jsonData)) {
            if (TextUtils.isEmpty(defaultIpConfig))
                return dnsInfoList;

            jsonData = defaultIpConfig;
            source = DNS_SOURCE_DEFAULT;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            if (!jsonObject.has(AdConstants.Config.AD_DNS_SWITCH) || jsonObject.optBoolean(AdConstants.Config.AD_DNS_SWITCH)) {
                if (!jsonObject.has(AdConstants.Config.AD_DNS_LIST)) {
                    jsonObject = new JSONObject(defaultIpConfig);
                }
            } else {
                return dnsInfoList;
            }

            JSONArray jsonArray = jsonObject.getJSONArray(AdConstants.Config.AD_DNS_LIST);
            for (int i = 0; i < jsonArray.length(); i++) {
                DnsInfo dnsInfo = new DnsInfo();
                dnsInfo.setHost(jsonArray.getJSONObject(i).optString("host"));
                dnsInfo.setIps(jsonArray.getJSONObject(i).optString("ips"));
                dnsInfo.setSource(source);
                dnsInfoList.add(dnsInfo);
            }
        } catch (Exception ignore) { }
        return dnsInfoList;
    }

    private static class DnsInfo {
        private String host;
        private String ips;
        private String source;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getIps() {
            return ips;
        }

        public void setIps(String ips) {
            this.ips = ips;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getSource() {
            return source;
        }

        @NonNull
        @Override
        public String toString() {
            return "DnsInfo{" +
                    "host='" + host + '\'' +
                    ", ips='" + ips + '\'' +
                    '}';
        }
    }
}
