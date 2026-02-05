package com.gcc.victoriapublichallEvent.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private String title;
    private String baseUrl;
    private String getOtpUrl;
    private String erpUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getGetOtpUrl() {
        return getOtpUrl;
    }

    public void setGetOtpUrl(String getOtpUrl) {
        this.getOtpUrl = getOtpUrl;
    }

    public String getErpUrl() {
        return erpUrl;
    }

    public void setErpUrl(String erpUrl) {
        this.erpUrl = erpUrl;
    }

}
