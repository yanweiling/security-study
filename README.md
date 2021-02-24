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

------------------------------------
1.用户登录username password，经过JwtLoginFilter（扩展UsernamePasswordAuthenticationFilter）,将用户名和密码封装成token
{"authenticated":false,"authorities":[],"credentials":"123","name":"1","principal":"1"}；
然后获取访问ip和sessionid，并将该信息写入到detail中,变成：
{"authenticated":false,"authorities":[],"credentials":"1234","details":{"remoteAddress":"0:0:0:0:0:0:0:1"},"name":"1","principal":"1"}
该token，我们叫做loginToken

2.交给AuthenticationManager进行校验该token的正确性，AuthenticationManager本身是个接口，交给具体的AuthenticationProvider去实现

3.JwtAuthenticationProvider（实现了AuthenticationProvider）中，根据token中用户名找到UserDetail，并将token中的密码和UserDetail中的密码进行
对比校验，如果校验失败，则会被SimpleUrlAuthenticationFailureHandler的onAuthenticationFailure处理，根据不同的异常类型，返回前端不同的提示信息；

如果校验成功，则根据UserDetail，用户密码，用户角色构造认证的token，并将起初登陆构造的loginToken中的请求ip和sessionid
设置到认证的token中；
{"authenticated":true,"authorities":[{"authority":"1"},{"authority":"3"}],"credentials":"123","details":{"remoteAddress":"0:0:0:0:0:0:0:1"},"name":"zhangsan","principal":{"accountNonExpired":true,"accountNonLocked":true,"authorities":[{"authority":"1"},{"authority":"3"}],"credentialsNonExpired":true,"enabled":true,"id":1,"password":"$2a$10$NEsrNyBIPGELx9DpIbm/x.VwAMOzhmX4cOfO4jzwRJf6XizYrxjI2","roles":[{"id":1,"roleId":"1","roleName":"admin"},{"id":3,"roleId":"3","roleName":"super"}],"userEnabled":1,"userId":"1","userName":"zhangsan","userPwd":"$2a$10$NEsrNyBIPGELx9DpIbm/x.VwAMOzhmX4cOfO4jzwRJf6XizYrxjI2","username":"zhangsan"}}
并将该token封装为JwtLoginToken（集成了Authentication）对象


4.然后被SimpleUrlAuthenticationFailureHandler的onAuthenticationSuccess方法处理：
获取JwtLoginToken，并将其中的princple也就是UserDetail对象封装成token返回给前端；

登陆过程中存在的异常有：
用户名不存在
用户名或者密码不正确
用户不可用
用户被锁定

------------------------------------------------------------------------------------

1.当用户请求非/login时候，会被JwtTokenFilter拦截（实现了OncePerRequestFilter）：
验证header中是否存在token
验证token是否已经失效
将token转换为Clamins:
{"exp":1614137400,"userDetails":"{\"accountNonExpired\":true,\"accountNonLocked\":true,\"authorities\":[{\"authority\":\"1\"},{\"authority\":\"3\"}],\"credentialsNonExpired\":true,\"enabled\":true,\"id\":1,\"password\":\"$2a$10$NEsrNyBIPGELx9DpIbm/x.VwAMOzhmX4cOfO4jzwRJf6XizYrxjI2\",\"roles\":[{\"id\":1,\"roleId\":\"1\",\"roleName\":\"admin\"},{\"id\":3,\"roleId\":\"3\",\"roleName\":\"super\"}],\"userEnabled\":1,\"userId\":\"1\",\"userName\":\"zhangsan\",\"userPwd\":\"$2a$10$NEsrNyBIPGELx9DpIbm/x.VwAMOzhmX4cOfO4jzwRJf6XizYrxjI2\",\"username\":\"zhangsan\"}"}

规则：claims中有userDetails，userDetails中有角色roles

将现有的userDetail和roles以及空密码构造认证的token：
{"authenticated":true,"authorities":[{"authority":"1"},{"authority":"3"}],"credentials":"","details":{"remoteAddress":"0:0:0:0:0:0:0:1"},"name":"zhangsan","principal":{"accountNonExpired":true,"accountNonLocked":true,"authorities":[{"authority":"1"},{"authority":"3"}],"credentialsNonExpired":true,"enabled":true,"id":1,"password":"$2a$10$NEsrNyBIPGELx9DpIbm/x.VwAMOzhmX4cOfO4jzwRJf6XizYrxjI2","roles":[{"id":1,"roleId":"1","roleName":"admin"},{"id":3,"roleId":"3","roleName":"super"}],"userEnabled":1,"userId":"1","userName":"zhangsan","userPwd":"$2a$10$NEsrNyBIPGELx9DpIbm/x.VwAMOzhmX4cOfO4jzwRJf6XizYrxjI2","username":"zhangsan"}}
并将给token保存到SecurityContextHolder中，并放行

2.如果token是伪造的，则token无法转换为Clamins，则由response直接写出提示信息；

3.如果token校验通过，则判断用户请求的资源需要哪些角色，用FilterInvocationSecurityMetadataSource，
并将所需要的角色放入到SecurityConfig中；

4.判断该用户是否具有该角色，进入到AccessDecisionManager中：没有相应角色，则抛出异常



