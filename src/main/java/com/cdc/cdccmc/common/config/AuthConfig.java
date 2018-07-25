package com.cdc.cdccmc.common.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mjl-pc on 2017/9/5.
 */
@Configuration
@ConfigurationProperties(prefix = "auth")
public class AuthConfig {
	/**
	 * 访问鉴权的url
	 */
    private String url; 
    /**
     * 租户账号
     */
    private String corpUuid;
    /**
     * 应用uuid
     */
    private String appUuid;
    /**
     * app_secret
     */
    private String appSecret;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCorpUuid() {
        return corpUuid;
    }

    public void setCorpUuid(String corpUuid) {
        this.corpUuid = corpUuid;
    }

	public String getAppUuid() {
		return appUuid;
	}

	public void setAppUuid(String appUuid) {
		this.appUuid = appUuid;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
    
    
}
