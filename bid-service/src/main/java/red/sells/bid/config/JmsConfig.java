package red.sells.bid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.stereotype.Component;

@Component
@EnableJms
public class JmsConfig {
    /**
     * Serialize message content to JSON using TextMessage. This avoids using the Java serializer which would tightly
     * couple the microservices, where using Jackson can decouple the message serialization.
     * @return The MappingJackson2MessageConverter
     * @see <a href="https://spring.io/guides/gs/messaging-jms">Spring Messaging with JMS</a>
     */
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
