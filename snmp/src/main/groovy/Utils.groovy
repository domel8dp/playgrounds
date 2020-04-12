import org.snmp4j.CommunityTarget
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.smi.OctetString
import org.snmp4j.smi.UdpAddress

import java.util.concurrent.TimeUnit

class Utils {
    static def createTarget(config) {
        CommunityTarget target = new CommunityTarget()
        target.with {
            setCommunity(new OctetString(config.snmp.community))
            setAddress(UdpAddress.parse(config.snmp.address))
            setVersion(SnmpConstants.version2c)
            setTimeout(TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS))
            setRetries(1)
        }
        return target
    }
}
