package com.tiktok.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于JWT创建Token.
 *
 * @author lzc
 * @version 1.0
 */
public class JWTUtils {

    public static String createJWT(String userId, String password) {
        Map<String, Object> headers = new HashMap<>();
        Date issureDate = new Date();
        Date expireDate = new Date(issureDate.getTime() + 3600 * 24 * 1000);
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");
        return JWT.create().withHeader(headers) // 头部信息
                .withIssuer("Lzc") // 签发人
                .withClaim("userId", userId)    // 中间部分
                .withIssuedAt(issureDate)   // 签发时间
                .withExpiresAt(expireDate)  // 过期时间
                .sign(Algorithm.HMAC256(password));   // 尾部
    }
}