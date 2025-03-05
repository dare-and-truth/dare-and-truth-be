package PNV.DareAndTruth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DareAndTruthApplication {

    public static void main(String[] args) {
        SpringApplication.run(DareAndTruthApplication.class, args);
    }
}
