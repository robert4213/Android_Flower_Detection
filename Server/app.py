# This is app.py, this is the main file called.
from server import app
from flask import render_template

@app.route('/')
def home():
    return 'Home Page'

if __name__ == '__main__':
    app.run(debug=True)
