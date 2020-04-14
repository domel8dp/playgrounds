import ch.qos.logback.classic.encoder.PatternLayoutEncoder

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%-40X{akkaSource}] %-5level - %msg%n"
//        pattern = "%d{HH:mm:ss.SSS} [%X{akkaSource}] %-5level %logger{36} - %msg%n"
    }
}
root(DEBUG, ["STDOUT"])