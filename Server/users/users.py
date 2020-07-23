from flask import Blueprint, current_app, session, request, jsonify
from flask_login import login_required
from werkzeug.utils import secure_filename
from sqlalchemy.exc import IntegrityError
from datetime import datetime
from server import db
from server.utils import response_code
from server.db_models import User, LoginHistory
import json
import re
import os
import uuid


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

    # Generate a session_id for login user
    session_id = str(uuid.uuid4())
    session[session_id] = email
    # write login history
    new_login = LoginHistory(user.id, \
                            ip_address=request.environ['REMOTE_ADDR'], \
                            successful=True, \
                            login_time=datetime.now(), \
                            login_agent=request.user_agent.string)

    write_login_history(new_login)

    return jsonify(errno=response_code.RET.OK, \
                   errmsg="Login Successful", \
                   session_id=session_id)


def write_login_history(new_login):
    try:
        db.session.add(new_login)
        db.session.commit()
    except Exception as e:
        print("LOGIN HISTORY WRITE ERROR")
        db.session.rollback()


# check_login
@user_blueprint.route('/check_login', methods=['POST'])
def check_login():
    req_dict = request.get_json()
    if not req_dict.get("session_id"):
        return jsonify(errno=response_code.RET.SESSIONERR, \
                       errmsg="no user login")
    session_id = req_dict.get("session_id")
    email = session.get(session_id)
    if email:
        return jsonify(errno=response_code.RET.OK, \
                       errmsg="true", \
                       email=session[session_id])
    else:
        return jsonify(errno=response_code.RET.SESSIONERR, errmsg="no user login")

# log out
@user_blueprint.route('/logout', methods=['POST'])
def logout():
    req_dict = request.get_json()
    if not req_dict.get("session_id"):
        return jsonify(errno=response_code.RET.SESSIONERR, errmsg="no user login")
    session_id = req_dict.get("session_id")
    del session[session_id]
    return jsonify(errno=response_code.RET.OK, errmsg="OK")

# change password
@user_blueprint.route('/change_password', methods=['POST'])
def change_password():
    # get dictionary from json from request
    req_dict = request.get_json()
    if not req_dict.get("session_id"):
        return jsonify(errno=response_code.RET.SESSIONERR, errmsg="no user login")
    session_id = req_dict.get("session_id")
    current_password = req_dict.get("current_password")
    new_password = req_dict.get("new_password")
    new_password2 = req_dict.get("new_password2")
    print("request recieved")
    try:
        user = User.query.filter_by(email=session[session_id]).first()
        print(session[session_id])
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
