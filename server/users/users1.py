from flask import Blueprint, current_app, session, request, jsonify
from werkzeug.utils import secure_filename
from sqlalchemy.exc import IntegrityError
from datetime import datetime
from server import db
from server.utils import response_code
from server.db_models import User, LoginHistory
import json
import re
import os


user_blueprint = Blueprint('user', __name__)

# Register API
@user_blueprint.route('/register', methods=['POST'])
def register():

    # get dictionary from json from request
    req_dict = request.get_json()

    email = req_dict.get("email")
    mobile = req_dict.get("mobile")
    username = req_dict.get("username")
    password = req_dict.get("password")
    password2 = req_dict.get("password2")

    # test print, will be deleted
    print("All users in db")
    users = User.query.all()
    for ele in users:
        print(ele)
    print("Done printing all users in db")

    print("request, email: {}, username: {}\
            mobile: {}, password: {}, password2: {}".\
            format(email, username, mobile, password, password2))

    # check arguments
    if not all([email, username, password, password2]):
        return jsonify(errno=response_code.RET.PARAMERR, errmsg="Registration information not complete")
    # check email address format
    if not re.match(r"\"?([-a-zA-Z0-9.`?{}]+@\w+\.\w+)\"?", email):
        return jsonify(errno=response_code.RET.PARAMERR, errmsg="Please enter the correct email address")
    # check mobile number format
    if mobile and not re.match(r"\d{10}", mobile):
        return jsonify(errno=response_code.RET.PARAMERR, errmsg="Mobile number incorrect")

    if password != password2:
        return jsonify(errno=response_code.RET.PARAMERR, errmsg="Passwords not match")

    registration_time = datetime.now()
    user = User(email=email, username=username, password=password, registration_time=registration_time)
    if mobile:
        user.set_mobile(mobile)

    print(user)
    try:
        db.session.add(user)
        db.session.commit()
    except IntegrityError as e:
        # if error, db rollback
        db.session.rollback()
        current_app.logger.error(e)
        return jsonify(errno=response_code.RET.PARAMERR, errmsg="Email address registered!")
    except Exception as e:
        db.session.rollback()
        current_app.logger.error(e)
        return jsonify(errno=response_code.RET.DATAERR, errmsg="Database Error!")

    session["username"] = username
    session["email"] = email
    session["user_id"] = user.id

    return jsonify(errno=response_code.RET.OK, errmsg="Successfully registered")


# login API
@user_blueprint.route('/login', methods=['POST'])
def login():

    req_dict = request.get_json()
    email = req_dict.get("email")
    password = req_dict.get("password")

    if not all([email, password]):
        return jsonify(errno=response_code.RET.PARAMERR, errmsg="Please enter Email and Password")

    try:
        user = User.query.filter_by(email=email).first()
    except Exception as e:
        current_app.logger.error(e)
        return jsonify(errno=response_code.RET.DBERR, errmsg="Query User Failed")

    if user is None:
        return jsonify(errno=response_code.RET.DBERR, errmsg="User Not Exist")

    if not user.check_password(password):

        new_login = LoginHistory(user.id, \
                            ip_address=request.environ['REMOTE_ADDR'], \
                            successful=False, \
                            login_time=datetime.now(), \
                            login_agent=request.user_agent.string)
        write_login_history(new_login)
        return jsonify(errno=response_code.RET.DBERR, errmsg="Incorrect Password")

    session["email"] = email
    session["username"] = user.username
    session["user_id"] = user.id

    # write login history
    new_login = LoginHistory(user.id, \
                            ip_address=request.environ['REMOTE_ADDR'], \
                            successful=True, \
                            login_time=datetime.now(), \
                            login_agent=request.user_agent.string)

    write_login_history(new_login)

    return jsonify(errno=response_code.RET.OK, errmsg="Login Successful")


def write_login_history(new_login):
    try:
        db.session.add(new_login)
        db.session.commit()
    except Exception as e:
        print("LOGIN HISTORY WRITE ERROR")
        db.session.rollback()


# check_login
@user_blueprint.route('/check_login', methods=['GET'])
def check_login():
    email = session.get("email")
    if email:
        return jsonify(errno=response_code.RET.OK, errmsg="true", data={"email":email})
    else:
        return jsonify(errno=response_code.RET.SESSIONERR, errmsg="not login")

# log out
@user_blueprint.route('/logout', methods=['DELETE'])
def logout():
    csrf_token = session.get("csrf_token")
    session.clear()
    session["csrf_token"] = csrf_token
    return jsonify(errno=response_code.RET.OK, errmsg="OK")

# change password
@user_blueprint.route('/change_password', methods=['POST'])
def change_password():
    # get dictionary from json from request
    req_dict = request.get_json()

    if not session.get("user_id"):
        return jsonify(errno=response_code.RET.SESSIONERR, errmsg="not login")

    current_password = req_dict.get("current_password")
    new_password = req_dict.get("new_password")
    new_password2 = req_dict.get("new_password2")

    try:
        user = User.query.filter_by(id=session.get("user_id")).first()
    except Exception as e:
        current_app.logger.error(e)
        return jsonify(errno=response_code.RET.DBERR, errmsg="Query User Failed")

    if not user.check_password(current_password):
        return jsonify(errno=response_code.RET.DBERR, errmsg="Current password incorrect")

    if not all([current_password, new_password, new_password2]):
        return jsonify(errno=response_code.RET.PARAMERR, errmsg="Please enter passwords")

    if new_password != new_password2:
        return jsonify(errno=response_code.RET.PARAMERR, errmsg="Passwords not match")

    try:
        user.change_password(new_password)
        db.session.commit()
    except Exception as e:
        db.session.rollback()
        current_app.logger.error(e)
        return jsonify(errno=response_code.RET.DATAERR, errmsg="Database Error!")

    return jsonify(errno=response_code.RET.OK, errmsg="Password changed")
