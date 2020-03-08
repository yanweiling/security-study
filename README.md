数据库中额角色要加前缀ROLE_

1.新建tokenProvider
2.新建MyToken
3.新建AuthenticateController--现在我们想匿名请求此接口，需要在SecurityConfiguration中配置
.antMatchers("/auth/**").permitAll()

这样启动项目后，可以不用登录直接访问
http://localhost:8080/auth/authenticate?username=admin&password=admin
获得{"token":"admintoken","expires":1585454646793}

如果输入的username或者password不正确，则会跳转到/login下

----
我们现在在login下，输入用户名和密码，即使正确，也无法实现跳转---我们需要复原session