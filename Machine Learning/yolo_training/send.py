import requests

url = "http://34.105.118.192/"

payload = {}
files = {}
headers= {}

response = requests.request("POST", url,)

print(response.text.encode('utf8'))
