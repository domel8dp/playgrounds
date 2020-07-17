import com.fasterxml.jackson.databind.ObjectMapper

def d = new Data(strValue: 'a str value', dblValue: Math.PI, bigDValue: new BigDecimal(Math.E), intValue: 777, boolValue: true)
println d
def m = new ObjectMapper()
def json = m.writeValueAsString(d)
println json
def d1 = m.readValue(json, Data)
d1.boolValue = false
println d1
println m.writeValueAsString(d1)

