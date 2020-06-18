### 跑服务器
python3 app.py

- 需要把utils/config.py 里面几个关于model的地址改成自己的机子上的地址

- 如果报错没有module，根据错误提示把相应的library安装一下就可以了，比如 pip3 install XXXXX(library name)
- 如果数据库报错，把server/data.sqlite删除，然后跑一下python3 db_setup.py


### APIs
1. 注册URL
- POSTMAN: http://10.0.2.2:5000/user/register
- Andriod Studio: http://10.0.2.2:5000/user/register
- JSON Request Body (POST)
`{
    "email": "sharon.jin@sjsu.edu",
    "mobile": "15536644556",
    "username": "sharon",
    "password": "test",
    "password2": "test"
}`

2. Login in
- POSTMAN: http://127.0.0.1:5000/user/login
- Andriod Studio: http://10.0.2.2:5000/user/login
- JSON Request Body (POST)
`{
    "email": "sharon.jin@sjsu.edu",
    "password": "test"
}`

3. Logout
- POSTMAN: http://127.0.0.1:5000/user/logout
- Andriod Studio: http://10.0.2.2:5000/user/logout
- GET

4. Change password
- POSTMAN: http://127.0.0.1:5000/user/change_password
- Andriod Studio: http://10.0.2.2:5000/user/change_password
- JSON Request Body (POST)
Request json body
`{
    "current_password": "test",
    "new_password": "test1",
    "new_password2": "test1"
}`

5. Image Upload
- POSTMAN: http://127.0.0.1:5000/upload
- Andriod Studio: http://10.0.2.2:5000/upload
