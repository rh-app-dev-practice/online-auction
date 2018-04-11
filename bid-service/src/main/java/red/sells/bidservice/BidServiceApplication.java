package red.sells.bidservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BidServiceApplication {

	@Bean
	public AccessTokenFilter authFilter() {
		return new AccessTokenFilter();
	}

	public static void main(String[] args) {
		SpringApplication.run(BidServiceApplication.class, args);
	}
}
