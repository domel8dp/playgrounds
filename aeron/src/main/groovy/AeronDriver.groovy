import io.aeron.driver.MediaDriver

import java.util.concurrent.TimeUnit

println 'Launching driver'
try (def driver = MediaDriver.launch()) {
    println 'Waiting for STOP'
    sleep(TimeUnit.MINUTES.toMillis(2))
}
println 'done!'
