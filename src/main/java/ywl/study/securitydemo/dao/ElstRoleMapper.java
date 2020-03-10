package ywl.study.securitydemo.dao;

import org.apache.ibatis.annotations.Mapper;
import ywl.study.securitydemo.entity.ElstRole;

import java.util.List;

@Mapper
public interface ElstRoleMapper {
    List<ElstRole> selectRoleByUser(String userId);
    List<ElstRole> selectRoleByMenu(String menuId);
}
