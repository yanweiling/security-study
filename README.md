https://blog.csdn.net/qq_36882793/article/details/102839333---文档来源

1.UsernamePasswordAuthenticationFilter 默认拦截的是/login,
所以当请求：http://localhost:8888/elst/login POST 的时候，会被JwtLoginFilter拦截 ，
由JwtLoginFilter生成未登录的认证信息JwtLoginToken，然后调用自己配置好的AuthenticationProvider进行校验，
此项目中用的AuthenticationProvider是JwtAuthenticationProvider
校验方式：
1.根据输入的用户名，获取到UserDetail信息【用户名，密码，是否过期，是否被锁定，是否可用等信息】
2.检验该UserDetail是否可用，是否未被锁定，是否未过期
3.将JwtLoginToken中的密码和UserDetail中的密码进行对比校验，校验通过，构造已经认证的JwtLoginToken

由于JwtLoginFilter可以设置了校验成功处理类和校验失败的处理类；
当校验完成后，会调用响应的处理类；
登录成功，会返回签发的 jwt token信息
-----
如果请求不是/login ，则会被JwtTokenFilter拦截【我们也可以设置匿名请求的路径，这样就不会被spring security拦截了】
1.成功登录后，将返回的token信息放在header中请求
2.校验token信息是否存在--不存在，提示没有登录；如果已经失效，则提示重新登录
3.将jwt token转换成Claim，如果转换失败，则提示登录失效
4.以上三步完成后，则封装成已经登录的jwt token，并且在security 上下文进行保存，继续下面的filter
5.由于定义了
  .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setSecurityMetadataSource(filterInvocationSecurityMetadataSource);
                        object.setAccessDecisionManager(accessDecisionManager);
                        return object;
                    }
 所以，接下来会调用filterInvocationSecurityMetadataSource---检验请求的路径需要什么角色---并且把请求路径，所需要的角色集合，用户JwtToken全部传到下一个filter中
 然后调用 accessDecisionManager---根据上一个filter传过来的参数，校验当前用户是否有角色

-------------
测试方式：
1.http://localhost:8888/elst/login?username=1&password=123 post
返回：
{
    "code": 1000,
    "data": "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1ODM4MzM1NTcsInVzZXJEZXRhaWxzIjoie1wiYWNjb3VudE5vbkV4cGlyZWRcIjp0cnVlLFwiYWNjb3VudE5vbkxvY2tlZFwiOnRydWUsXCJhdXRob3JpdGllc1wiOlt7XCJhdXRob3JpdHlcIjpcIjFcIn0se1wiYXV0aG9yaXR5XCI6XCIzXCJ9XSxcImNyZWRlbnRpYWxzTm9uRXhwaXJlZFwiOnRydWUsXCJlbmFibGVkXCI6dHJ1ZSxcImlkXCI6MSxcInBhc3N3b3JkXCI6XCIkMmEkMTAkTkVzck55QklQR0VMeDlEcElibS94LlZ3QU1PemhtWDRjT2ZPNGp6d1JKZjZYaXpZcnhqSTJcIixcInJvbGVzXCI6W3tcImlkXCI6MSxcInJvbGVJZFwiOlwiMVwiLFwicm9sZU5hbWVcIjpcImFkbWluXCJ9LHtcImlkXCI6MyxcInJvbGVJZFwiOlwiM1wiLFwicm9sZU5hbWVcIjpcInN1cGVyXCJ9XSxcInVzZXJFbmFibGVkXCI6MSxcInVzZXJJZFwiOlwiMVwiLFwidXNlck5hbWVcIjpcInpoYW5nc2FuXCIsXCJ1c2VyUHdkXCI6XCIkMmEkMTAkTkVzck55QklQR0VMeDlEcElibS94LlZ3QU1PemhtWDRjT2ZPNGp6d1JKZjZYaXpZcnhqSTJcIixcInVzZXJuYW1lXCI6XCJ6aGFuZ3NhblwifSJ9.YnHwCYbSHWfYuSyNSB71k7st-2D0y5BVuwYcmfvyig8",
    "message": "操作成功"
}

2.http://localhost:8888/elst/kw/admin  
header中
Authentication=token信息
返回：hahah

3.请求http://localhost:8888/elst/login?username=2&password=123 post
返回：
{
    "code": 1000,
    "data": "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1ODM4MzM4ODgsInVzZXJEZXRhaWxzIjoie1wiYWNjb3VudE5vbkV4cGlyZWRcIjp0cnVlLFwiYWNjb3VudE5vbkxvY2tlZFwiOnRydWUsXCJhdXRob3JpdGllc1wiOlt7XCJhdXRob3JpdHlcIjpcIjJcIn1dLFwiY3JlZGVudGlhbHNOb25FeHBpcmVkXCI6dHJ1ZSxcImVuYWJsZWRcIjp0cnVlLFwiaWRcIjoyLFwicGFzc3dvcmRcIjpcIiQyYSQxMCRORXNyTnlCSVBHRUx4OURwSWJtL3guVndBTU96aG1YNGNPZk80anp3UkpmNlhpellyeGpJMlwiLFwicm9sZXNcIjpbe1wiaWRcIjoyLFwicm9sZUlkXCI6XCIyXCIsXCJyb2xlTmFtZVwiOlwidGVhY2hlclwifV0sXCJ1c2VyRW5hYmxlZFwiOjEsXCJ1c2VySWRcIjpcIjJcIixcInVzZXJOYW1lXCI6XCJsaXNpXCIsXCJ1c2VyUHdkXCI6XCIkMmEkMTAkTkVzck55QklQR0VMeDlEcElibS94LlZ3QU1PemhtWDRjT2ZPNGp6d1JKZjZYaXpZcnhqSTJcIixcInVzZXJuYW1lXCI6XCJsaXNpXCJ9In0.aNVVJNdWfiB98LOmQOjrA1AqOw0qL-7tl3p1Nf6envI",
    "message": "操作成功"
}

4.再请求 http://localhost:8888/elst/kw/admin post
Authentication =3返回的token信息
返回：
{
    "code": 2013,
    "message": "权限不足，请联系管理员 : 权限不足"
}

5.请求  post
http://localhost:8888/elst/jg/teacher
Authentication =3返回的token信息
返回：hahah