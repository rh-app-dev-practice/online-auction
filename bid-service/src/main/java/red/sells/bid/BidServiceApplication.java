package red.sells.bid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import red.sells.bid.security.AccessTokenFilter;

@SpringBootApplication
public class BidServiceApplication {



	public static void main(String[] args) {
		SpringApplication.run(BidServiceApplication.class, args);
	}
}
