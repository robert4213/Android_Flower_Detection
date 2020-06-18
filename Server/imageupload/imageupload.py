from flask import Blueprint,render_template,redirect,url_for,flash
from flask import Flask, render_template, request, send_from_directory, jsonify
from werkzeug.utils import secure_filename
import os
from server.model import predict
import json

imageupload_blueprint = Blueprint('imageupload',
                              __name__,
                              template_folder='templates/')

# acceptable image format
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'JPG', 'PNG', 'bmp', 'jpeg'])
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


# Upload Image Route
@imageupload_blueprint.route('/upload', methods=['POST', 'GET'])
def upload():
    if request.method == 'POST':
        # Get the image file
        import os
        if os.path.exists("static/data.json"):
            os.remove("static/data.json")
        else:
            print("The json file does not exist")

        f = request.files['file']
        if not (f and allowed_file(f.filename)):
            return jsonify({"error": 1001, "msg": "Image Type: png/PNG/jpg/JPG/bmp/jpeg"})
        # Get basepath
        PATH = os.path.dirname(__file__)
        PARENT_PATH  = os.path.dirname(PATH)
        # Save the image in static/images folder
        upload_path = os.path.join(PARENT_PATH, 'static/images', secure_filename(f.filename))
        # Save the image
        f.save(upload_path)
        print(upload_path)
        # Run the machinle learning prediction model
        boxes = predict.predict_species(upload_path)
        print(boxes)
        # Jsonify the result
        result_dict = write_json_result(boxes)
        print(jsonify(result_dict))
        return jsonify(result_dict)
    # 重新返回上传界面
    return render_template('upload.html')

def write_json_result(boxes):
    if len(boxes) == 0: return
    result_dic = {}
    result_dic["type"] = boxes[0][0]
    box = {}
    box["top"] = boxes[0][1][0]
    box["left"] = boxes[0][1][1]
    box["bottom"] = boxes[0][1][2]
    box["right"] = boxes[0][1][3]
    result_dic["box"] = box
    result_dic["score"] = str(boxes[0][2])
    PATH = os.path.dirname(__file__)
    PARENT_PATH  = os.path.dirname(PATH)
    with open(os.path.join(PARENT_PATH, 'static/data.json'), "w") as f:
        json.dump(result_dic, f)
    return result_dic

@imageupload_blueprint.route('/data.json',methods=['GET'])
def download_file():
    PATH = os.path.dirname(__file__)
    PARENT_PATH  = os.path.dirname(PATH)
    return send_from_directory(os.path.join(PARENT_PATH, 'static'), 'data.json')
