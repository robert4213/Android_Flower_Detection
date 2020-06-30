from server import db
from server.db_models import User, LoginHistory, Species, PredictHistory
from datetime import datetime, time
# ## CREATE ALL THE TABLES Model -> DB table
# Species.__table__.drop(app)

# Delete records
Species.query.delete()
# db.create_all()
s1 = Species("Anthurium", "https://en.wikipedia.org/wiki/Anthurium",datetime.now());
s2 = Species("Azalea", "https://en.wikipedia.org/wiki/Azalea",datetime.now());
s3 = Species("Barbeton Daisy", "https://en.wikipedia.org/wiki/Gerbera_jamesonii",datetime.now());
s4 = Species("Bishop of Llandaff", "https://en.wikipedia.org/wiki/Dahlia_%27Bishop_of_Llandaff%27",datetime.now());
s5 = Species("Bougainvillea", "https://en.wikipedia.org/wiki/Bougainvillea",datetime.now());
s6 = Species("California Poppy", "https://en.wikipedia.org/wiki/Eschscholzia_californica",datetime.now());
s7 = Species("Camellia", "https://en.wikipedia.org/wiki/Camellia",datetime.now());
s8 = Species("Cape Flower", "https://en.wikipedia.org/wiki/Tecoma_capensis",datetime.now());
s9 = Species("Clematis", "https://en.wikipedia.org/wiki/Clematis",datetime.now());
s10 = Species("Common Dandelion", "https://en.wikipedia.org/wiki/Taraxacum_officinale",datetime.now());

for s in [s1,s2,s3,s4,s5,s6,s7,s8,s9,s10]:
    db.session.add(s)

s11 = Species("Cyclamen", "https://en.wikipedia.org/wiki/Cyclamen",datetime.now());
s12 = Species("Foxglove", "https://en.wikipedia.org/wiki/Digitalis",datetime.now());
s13 = Species("Frangipani", "https://en.wikipedia.org/wiki/Plumeria",datetime.now());
s14 = Species("Geranium", "https://en.wikipedia.org/wiki/Pelargonium",datetime.now());
s15 = Species("Hibiscus", "https://en.wikipedia.org/wiki/Hibiscus",datetime.now());
s16 = Species("Lotus", "https://en.wikipedia.org/wiki/Nelumbo_nucifera",datetime.now());
s17 = Species("Morning Glory", "https://en.wikipedia.org/wiki/Morning_glory",datetime.now());
s18 = Species("Passion Flower", "https://en.wikipedia.org/wiki/Passiflora",datetime.now());
s19 = Species("Petunia", "https://en.wikipedia.org/wiki/Petunia",datetime.now());
s20 = Species("Fritillary", "https://en.wiktionary.org/wiki/fritillary",datetime.now());

for s in [s11,s12,s13,s14,s15,s16,s17,s18,s19,s20]:
    db.session.add(s)

s21 = Species("Pink-yellow Dahlia", "https://en.wikipedia.org/wiki/Dahlia",datetime.now());
s22 = Species("Poinsettia", "https://en.wikipedia.org/wiki/Poinsettia",datetime.now());
s23 = Species("Primula", "https://en.wikipedia.org/wiki/Primula",datetime.now());
s24 = Species("Rose", "https://en.wikipedia.org/wiki/Rose",datetime.now());
s25 = Species("Snapdragon", "https://en.wikipedia.org/wiki/Antirrhinum",datetime.now());
s26 = Species("Sword Lily", "https://en.wikipedia.org/wiki/Gladiolus",datetime.now());
s27 = Species("Thorn Apple", "https://en.wikipedia.org/wiki/Datura_stramonium",datetime.now());
s28 = Species("Wallflower", "https://en.wikipedia.org/wiki/Erysimum_cheiri",datetime.now());
s29 = Species("Watercress", "https://en.wikipedia.org/wiki/Watercress",datetime.now());
s30 = Species("Water Lily", "https://en.wikipedia.org/wiki/Nymphaeaceae",datetime.now());

for s in [s21,s22,s23,s24,s25,s26,s27,s28,s29,s30]:
    db.session.add(s)


s31 = Species("Ammopiptanthus Mongolicus", "https://en.wikipedia.org/wiki/Ammopiptanthus",datetime.now());
s32 = Species("Brazilian Jasmine","https://en.wikipedia.org/wiki/Mandevilla_sanderi", datetime.now());
s33 = Species("Lily", "https://en.wikipedia.org/wiki/Lilium", datetime.now());
s34 = Species("Start Jasmine", "https://en.wikipedia.org/wiki/Trachelospermum_jasminoides", datetime.now());
s35 = Species("Sunflower", "https://en.wikipedia.org/wiki/Helianthus", datetime.now());
s36 = Species("Convolvulus", "https://en.wikipedia.org/wiki/Convolvulus", datetime.now());
s37 = Species("Hydrangea", "https://en.wikipedia.org/wiki/Hydrangea", datetime.now());
s38 = Species("Peony", "https://en.wikipedia.org/wiki/Peony", datetime.now());
s39 = Species("Orchid", "https://en.wikipedia.org/wiki/Orchidaceae", datetime.now());
s40 = Species("Guzmania lingulata", "https://en.wikipedia.org/wiki/Guzmania_lingulata", datetime.now());
s41 = Species("Oenothera speciosa", "https://en.wikipedia.org/wiki/Oenothera_speciosa",datetime.now());
s42 = Species("Hypericum patulum", "https://en.wikipedia.org/wiki/Hypericum_patulum",datetime.now());
s43 = Species("Chrysanthemum", "https://en.wikipedia.org/wiki/Chrysanthemum",datetime.now());
s44 = Species("Calendula Officinalis", "https://en.wikipedia.org/wiki/Calendula_officinalis",datetime.now());
s45 = Species("Lily of the Nile", "https://en.wikipedia.org/wiki/Agapanthus",datetime.now());

for s in [s31,s32,s33,s34,s35,s36,s37,s38,s39,s40,s41,s42,s43,s44,s45]:
    db.session.add(s)


db.session.commit()

# users = User.query.all()
# for ele in users:
#     print(ele)
# print("Done printing all users in db")
#
# records = LoginHistory.query.all()
# for record in records:
#     print(record)
# print("Done printing all login history in db")
#
#
# records = PredictHistory.query.all()
# for record in records:
#     print(record)
# print("Done printing all predict history in db")
#
records = Species.query.all()
for record in records:
    print(record)
print("Done printing all species in db")
