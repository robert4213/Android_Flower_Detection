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

    def __repr__(self):
        return "username: {}, email: {}, mobile: {}, registration_time: {}, password: {}". \
                    format(self.username, self.email, self.mobile, self.registration_time, self.password)

    def change_password(self, password):
        self.password_hash = generate_password_hash(password)

    def set_mobile(self, mobile):
        self.mobile = mobile

    def set_username(self, username):
        self.username = username

    def set_mobile(self, mobile):
        self.mobile = mobile



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
    login_agent = db.Column(db.String(128))


    def __init__(self,user_id, ip_address, successful, login_time, login_agent):
        self.user_id = user_id
        self.ip_address = ip_address
        self.successful = successful
        self.login_time = login_time
        self.login_agent = login_agent

    def __repr__(self):
        return "login user_id: {}, ip_address: {}, successful: {}, \
                login_time: {}, login_agent: {}".format(self.user_id,\
                    self.ip_address, self.successful, self.login_time,\
                    self.login_agent)

##############
## Model - Species
##############
class Species(db.Model):

    __tablename__ = 'species'

    id = db.Column(db.Integer,primary_key= True)
    name = db.Column(db.String(128))
    external_link = db.Column(db.String(256))
    update_time = db.Column(db.DateTime)
    standard_image_id = db.Column(db.String(128))

    def __init__(self,name, external_link, update_time):
        self.name = name
        self.external_link = external_link
        self.update_time = update_time

    def __repr__(self):
        return "id: {}, name: {}, external_link: {}".format(self.id, self.name, self.external_link)

    def set_external_link(self, external_link):
        self.external_link = external_link
        self.update_time = datetime.now()

    def set_standard_image(self, standard_image_id):
        self.standard_image_id = standard_image_id
        self.update_time = datetime.now()

##############
## Model - PredictHistory
##############
class PredictHistory(db.Model):

    __tablename__ = 'predict_histories'

    id = db.Column(db.Integer,primary_key= True)
    user_id = db.Column(db.Integer, db.ForeignKey("users.id"))
    species_id = db.Column(db.Integer, db.ForeignKey("species.id"))
    predict_time = db.Column(db.DateTime)
    # predict_image_id = db.Column(db.String(256))

    def __init__(self, user_id, species_id, predict_time):
        self.user_id = user_id
        self.species_id = species_id
        self.predict_time = predict_time
        #self.predict_image_id = predict_image_id

    def __repr__(self):
        return "user_id: {} predicted species_id:{} at {}".format(self.user_id, self.species_id, self.predict_time)
