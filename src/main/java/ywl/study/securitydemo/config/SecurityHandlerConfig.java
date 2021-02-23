package ywl.study.securitydemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityHandlerConfig {
    /**
     * 未登录，返回401
     *
     * @return
     */
//    @Bean
//    public AuthenticationEntryPoint authenticationEntryPoint() {
//        return new AuthenticationEntryPoint() {
//
//            @Override
//            public void commence(HttpServletRequest request, HttpServletResponse response,
//                                 AuthenticationException authException) throws IOException, ServletException {
//                response.setCharacterEncoding("UTF-8");
//                response.getWriter().write("{\"msg\":\"访问由于凭据无效被拒绝\",\"code\":\"401\"}");
//                response.getWriter().flush();
//                // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "访问由于凭据无效被拒绝");
//            }
//        };
//    }

    /**
     * 未登录，返回403
     *
     * @return
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                //当用户在没有授权的情况下访问受保护的REST资源时，将调用此方法发送403 Forbidden响应
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"msg\":\"禁止执行访问\",\"code\":\"403\"}");
                response.getWriter().flush();
                // response.sendError(HttpServletResponse.SC_FORBIDDEN, "禁止执行访问 ");
            }
        };

    }
}
