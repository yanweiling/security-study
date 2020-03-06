
**第一步**
 - 
 ```  
  @Controller
  public class AppController {
         
             @RequestMapping("/hello")
             @ResponseBody
             String home() {
                 return "Hello ,spring security!";
             }
         } 
 ``` 



    这个访问此方法可直接访问通过，不收权限框架保护

2.第二步
 - 
  ``` 
新增
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
 ``` 
    再次访问/hello，我们可以得到一个http-basic的认证弹窗，如果是springboot 2.x的话，不会有认证弹框，如果有1.x的话，会有认证弹框

    关闭认证弹框的方式：security.basic.enabled=false



3.第三步
 - 
 ```
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();
    }
}
 ```
    说明：
    authorizeRequests() 定义哪些URL需要被保护 哪些不需要被保护
    formLogin() 定义当需要用户登录时候，转到的登录页面，此时，我们并没有写登录页面，但是spring security默认提供了一个登录页面，以及登录控制器。

    加完了上面的配置类之后，我们重启应用。然后继续访问http://localhost:8080/hello。会发现自动跳转到一个登录页面了
`  这个页面是spring security 提供的默认的登录页面，其的html内容如下： `

 <html><head><title>Login Page</title></head><body onload='document.f.username.focus();'>
 <h3>Login with Username and Password</h3><form name='f' action='/login' method='POST'>
 <table>
     <tr><td>User:</td><td><input type='text' name='username' value=''></td></tr>
     <tr><td>Password:</td><td><input type='password' name='password'/></td></tr>
     <tr><td colspan='2'><input name="submit" type="submit" value="Login"/></td></tr>
     <input name="_csrf" type="hidden" value="635780a5-6853-4fcd-ba14-77db85dbd8bd" />
 </table>
 </form></body></html>

    我们可以发现，这里有个form 。action="/login"，这个/login依然是spring security提供的。form表单提交了三个数据：

    username 用户名
    password 密码
    _csrf CSRF保护方面的内容，暂时先不展开解释


 为了登录系统，我们需要知道用户名密码，spring security 默认的用户名是user，spring security启动的时候会生成默认密码（在启动日志中可以看到）。本例，我们指定一个用户名密码，在配置文件中加入如下内容：
 ``` 
 # security
 security.basic.enabled=false
 security.user.name=admin
 security.user.password=admin

 2.x的话，需要这样配置
 spring.security.user.name=admin
 spring.security.user.password=admin
 ``` 

 4.第四步
  - 
 通常情况下，我们需要实现“特定资源只能由特定角色访问”的功能。假设我们的系统有如下两个角色：

    ADMIN 可以访问所有资源
    USER 只能访问特定资源

 新增ProductTestController  新增AdminTestController
 新增用户
  ``` 
  @Override
     protected void configure(AuthenticationManagerBuilder auth) throws Exception {
         auth
                 .inMemoryAuthentication()
                 .withUser("admin1") // 管理员，同事具有 ADMIN,USER权限，可以访问所有资源
                     .password("{noop}admin1")  //
                     .roles("ADMIN", "USER")
                     .and()
                 .withUser("user1").password("{noop}user1") // 普通用户，只能访问 /product/**
                     .roles("USER");
     }
 ``` 
备注，如果是1.X的话{noop}不用写

继续增加“链接-角色”控制配置，代码如下:
 ``` 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/product/**").hasRole("USER")
                    .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();
    }
 ``` 