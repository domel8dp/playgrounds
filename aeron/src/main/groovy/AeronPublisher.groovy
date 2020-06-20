import io.aeron.Aeron
import io.aeron.CommonContext
import org.agrona.concurrent.UnsafeBuffer

import java.nio.charset.StandardCharsets

import static org.agrona.BufferUtil.allocateDirectAligned

def CHANNEL = CommonContext.IPC_CHANNEL
def STREAM_ID = 1
def BUFFER = new UnsafeBuffer(allocateDirectAligned(256, 64))

println 'Connecting Publisher'
try (def aeron = Aeron.connect(new Aeron.Context())) {
    println "Publishing on stream: ${STREAM_ID}"
    def publication = aeron.addPublication(CHANNEL, STREAM_ID)

    (0..10).each {
        def msg = "Message(${it})"
        def bytes = msg.getBytes(StandardCharsets.UTF_8)
        BUFFER.putBytes(0, bytes)
        println "Sending ${msg}"
        long resultingPosition = publication.offer(BUFFER, 0, bytes.length)
        if (resultingPosition <= 0) {
            println "Error: ${resultingPosition}"
        }
    }
}
println 'done!'
