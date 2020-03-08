package ywl.study.securitydemo.util;

import org.springframework.security.core.token.DefaultToken;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ywl.study.securitydemo.entity.MyToken;

@Component
public class TokenProvider {
    private  String secretKey;
    private final int tokenValidity=30*60*1000;

//    public TokenProvider(String secretKey, int tokenValidity) {
//        this.secretKey = secretKey;
//        this.tokenValidity = tokenValidity;
//    }
//
    //生成token
    public MyToken createToken(UserDetails userDetails){
        long expires = System.currentTimeMillis() + 1000L * tokenValidity;
        String token =  computeSignature(userDetails, expires);
        return new MyToken(token, expires);
    }

    // 验证token
    public boolean validateToken(String authToken, UserDetails userDetails) {

        return true ;
    }

    // 从token中识别用户
    public String getUserNameFromToken(String authToken) {
        return authToken.substring(0,authToken.indexOf("token"));
    }

    public String computeSignature(UserDetails userDetails, long expires) {
        // 一些特有的信息组装 ,并结合某种加密活摘要算法
        return userDetails.getUsername()+"token";
    }





}
