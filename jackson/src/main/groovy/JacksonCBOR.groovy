import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory

def d = new Data(strValue: 'a str value', dblValue: Math.PI, bigDValue: new BigDecimal(Math.E), intValue: 777, boolValue: true)
println d
def m = new ObjectMapper(new CBORFactory())
def bytes = m.writeValueAsBytes(d)
println "CBOR bytes: ${bytes.length}"
def d1 = m.readValue(bytes, Data)
d1.boolValue = false
println d1