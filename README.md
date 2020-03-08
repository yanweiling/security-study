本分支实现：输入用户名和密码获得用户token，之后，再访问接口，只要接口中header中带有token，并对token经过一系列校验以后，
认证通过，存放认证信息以后，可以直接访问到接口

1.新增MyTokenFilter ----通过这个filter来拦截客户端请求 解析其中的token，复原无状态下的"session"
让当前请求处理线程中具有认证授权数据，后面的业务逻辑才能执行
2.新增MyAuthTokenConfigurer --将MyTokenFilter加到Security的filterChain以前

3.在SecurityConfiguration中
配置
 .apply(securityConfigurerAdapter());// 这里增加securityConfigurerAdapter
 
 测试步骤
 1.访问http://localhost:8080/auth/authenticate?username=admin&password=admin
 获得 {"token":"admintoken","expires":1585475822551}
 2.http://localhost:8080/admin/home get请求 header中设置  my-auth-token=admintoken
   会触发MyTokenFilter的过滤方法，认证通过且保存认证信息后，就直接访问到接口了；