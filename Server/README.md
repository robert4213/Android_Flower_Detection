### 跑服务器
python3 app.py

- 需要把utils/config.py 里面几个关于model的地址改成自己的机子上的地址

- 如果报错没有module，根据错误提示把相应的library安装一下就可以了，比如 pip3 install XXXXX(library name)
- 如果数据库报错，把server/data.sqlite删除，然后跑一下python3 db_setup.py


### APIs
1. 注册URL
"http://10.0.2.2:5000/user/register";
Request
`{
    "email": "sharon.jin@sjsu.edu",
    "mobile": "15536644556",
    "username": "sharon",
    "password": "test",
    "password2": "test"
}`

2. Login in
"http://10.0.2.2:5000/user/login";
Request
`{
    "email": "sharon.jin@sjsu.edu",
    "password": "test"
}`

3. Logout
"http://10.0.2.2:5000/user/logout";


4. Change password
"http://10.0.2.2:5000/user/change_password";
Request json body
`{
    "current_password": "test",
    "new_password": "test1",
    "new_password2": "test1"
}`
