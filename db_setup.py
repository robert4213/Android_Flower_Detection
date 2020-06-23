from server import db
from server.db_models import User, LoginHistory, Species, PredictHistory
from datetime import datetime, time
# ## CREATE ALL THE TABLES Model -> DB table
# Species.__table__.drop(app)

db.create_all()
s1 = Species("rose", "https://en.wikipedia.org/wiki/Rose",datetime.now(), "s_rose");
s2 = Species("cyclamen", "https://en.wikipedia.org/wiki/Cyclamen",datetime.now(), "s_cyclamen");
s3 = Species("foxglove", "https://en.wikipedia.org/wiki/Digitalis",datetime.now(), "s_foxglove");
s4 = Species("frangipani", "https://en.wikipedia.org/wiki/Plumeria",datetime.now(), "s_frangipani");
s5 = Species("lotus", "https://en.wikipedia.org/wiki/Nelumbo_nucifera",datetime.now(), "s_lotus");
s6 = Species("passion flower", "https://en.wikipedia.org/wiki/Passiflora",datetime.now(), "s_passionflower");
s7 = Species("petunia", "https://en.wikipedia.org/wiki/Petunia",datetime.now(), "s_petunia");
s8 = Species("wallflower", "https://en.wikipedia.org/wiki/Erysimum",datetime.now(), "s_wallflower");
s9 = Species("water lily", "https://en.wikipedia.org/wiki/Nymphaeaceae",datetime.now(), "s_waterlily");
s10 = Species("watercress", "https://en.wikipedia.org/wiki/Watercress",datetime.now(), "s_watercress");

for s in [s1,s2,s3,s4,s5,s6,s7,s8,s9,s10]:
    db.session.add(s)

db.session.commit()

users = User.query.all()
for ele in users:
    print(ele)
print("Done printing all users in db")

records = LoginHistory.query.all()
for record in records:
    print(record)
print("Done printing all login history in db")
