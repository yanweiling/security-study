package ywl.study.securitydemo.service.jwtsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ywl.study.securitydemo.dao.ElstUserMapper;

@Component
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private ElstUserMapper elstUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return elstUserMapper.loadUserByUsername(username);
    }
}
