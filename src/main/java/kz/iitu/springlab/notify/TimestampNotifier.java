package kz.iitu.springlab.notify;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component("timestamp")
@Order(3)
public class TimestampNotifier implements Notifier {

    private static final Logger log = LoggerFactory.getLogger(TimestampNotifier.class);

    @PostConstruct
    public void init() {
        log.info("TimestampNotifier initialized!");
    }

    @Override
    public String send(String message) {
        return Instant.now().toString() + ": " + message;
    }

    @Override
    public String channel() {
        return "timestamp";
    }
}