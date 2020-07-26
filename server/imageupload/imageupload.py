from flask import Blueprint, render_template, Flask, request, send_from_directory, jsonify, session
from werkzeug.utils import secure_filename
from server.model import predict
from server import db
from server.db_models import PredictHistory, Species
from datetime import datetime
import json
import os
from server import detect
imageupload_blueprint = Blueprint('imageupload',
                              __name__,
                              template_folder='templates/')

# acceptable image format
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'JPG', 'PNG', 'bmp', 'jpeg'])
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


# Upload Image Route
@imageupload_blueprint.route('/upload', methods=['POST'])
def upload():
    # Get the image file
    import os
    if os.path.exists("static/data.json"):
        os.remove("static/data.json")
    else:
        print("The json file does not exist")

    f = request.files['file']
    if not (f and allowed_file(f.filename)):
        return jsonify({"error": 1001, "msg": "Image Type: png/PNG/jpg/JPG/bmp/jpeg"})

    # Basepath
    PATH = os.path.dirname(__file__)
    PARENT_PATH  = os.path.dirname(PATH)

    # Save the image in static/images folder
    upload_path = os.path.join(PARENT_PATH, 'static/images', secure_filename(f.filename))

    # Save the image
    f.save(upload_path)
    #print(upload_path)
    # Run the machinle learning prediction model
    boxes = detect.detect(upload_path)
    # print(boxes)
    # TODO Test deployment in Server
    # boxes = [['rose', [-12, 71, 427, 436], 0.97550285]]
    #return jsonify(boxes)
    os.remove(upload_path)
    # Jsonify the result
    result_dict = write_json_multiple_result(boxes)
    # TODO user_id need to fix to session["user_id"]
    # TODO species_id needs to fix to id
    new_predict = PredictHistory(user_id=1, \
                                species_id=1,\
                                predict_time=datetime.now())
    print(jsonify(result_dict))
    return jsonify(result_dict)
    # redirect to upload page
    # return render_template('upload.html')

# Transform the boxes into JSON
# Transform the boxes into JSON
def write_json_multiple_result(boxes):
    if len(boxes) == 0: return
    result_list = {}
    result_list["object"] = []
    for i in range(len(boxes)):
        result_dic = {}
        result_dic["type"] = boxes[i][0]
        box = {}
        box["top"] = boxes[i][1][0]
        box["left"] = boxes[i][1][1]
        box["bottom"] = boxes[i][1][2]
        box["right"] = boxes[i][1][3]
        result_dic["box"] = box
        result_dic["score"] = str(boxes[i][2])
        ## query external_link in database
        result_dic["link"] = Species.query.filter_by(name=boxes[i][0].capitalize()).first().external_link
        result_list["object"].append(result_dic)
    PATH = os.path.dirname(__file__)
    PARENT_PATH  = os.path.dirname(PATH)
    with open(os.path.join(PARENT_PATH, 'static/data1.json'), "w") as f:
        json.dump(result_list, f)
    return result_list


def write_predict_history(new_predict):
    try:
        db.session.add(new_predict)
        db.session.commit()
    except Exception as e:
        print("PREDICT HISTORY WRITE ERROR")
        db.session.rollback()

# Return data.json file
@imageupload_blueprint.route('/data.json',methods=['GET'])
def download_file():
    PATH = os.path.dirname(__file__)
    PARENT_PATH  = os.path.dirname(PATH)
    return send_from_directory(os.path.join(PARENT_PATH, 'static'), 'data.json')
