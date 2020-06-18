from flask import Flask, render_template, request, send_from_directory, jsonify
from werkzeug.utils import secure_filename
from datetime import timedelta
import os
from model import predict
import json


app = Flask(__name__)

# 输出
@app.route('/')
def home():
    return 'Home Page'

# 设置允许的文件格式
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'JPG', 'PNG', 'bmp'])
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


# Upload Image Route
@app.route('/upload', methods=['POST', 'GET'])
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
            return jsonify({"error": 1001, "msg": "图片类型：png、PNG、jpg、JPG、bmp"})
        # Get basepath
        basepath = os.path.dirname(__file__)
        # Save the image in static/images folder
        upload_path = os.path.join(basepath, 'static/images', secure_filename(f.filename))
        # Save the image
        f.save(upload_path)
        print(upload_path)
        # Run the machinle learning prediction model
        boxes = predict.predict(upload_path)
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
    with open("static/data.json", "w") as f:
        json.dump(result_dic, f)
    return result_dic

@app.route('/data.json',methods=['GET'])
def download_file():
    return send_from_directory(os.path.join(app.root_path, 'static'), 'data.json')

if __name__ == '__main__':
    app.run(debug=True)
