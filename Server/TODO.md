0. 整理了下文件结构
1. 上传图片和返回结果没问题
2. 数据库写了model，但是还没调试
3. 数据库的接口还没写
4. 登录验证的功能还没写完
5. 维基百科的显示感觉还需要优化



`python3 app.py db init
python3 app.py db migrate -m "init database"
`

- 完成和测试用户注册功能 ok
- 完成和测试用户登录功能 ok
- 完成用户登出功能的接口 ok
- 完成用户修改密码的接口 ok
---- 以上OK

- 完成插入用户登录记录数据库的写操作
- 完成用户预测记录数据库的写操作
---- 以上TODO

- 修改modle的session功能
- 修改model的返回功能（不要去json网页读取，直接把json result返回response）
---- 以上TODO

- 利用Load balancer处理多个request
---- 以上TODO
