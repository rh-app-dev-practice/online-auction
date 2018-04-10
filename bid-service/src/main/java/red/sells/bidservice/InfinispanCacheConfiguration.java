package red.sells.bidservice;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.spring.starter.embedded.InfinispanCacheConfigurer;
import org.infinispan.spring.starter.embedded.InfinispanGlobalConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.infinispan.configuration.cache.Configuration;

import java.lang.invoke.MethodHandles;

@org.springframework.context.annotation.Configuration
public class InfinispanCacheConfiguration {

    private static final String CACHE_NAME = "test";

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * This bean is optional but it shows how to inject {@link org.infinispan.configuration.global.GlobalConfiguration}.
     */
    @Bean
    public InfinispanGlobalConfigurer globalConfiguration() {
        logger.info("Defining Global Configuration");
        return () -> GlobalConfigurationBuilder
                .defaultClusteredBuilder()
                .globalJmxStatistics().allowDuplicateDomains(true)
                .build();
    }

    /**
     * Here we inject {@link Configuration}.
     */
    @Bean
    public InfinispanCacheConfigurer cacheConfigurer() {
        logger.info("Defining {} configuration", CACHE_NAME);
        return manager -> {
            Configuration ispnConfig = new ConfigurationBuilder()
                    .clustering().cacheMode(CacheMode.DIST_SYNC)
                    .build();

            manager.defineConfiguration(CACHE_NAME, ispnConfig);
        };
    }
}
