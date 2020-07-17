import org.snmp4j.CommunityTarget
import org.snmp4j.UserTarget
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.security.SecurityLevel
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

    static def createV3Target(config) {
        UserTarget target = new UserTarget();
        target.setAddress(config.snmp.address);
        target.setTimeout(500);
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
        target.setSecurityName(new OctetString("MD5DES"));
        return target
    }
}
