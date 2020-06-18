class RET:
    OK                  = "0"
    DBERR               = "4001"
    NODATA              = "4002"
    DATAEXIST           = "4003"
    DATAERR             = "4004"
    SESSIONERR          = "4101"
    LOGINERR            = "4102"
    PARAMERR            = "4103"
    USERERR             = "4104"
    # ROLEERR             = "4105"
    PWDERR              = "4106"
    REQERR              = "4201"
    IPERR               = "4202"
    THIRDERR            = "4301"
    IOERR               = "4302"
    SERVERERR           = "4500"
    UNKOWNERR           = "4501"

error_map = {
    RET.OK                    : u"Successful",
    RET.DBERR                 : u"Database Query Error",
    RET.NODATA                : u"No Data",
    RET.DATAEXIST             : u"Data Exists",
    RET.DATAERR               : u"Data Error",
    RET.SESSIONERR            : u"Not Login",
    RET.LOGINERR              : u"Login Error",
    RET.PARAMERR              : u"Parameter Error",
    RET.USERERR               : u"User Not Exist",
    RET.PWDERR                : u"Password Error",
    RET.REQERR                : u"Request Error",
    RET.IPERR                 : u"IP Error",
    RET.THIRDERR              : u"Third Party Error",
    RET.IOERR                 : u"I/O Error",
    RET.SERVERERR             : u"Server Error",
    RET.UNKOWNERR             : u"Unknown Error",
}
