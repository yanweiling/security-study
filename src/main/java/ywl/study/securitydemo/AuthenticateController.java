package ywl.study.securitydemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ywl.study.securitydemo.entity.MyToken;
import ywl.study.securitydemo.service.CustomUserDetailsService;
import ywl.study.securitydemo.util.TokenProvider;

import javax.annotation.Resource;

@RestController
@RequestMapping("/auth")
public class AuthenticateController {
    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Autowired
    TokenProvider tokenProvider;


    /*该接口可用于登录*/
    @RequestMapping(value = "/authenticate",method = RequestMethod.GET)
    public MyToken authorize(@RequestParam String username, @RequestParam String password) {
        // 1 创建UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(username, password);
        //2.认证---根据数据源查询，如果账号不正确，或者密码不对，则直接跳转到登录页面
        Authentication authentication=this.authenticationManager.authenticate(token);
        //3.保存认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //4 加载UserDetails
        UserDetails details=this.userDetailsService.loadUserByUsername(username);
        //5 生成自定义token
        return tokenProvider.createToken(details);

    }

}
