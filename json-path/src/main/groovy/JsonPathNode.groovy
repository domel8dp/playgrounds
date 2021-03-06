import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider

def JSON = '''
{
    "int": 123456,
    "str": "some text",
    "pi" : 3.14159265,
    "flag": true,
    "not_set": null,
    "array": [
        {"active":true, "selected":true},
        {"active":true, "selected":false}
    ]
}
'''
def JSON_ARRAY = '''
[
    {"active":true, "selected":true},
    {"active":true, "selected":false}
]
'''

def jsonParser = JsonPath.using(Configuration.defaultConfiguration()
        .jsonProvider(new JacksonJsonNodeJsonProvider())
        .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL))

def json = jsonParser.parse(JSON)
printClassAndValue(json, '$.int')
printClassAndValue(json, '$.str')
printClassAndValue(json, '$.pi')
printClassAndValue(json, '$.flag')
printClassAndValue(json, '$.not_set')
printClassAndValue(json, '$.missing')
printClassAndValue(json, '$.array.[0]')
printClassAndValue(json, '$.array.[*].selected')
printClassAndValue(json, '$.array.[*].missing')
def jsonArray = jsonParser.parse(JSON_ARRAY)
printClassAndValue(jsonArray, '$.[*].selected')
printClassAndValue(jsonArray, '$.[*].missing')

static def printClassAndValue(json, path) {
    try {
        println "### ${path}"
        def data = json.read(path)
        println data.getClass()
        println data
    } catch (Exception e) {
        e.printStackTrace()
    }
}
