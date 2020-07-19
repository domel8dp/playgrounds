import io.nats.client.Nats
import io.nats.client.Options

import java.nio.charset.StandardCharsets

def options = new Options.Builder()
// 'nats://ready2go@192.168.0.22:4222', 'nats://ready2go@192.168.0.22:4223'
        .servers(['nats://ready2go@172.17.0.2:4222', 'nats://ready2go@172.17.0.3:4222'] as String [])
        .connectionName('Request Client Connection')
        .build()

def rand = new Random()

try (def connection = Nats.connect(options)) {
    println "Request Client, Connection status: ${connection.getStatus()}"
    connection.request('pl.dpawlak.test', "Request(${rand.nextInt(Integer.MAX_VALUE)})".getBytes(StandardCharsets.UTF_8))
            .thenAccept((msg) -> {
                println "Response: ${new String(msg.data, StandardCharsets.UTF_8)}"
            })
            .exceptionally((ex) -> {
                println "Exception: ${ex}"
            })
}
println 'done!'
