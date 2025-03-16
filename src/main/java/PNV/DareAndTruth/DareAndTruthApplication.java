package PNV.DareAndTruth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@EnableMongoRepositories
@EnableRetry
public class DareAndTruthApplication {

    public static void main(String[] args) {
        SpringApplication.run(DareAndTruthApplication.class, args);
    }
}
