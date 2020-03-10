package ywl.study.securitydemo.dao;

import org.apache.ibatis.annotations.Mapper;
import ywl.study.securitydemo.entity.info.ElstMenuInfo;

import java.util.List;

@Mapper
public interface ElstMenuMapper {
    List<ElstMenuInfo> getAllMenuInfo();
}
