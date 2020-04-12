import org.snmp4j.PDU
import org.snmp4j.Snmp
import org.snmp4j.event.ResponseEvent
import org.snmp4j.smi.OID
import org.snmp4j.smi.VariableBinding
import org.snmp4j.transport.DefaultUdpTransportMapping

def config = new ConfigSlurper().parse(new File('snmp_config.groovy').toURI().toURL())
def target = Utils.createTarget(config)

Snmp snmp = new Snmp(new DefaultUdpTransportMapping())
snmp.listen()

PDU pdu = new PDU()
pdu.with {
    setType(GET)
    add(new VariableBinding(new OID("1.3.6.1.2.1.1.1.0"))) // sysDescr
    add(new VariableBinding(new OID("${config.snmp.baseOid}.1.2.0")))
}

ResponseEvent response = snmp.send(pdu, target)
println response.getResponse() ? "Received response: ${response.getResponse()}" : 'TIMEOUT'

println 'closing'
snmp.close()
