import io.nats.client.Nats
import io.nats.client.Options

import java.nio.charset.StandardCharsets
import java.util.concurrent.CountDownLatch

def options = new Options.Builder()
        .servers(['nats://172.17.0.2:4222', 'nats://172.17.0.3:4222'] as String [])
        .connectionName('Server Connection')
        .build()

def latch = new CountDownLatch(20)

try (def connection = Nats.connect(options)) {
    println "Server, Connection status: ${connection.getStatus()}"
    def dispatcher = connection.createDispatcher((msg) -> {
        println "Received ${new String(msg.data, StandardCharsets.UTF_8)}, ACKING"
        if (latch.count % 3 == 0) {
            connection.publish(msg.replyTo, 'ACK'.getBytes(StandardCharsets.UTF_8))
        } else {
            println 'Forgot to ACK'
        }
        latch.countDown()
    })
    dispatcher.subscribe('pl.dpawlak.test', 'queue-group')
    latch.await()
}
println 'done!'