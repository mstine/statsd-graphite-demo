package com.mattstine.mmlom;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.readytalk.metrics.StatsDReporter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RestController
public class StatsdGraphiteDemoApplication implements CommandLineRunner {

    Log log = LogFactory.getLog(StatsdGraphiteDemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(StatsdGraphiteDemoApplication.class, args);
    }

    @Autowired
    MetricRegistry metricRegistry;

    @Bean
    StatsDReporter statsDReporter() {
        return StatsDReporter.forRegistry(metricRegistry)
                .prefixedWith("springboot.metrics")
                .build("192.168.99.100", 8125);
    }

    @Override
    public void run(String... strings) throws Exception {
        metricRegistry.registerAll(new MemoryUsageGaugeSet());
        metricRegistry.registerAll(new ThreadStatesGaugeSet());
        metricRegistry.registerAll(new GarbageCollectorMetricSet());
        metricRegistry.registerAll(new ClassLoadingGaugeSet());

        statsDReporter().start(10, TimeUnit.SECONDS);
    }

    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

}
