package ywl.study.securitydemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    //zhangsan可以访问,lisi权限不足
    @RequestMapping("kw/admin")
    public String admin(){
        return "hahah";
    }
    // zhanggsan，lisi都可访问
    @RequestMapping("jg/teacher")
    public String teacher(){
        return "hahah";
    }
}
