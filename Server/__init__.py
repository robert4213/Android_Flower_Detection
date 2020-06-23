import os
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate, MigrateCommand
from flask_login import LoginManager
from flask_mail import Mail
from datetime import timedelta
# import server.imageupload.imageupload


# Create a login manager object
login_manager = LoginManager()

app = Flask(__name__)
ROOT_PATH = app.root_path

# Often people will also separate these into a separate config.py file
app.config['SECRET_KEY'] = 'mysecretkey'
basedir = os.path.abspath(os.path.dirname(__file__))
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + os.path.join(basedir, 'data.sqlite')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# Static file cache expiration
app.send_file_max_age_default = timedelta(seconds=1)

db = SQLAlchemy(app)
Migrate(app,db)
# db.add_command("db", MigrateCommand)

# We can now pass in our app to the login manager
login_manager.init_app(app)

# Tell users what view to go to when they need to login.
login_manager.login_view = "login"

## !! These imports need to come after you've defined db, otherwise you will
# get errors in your models.py files.
## Grab the blueprints from the other views.py files for each "app"

from server.imageupload.imageupload import imageupload_blueprint
from server.users.users import user_blueprint

app.register_blueprint(imageupload_blueprint,url_prefix="/")
app.register_blueprint(user_blueprint, url_prefix="/user")
