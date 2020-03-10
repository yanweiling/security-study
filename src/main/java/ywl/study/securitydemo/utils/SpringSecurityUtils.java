package ywl.study.securitydemo.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import ywl.study.securitydemo.entity.security.JwtUserDetails;

public class SpringSecurityUtils {
    public static JwtUserDetails getCurrentUser() {
        return (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
