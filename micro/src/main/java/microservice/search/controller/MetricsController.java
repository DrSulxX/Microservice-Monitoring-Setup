package microservice.search.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class MetricsController {

    private final MeterRegistry meterRegistry;
    private final Counter apiRequestsCounter;  // Counter, mis loendab päringud

    public MetricsController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        // Loome Counteri, mis loendab päringute arvu
        this.apiRequestsCounter = meterRegistry.counter("api_requests_total");
    }

    @GetMapping("/metrics/search")
    public String getMetrics() {
        // Kasutame olemasolevat timerit
        Timer timer = meterRegistry.find("api.requests").timer();

        // Kasutame apiRequestsCounter'it, et saada päringute arv
        apiRequestsCounter.increment();  // Iga päring suurendab loendurit

        // Tagastame andmed õigel kujul koos EOF lõpetusega
        return String.format("api_requests_total %d\napi_requests_duration_seconds %.2f\n# EOF",
                             (long) apiRequestsCounter.count(),  // Kasutame Counteri väärtust päringute arvu jaoks
                             timer != null ? timer.totalTime(TimeUnit.SECONDS) : 0.0);  // Kui timer olemas, tagastame kogu aja, vastasel juhul 0
    }
}