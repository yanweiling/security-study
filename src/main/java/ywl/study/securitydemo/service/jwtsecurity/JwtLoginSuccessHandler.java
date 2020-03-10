package ywl.study.securitydemo.service.jwtsecurity;

import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ywl.study.securitydemo.common.Constants;
import ywl.study.securitydemo.common.JsonResult;
import ywl.study.securitydemo.entity.security.JwtUserDetails;
import ywl.study.securitydemo.utils.JwtUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登陆验证成功处理
 */
@Component
public class JwtLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        System.out.println("JwtLoginSuccessHandler ....");
        response.setContentType("application/json;charset=UTF-8");
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        String json = JSON.toJSONString(jwtUserDetails);
        String jwtToken = JwtUtils.createJwtToken(json, Constants.DEFAULT_TOKEN_TIME_MS);
        //签发token
        JsonResult<String> jsonResult = new JsonResult<>();
        jsonResult.setSuccess(jwtToken);
        response.getWriter().write(JSON.toJSONString(jsonResult));
    }
}
