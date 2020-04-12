import org.snmp4j.PDU
import org.snmp4j.Snmp
import org.snmp4j.event.ResponseEvent
import org.snmp4j.event.ResponseListener
import org.snmp4j.smi.OID
import org.snmp4j.smi.VariableBinding
import org.snmp4j.transport.DefaultUdpTransportMapping

import java.util.concurrent.Semaphore

def sem = new Semaphore(1)
def config = new ConfigSlurper().parse(new File('snmp_config.groovy').toURI().toURL())
def target = Utils.createTarget(config)

Snmp snmp = new Snmp(new DefaultUdpTransportMapping())
snmp.listen()

PDU pdu = new PDU()
pdu.setType(PDU.GET)
def oidsCount = 55
def oidsInGroup = [19, 5, 7, 0, 20, 11, 0]
oidsInGroup.eachWithIndex { int oids, int i ->
    (1..oids).each {
        if (oidsCount-- > 0) {
            pdu.add(new VariableBinding(new OID("${config.snmp.baseOid}.${i + 1}.${it}.0")))
        }
    }
}

println "Request bindings size: ${pdu.getVariableBindings().size()}, PDU size: ${pdu.getBERLength()}"

ResponseListener listener = new ResponseListener() {
    void onResponse(ResponseEvent event) {
        // ALWAYS CANCEL!
        ((Snmp)event.getSource()).cancel(event.getRequest(), this)
        PDU response = event.getResponse()
        println response ? "Received response (size: ${response.getBERLength()}): ${response}" : 'TIMEOUT'
        println 'closing'
        snmp.close()
        sem.release()
    }
}

sem.acquire()
snmp.send(pdu, target, null, listener)
sem.acquire()
println 'script end'
