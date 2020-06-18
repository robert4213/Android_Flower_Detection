from flask import Blueprint,render_template,redirect,url_for,flash
from flask import Flask, render_template, request, send_from_directory, jsonify
from werkzeug.utils import secure_filename
import os
from server.model import predict
import json

login_history_blueprint = Blueprint('login',
                              __name__,
                              template_folder='templates/')


# Upload Image Route
@login_history_blueprint.route('/login_history', methods=['GET'])
def login_history():
    pass
    ## need to return the login history of a user, a json file
    return render_template('login_history.html', form=form)
