import org.snmp4j.MessageDispatcherImpl
import org.snmp4j.PDU
import org.snmp4j.Snmp
import org.snmp4j.event.ResponseEvent
import org.snmp4j.event.ResponseListener
import org.snmp4j.mp.MPv2c
import org.snmp4j.smi.OID
import org.snmp4j.smi.VariableBinding
import org.snmp4j.transport.DefaultUdpTransportMapping
import org.snmp4j.util.MultiThreadedMessageDispatcher
import org.snmp4j.util.ThreadPool

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

def threadsCount = 5
def requestsCount = 10
def latch = new CountDownLatch(requestsCount)
def config = new ConfigSlurper().parse(new File('snmp_config.groovy').toURI().toURL())
def target = Utils.createTarget(config)

def tp = ThreadPool.create("snmp4j-ThreadPool", threadsCount)
//*
def md = new MessageDispatcherImpl()
md.addMessageProcessingModel(new MPv2c());
Snmp snmp = new Snmp(new MultiThreadedMessageDispatcher(tp, md), new DefaultUdpTransportMapping())
//*/
//Snmp snmp = new Snmp(new DefaultUdpTransportMapping())
snmp.listen()

requestsCount.times {
    PDU pdu = new PDU()
    pdu.with {
        setType(GET)
        add(new VariableBinding(new OID("1.3.6.1.2.1.1.1.0"))) // sysDescr
        add(new VariableBinding(new OID("${config.snmp.baseOid}.1.2.0")))
    }

    ResponseListener listener = new ResponseListener() {
        // this code is run on the thread pool
        void onResponse(ResponseEvent event) {
            // ALWAYS CANCEL!
            ((Snmp) event.getSource()).cancel(event.getRequest(), this)
            PDU response = event.getResponse()
            println "[${Thread.currentThread()}] ${response ? "Received response: ${response}" : 'TIMEOUT'}"
            Thread.sleep(TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS))
            latch.countDown()
        }
    }

    snmp.send(pdu, target, null, listener)
}

latch.await()

println 'closing'
snmp.close()
tp.stop()
println 'script end'
