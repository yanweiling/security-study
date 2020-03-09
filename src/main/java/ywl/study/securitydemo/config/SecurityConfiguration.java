package ywl.study.securitydemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ywl.study.securitydemo.service.CustomUserDetailsService;
import ywl.study.securitydemo.util.TokenProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomUserDetailsService userDetailsService;
    @Autowired
    TokenProvider tokenProvider;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //基于token，不需要用session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                //设置哪些请求可以匿名访问
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/product/**").hasRole("USER")
                .anyRequest().authenticated()
                .and()
                .apply(securityConfigurerAdapter())
                .and()
                .formLogin().and()
                .httpBasic();

         //如果采用token验证，那header请求一定要禁用缓存
        http.headers().cacheControl();
    }

    /*增加了 管理员（admin1，密码admin1），以及普通用户（user1,密码user1）*/
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
//        auth.inMemoryAuthentication()
//                .withUser("admin1")
//                .password("{noop}admin1")
//                .roles("ADMIN","USER")
//                .and()
//                .withUser("user1")
//                .password("{noop}user1")
//                .roles("USER");
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    //解决AuthenticationManager注入不进来的问题
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 增加方法
    private MyAuthTokenConfigurer securityConfigurerAdapter() {
        return new MyAuthTokenConfigurer(userDetailsService, tokenProvider);
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encode=new BCryptPasswordEncoder();
        System.out.println(encode.encode("user1"));
    }


}
