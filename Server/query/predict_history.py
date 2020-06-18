from flask import Blueprint,render_template,redirect,url_for,flash
from flask import Flask, render_template, request, send_from_directory, jsonify
from werkzeug.utils import secure_filename
import os
from server.model import predict
import json

predict_history_blueprint = Blueprint('query',
                              __name__)


# Upload Image Route
@predict_history_blueprint.route('/predict_history', methods=['GET'])
def predict_history():
    ## 还没写完
    pass
    ## need to return the prediction history of a user, a json file
    ## return render_template('login_history.html', form=form)
