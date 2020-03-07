package ywl.study.securitydemo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ywl.study.securitydemo.entity.User;

public interface UserRepository  extends JpaRepository<User,Long> {
    User findByLogin(String login);
}
