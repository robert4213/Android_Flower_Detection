# This is app.py, this is the main file called.
from server import app

@app.route('/')
def home():
    return 'Home Page, Flask Appplication Started'

if __name__ == '__main__':
    app.run(debug=True,host="0.0.0.0")
