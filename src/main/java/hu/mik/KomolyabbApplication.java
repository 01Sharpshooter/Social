package hu.mik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@ServletComponentScan
@ComponentScan
@EnableCaching
@SpringBootApplication
public class KomolyabbApplication {

	public static void main(String[] args) {
		SpringApplication.run(KomolyabbApplication.class, args);
	}
}
