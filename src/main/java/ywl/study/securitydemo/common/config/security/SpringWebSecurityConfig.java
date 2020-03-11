package ywl.study.securitydemo.common.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ywl.study.securitydemo.filter.security.JwtLoginFilter;
import ywl.study.securitydemo.filter.security.JwtTokenFilter;

/**
 * Spring Security 配置类
 * 说明：
 * Spring Security默认是禁用注解的，要想开启注解，
 * 需要在继承WebSecurityConfigurerAdapter的类上加@EnableGlobalMethodSecurity注解
 * SpringWebSecurityConfig configure(AuthenticationManagerBuilder auth)
 * SpringWebSecurityConfig configure(HttpSecurity http)
 * SpringWebSecurityConfig configure(WebSecurity web)
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringWebSecurityConfig  extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource;
    @Autowired
    private AccessDecisionManager accessDecisionManager;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("启动后执行序号 2 --SpringWebSecurityConfig configure(HttpSecurity http)");
        //自定义登录拦截器
        JwtLoginFilter jwtLoginFilter=new JwtLoginFilter();
        jwtLoginFilter.setAuthenticationManager(authenticationManagerBean());
        jwtLoginFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        jwtLoginFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

        JwtTokenFilter jwtTokenFilter=new JwtTokenFilter();

        // 使用自定义验证实现器
        JwtAuthenticationProvider jwtAuthenticationProvider=new JwtAuthenticationProvider(userDetailsService,passwordEncoder);

        //登录验证信息
        http.authenticationProvider(jwtAuthenticationProvider)
                .authorizeRequests()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setSecurityMetadataSource(filterInvocationSecurityMetadataSource);
                        object.setAccessDecisionManager(accessDecisionManager);
                        return object;
                    }
                })
                .anyRequest().authenticated()
                .and()
                .formLogin();

        //jwt拦截器配置
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                //jwtLoginFilter 执行完，执行UsernamePasswordAuthenticationFilter拦截器，执行后执行jwtTokenFilter
                .addFilterAt(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtTokenFilter,JwtLoginFilter.class);

        //权限处理信息
        http.exceptionHandling()
                //   用来解决认证过的用户访问无权限资源时的异常
                .accessDeniedHandler(accessDeniedHandler)
                // 用来解决匿名用户访问无权限资源时的异常
                .authenticationEntryPoint(authenticationEntryPoint);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("启动后执行序号 1 --SpringWebSecurityConfig configure(AuthenticationManagerBuilder auth)");
        super.configure(auth);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        System.out.println("启动后执行序号 3---SpringWebSecurityConfig configure(WebSecurity web)");
        web.ignoring().mvcMatchers("/static/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
