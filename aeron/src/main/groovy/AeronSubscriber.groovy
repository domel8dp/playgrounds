import io.aeron.Aeron
import io.aeron.CommonContext
import io.aeron.FragmentAssembler
import org.agrona.concurrent.BackoffIdleStrategy

import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

def CHANNEL = CommonContext.IPC_CHANNEL
def STREAM_ID = 1

println 'Connecting Subscriber'
try (def aeron = Aeron.connect(new Aeron.Context())) {
    println "Subscribing on stream: ${STREAM_ID}"
    def subscription = aeron.addSubscription(CHANNEL, STREAM_ID)

    final fragmentHandler = (buffer, offset, length, header) -> {
        final byte[] data = new byte[length];
        buffer.getBytes(offset, data);

        println "Message from session ${header.sessionId()} (${length}@${offset}) <<${new String(data, StandardCharsets.UTF_8)}>>"
    }
    def fragmentAssembler = new FragmentAssembler(fragmentHandler)
    def idleStrategy = new BackoffIdleStrategy(
            100, 10, TimeUnit.MICROSECONDS.toNanos(1), TimeUnit.MICROSECONDS.toNanos(100))

    println 'Starting loop'
    while (!Thread.currentThread().isInterrupted()) {
        def fragmentsRead = subscription.poll(fragmentAssembler, 10)
        idleStrategy.idle(fragmentsRead)
    }
}
println 'done!'
