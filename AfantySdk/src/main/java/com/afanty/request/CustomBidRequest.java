package com.afanty.request;

public class CustomBidRequest {
    public static class DeviceInfo{
        private String ua;
        private Integer w;
        private Integer h;
        private Integer ppi;
        private String language;
        private String gaid;
        private String imei;
        private String cpuBit;
        private String abi;
        private String macAdress;

        public DeviceInfo appendUA(String ua){
            this.ua = ua;
            return this;
        }
        public DeviceInfo appendW(Integer w){
            this.w = w;
            return this;
        }
        public DeviceInfo appendH(Integer h){
            this.h = h;
            return this;
        }
        public DeviceInfo appendPPI(Integer ppi){
            this.ppi = ppi;
            return this;
        }
        public DeviceInfo appendLanguage(String language){
            this.language = language;
            return this;
        }
        public DeviceInfo appendGaid(String gaid){
            this.gaid = gaid;
            return this;
        }
        public DeviceInfo appendImei(String imei){
            this.imei = imei;
            return this;
        }
        public DeviceInfo appendCPUBit(String cpuBit){
            this.cpuBit = cpuBit;
            return this;
        }
        public DeviceInfo appendABI(String abi){
            this.abi = abi;
            return this;
        }
        public DeviceInfo appendMacAdress(String macAdress){
            this.macAdress = macAdress;
            return this;
        }

        public String getUA() {
            return ua;
        }

        public Integer getW() {
            return w;
        }

        public Integer getH() {
            return h;
        }

        public Integer getPPI() {
            return ppi;
        }

        public String getLanguage() {
            return language;
        }

        public String getGAID() {
            return gaid;
        }

        public String getImei() {
            return imei;
        }

        public String getCPUBit() {
            return cpuBit;
        }

        public String getABI() {
            return abi;
        }

        public String getMacAdress() {
            return macAdress;
        }
    }

    public static class App{
        private String pkg;
        private String appName;
        private String verName;
        private Integer verCode;
        private String publisher;

        public App appendPkg(String pkg){
            this.pkg = pkg;
            return this;
        }
        public App appendAppName(String appName){
            this.appName = appName;
            return this;
        }
        public App appendVerName(String verName){
            this.verName = verName;
            return this;
        }
        public App appendVerCode(Integer verCode){
            this.verCode = verCode;
            return this;
        }
        public App appendPublisher(String publisher){
            this.publisher = publisher;
            return this;
        }

        public String getPackageName() {
            return pkg;
        }

        public String getAppName() {
            return appName;
        }

        public String getVerName() {
            return verName;
        }

        public Integer getVerCode() {
            return verCode;
        }

        public String getPublisher() {
            return publisher;
        }
    }

}
