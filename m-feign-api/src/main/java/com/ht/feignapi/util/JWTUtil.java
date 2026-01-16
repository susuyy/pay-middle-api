package com.ht.feignapi.util;

import com.ht.feignapi.config.JWTKeyConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JWTUtil {

    public static String SIGN_KEY = JWTKeyConfig.JWT_KEY;
    public static String BEARER = "bearer";
    public static Integer AUTH_LENGTH = Integer.valueOf(7);

    public static String BASE64_SECURITY = Base64.getEncoder().encodeToString(SIGN_KEY.getBytes(StandardCharsets.UTF_8));

    public static String getToken(String auth) {
        if ((auth != null) && (auth.length() > AUTH_LENGTH.intValue())) {
            String headStr = auth.substring(0, 6).toLowerCase();
            if (headStr.compareTo(BEARER) == 0) {
                auth = auth.substring(7);
            }
            return auth;
        }
        return null;
    }

    public static Claims parseJWT(String jsonWebToken) {
        try {
            return
                    (Claims) Jwts.parser()
                            .setSigningKey(Base64.getDecoder().decode(BASE64_SECURITY))
                            .parseClaimsJws(jsonWebToken)
                            .getBody();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public static void main(String[] args) {
        Claims claims = parseJWT("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJtc2FkbWluIiwic2NvcGUiOlsiYWxsIl0sImV4cCI6MTYxNjU2NzUzNiwiYXV0aG9yaXRpZXMiOlsibXNhZG1pbiJdLCJqdGkiOiI3MDgzNGM5NS0wZjljLTRmNDQtYWZhZC1lMzk0MDA2Mjg2ZjAiLCJjbGllbnRfaWQiOiJodWFsaWFudGlhbmFkbWluIiwiZW5oYW5jZSI6ImVuaGFuY2UgaW5mbyJ9.Hp6XqSqRieYp3a5gZuBKMB9Oa7uIX5ktyJh-ZNUuKRw");
        System.out.println(claims);
    }
}
