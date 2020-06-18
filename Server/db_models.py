from server import db, login_manager
from werkzeug.security import generate_password_hash,check_password_hash
from flask_login import UserMixin
from datetime import time, datetime
# By inheriting the UserMixin we get access to a lot of built-in attributes
# which we will be able to call in our views!
# is_authenticated()
# is_active()
# is_anonymous()
# get_id()



##############
## Model - USER
##############
# The user_loader decorator allows flask-login to load the current user
# and grab their id.
@login_manager.user_loader
def load_user(user_id):
    return User.query.get(user_id)

class User(db.Model, UserMixin):

    # Create a table in the db
    __tablename__ = 'users'

    id = db.Column(db.Integer, primary_key = True)
    email = db.Column(db.String(64), unique=True, index=True, nullable=False)
    username = db.Column(db.String(64), nullable=False)
    mobile = db.Column(db.String(10))
    password = db.Column(db.String(128), nullable=False)
    password_hash = db.Column(db.String(128), nullable=False)
    registration_time = db.Column(db.DateTime, nullable=False)

    def __init__(self, email, username, password, registration_time):
        self.email = email
        self.username = username
        self.password = password
        self.password_hash = generate_password_hash(password)
        self.registration_time = registration_time

    def check_password(self, password):
        # https://stackoverflow.com/questions/23432478/flask-generate-password-hash-not-constant-output
        print("********print passwords*********")
        print(self.password)
        print(password)
        print(self.password_hash)
        print(check_password_hash(self.password_hash, password))
        return check_password_hash(self.password_hash, password)

    def change_password(self, password):
        self.password_hash = generate_password_hash(password)    

    def set_mobile(self, mobile):
        self.mobile = mobile

    def __repr__(self):
        return "username: {}, email: {}, mobile: {}, registration_time: {}". \
                    format(self.username, self.email, self.mobile, self.registration_time)

##############
## Model - LoginHistory
##############
class LoginHistory(db.Model):

    __tablename__ = 'loginhistories'

    id = db.Column(db.Integer,primary_key= True)
    user_id = db.Column(db.Integer, db.ForeignKey("users.id"))
    ip_address = db.Column(db.String(24))
    successful = db.Column(db.Boolean)
    login_time = db.Column(db.DateTime)
    login_device = db.Column(db.String(128))


    def __init__(self,user_id, ip_address, successful, login_time, login_device):
        self.user_id = user_id
        self.ip_address = ip_address
        self.successful = successful
        self.device_id = device_id
        self.login_device = login_device

##############
## Model - PredictHistory
##############
class Species(db.Model):

    __tablename__ = 'species'

    id = db.Column(db.Integer,primary_key= True)
    name = db.Column(db.String(128))
    external_link = db.Column(db.String(256))
    update_time = db.Column(db.DateTime)
    stardard_image_id = db.Column(db.String(128))

    def __init__(self,name, external_link, update_time, stardard_image_id):
        self.name = name
        self.external_link = external_link
        self.update_time = update_time
        self.stardard_image_id = stardard_image_id

    def __repr__(self):
        return "id: {}, name: {}".format(self.id, self.name)


##############
## Model - PredictHistory
##############
class PredictHistory(db.Model):

    __tablename__ = 'predict_histories'

    id = db.Column(db.Integer,primary_key= True)
    user_id = db.Column(db.Integer, db.ForeignKey("users.id"))
    species_id = db.Column(db.Integer, db.ForeignKey("species.id"))
    predict_time = db.Column(db.DateTime)
    predict_image_id = db.Column(db.String(256))

    def __init__(self,menu_name, brew_time):
        self.menu_name = menu_name
        self.brew_time = brew_time
