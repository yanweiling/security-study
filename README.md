数据库中额角色要加前缀ROLE_

我们使用管理员账号密码登录之后，就可以访问/admin/home了，
此时修改浏览器地址栏为/product/info之后刷新页面，仍然可以访问，说明认证状态被保持了；
如果关闭浏览器重新输入/admin/home就会提示我们重新登录

这有点session的感觉。如果此时，我们将浏览器cookie禁用掉，你会发现登录之后自动跳转只会得到403，
403是拒绝访问的意思，是没有权限的意思，说明这种情况下授权状态和session是挂钩的

即这时spring security使用了session

study03 实现让security不使用session