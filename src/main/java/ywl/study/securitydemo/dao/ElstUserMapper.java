package ywl.study.securitydemo.dao;

import org.apache.ibatis.annotations.Mapper;
import ywl.study.securitydemo.entity.ElstUser;
import ywl.study.securitydemo.entity.security.JwtUserDetails;

@Mapper
public interface ElstUserMapper {
    JwtUserDetails loadUserByUsername(String userId);

    int insert(ElstUser record);

    int insertSelective(ElstUser record);
}
