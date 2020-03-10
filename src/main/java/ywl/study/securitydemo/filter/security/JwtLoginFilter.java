package ywl.study.securitydemo.filter.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import ywl.study.securitydemo.entity.security.JwtLoginToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户登录验证拦截器--执行顺序在UsernamePasswordAuthenticationFilter 拦截器之后
 */
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * 拦截逻辑
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtLoginFilter attemptAuthentication");
        String userName=request.getParameter("username");
        String password=request.getParameter("password");
        //创建未认证的凭证，setAuthenticated(false),凭证中的主体principal是用户名
        JwtLoginToken jwtLoginToken=new JwtLoginToken(userName,password);
        //将认证详情(ip,sessionId)写到凭证
        jwtLoginToken.setDetails(new WebAuthenticationDetails(request));
        //AuthenticationManager获取受支持的AuthenticationProvider（这里是JwtAuthenticationProvider）
        //生成已经认证的凭证，此时凭证中的主体是userDetails---这里会委托给AuthenticationProvider实现类来验证
        //即 跳转到 JwtAuthenticationProvider.authenticate 方法中认证
        Authentication authenticatedToken=this.getAuthenticationManager().authenticate(jwtLoginToken);
        return authenticatedToken;
    }
}
