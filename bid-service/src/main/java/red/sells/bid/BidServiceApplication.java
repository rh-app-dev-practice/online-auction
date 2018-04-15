package red.sells.bid;

import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.spring.starter.remote.InfinispanRemoteConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BidServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(BidServiceApplication.class, args);
	}
}
