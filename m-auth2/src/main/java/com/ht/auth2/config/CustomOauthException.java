package com.ht.auth2.config;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

@JsonSerialize(using = CustomOauthExceptionSerializer.class)
public class CustomOauthException extends OAuth2Exception {
    private String oAuth2ErrorCode;

    private int httpErrorCode;

    public CustomOauthException(String msg, String oAuth2ErrorCode, int httpErrorCode) {
        super(msg);
        this.oAuth2ErrorCode = oAuth2ErrorCode;
        this.httpErrorCode = httpErrorCode;
    }

    public String getoAuth2ErrorCode() {
        return oAuth2ErrorCode;
    }

    public int getHttpErrorCode() {
        return httpErrorCode;
    }
}
