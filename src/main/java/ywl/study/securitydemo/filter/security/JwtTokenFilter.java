package ywl.study.securitydemo.filter.security;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ywl.study.securitydemo.common.JsonResponseStatus;
import ywl.study.securitydemo.common.JsonResult;
import ywl.study.securitydemo.entity.security.JwtLoginToken;
import ywl.study.securitydemo.entity.security.JwtUserDetails;
import ywl.study.securitydemo.utils.JwtUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Token 有效性验证拦截器
 */
public class JwtTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JwtTokenFilter doFilterInternal");
        try{
           String token=httpServletRequest.getHeader("Authentication");
           if(StringUtils.isEmpty(token)){
               httpServletResponse.setContentType("application/json;charset=UTF-8");
               JsonResult<String> jsonResult = new JsonResult<>();
               jsonResult.setFail(JsonResponseStatus.TokenFail.getCode(), "未登录");
               httpServletResponse.getWriter().write(JSON.toJSONString(jsonResult));
               return;
           }
           Claims claims=JwtUtils.parseJWT(token);
           if(JwtUtils.isTokenExpired(claims)){
               httpServletResponse.setContentType("application/json;charset=UTF-8");
               JsonResult<String> jsonResult = new JsonResult<>();
               jsonResult.setFail(JsonResponseStatus.TokenFail.getCode(), "登陆失效，请重新登陆");
               httpServletResponse.getWriter().write(JSON.toJSONString(jsonResult));
               return;
           }
           JwtUserDetails user=JSON.parseObject(claims.get("userDetails",String.class), JwtUserDetails.class);
           JwtLoginToken jwtLoginToken=new JwtLoginToken(user,"",user.getAuthorities());
           jwtLoginToken.setDetails(new WebAuthenticationDetails(httpServletRequest));
           SecurityContextHolder.getContext().setAuthentication(jwtLoginToken);
           filterChain.doFilter(httpServletRequest,httpServletResponse);

       }catch (Exception e){
           throw new BadCredentialsException("凭证登录失效，请重新登录");
       }
    }
}
