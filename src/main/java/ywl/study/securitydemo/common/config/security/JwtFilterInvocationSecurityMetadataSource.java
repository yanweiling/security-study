package ywl.study.securitydemo.common.config.security;

import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import ywl.study.securitydemo.dao.ElstMenuMapper;
import ywl.study.securitydemo.entity.info.ElstMenuInfo;

import java.util.Collection;
import java.util.List;

/**
 * 获取有权访问当前url的角色列表
 */
@Component
public class JwtFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    @Autowired
    private ElstMenuMapper elstMenuMapper;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        System.out.println("JwtFilterInvocationSecurityMetadataSource getAttributes");
        String requestUrl=((FilterInvocation)object).getRequestUrl();
        List<ElstMenuInfo> allMenu= elstMenuMapper.getAllMenuInfo();
        for(ElstMenuInfo menu: allMenu){
            if(antPathMatcher.match(menu.getMenuUrl(),requestUrl) && menu.getRoles().size()>0){
                String[] roleIds=menu.getRoles().stream().map(r->r.getRoleId()).toArray(String[]::new);
                return SecurityConfig.createList(roleIds);
            }
        }
        //如果没有匹配，则表示全部可以访问
        return SecurityConfig.createList();
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
