package red.sells.bidservice;

import org.infinispan.counter.EmbeddedCounterManagerFactory;
import org.infinispan.counter.api.*;
import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Date;
import java.util.UUID;

@RestController
public class BidServiceController {

    private final EmbeddedCacheManager cacheManager;
    private final CounterManager counterManager;

    @Autowired
    public JmsTemplate jmsTemplate;

    public BidServiceController(EmbeddedCacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.counterManager = EmbeddedCounterManagerFactory.asCounterManager(cacheManager);


        if (!counterManager.isDefined("someCounter")) {
            counterManager.defineCounter("someCounter", CounterConfiguration.builder(CounterType.UNBOUNDED_STRONG).initialValue(1).storage(Storage.VOLATILE).build());
        }
    }

    @GetMapping("/")
    public String helloWorld() {



        return "Hello World";
    }

    @PostMapping("/submit")
    public boolean submitBid(@RequestBody String string) {

        cacheManager.getCache("testCache").put("testKey", "testValue");
        System.out.println("Received value from cache: " + cacheManager.getCache("testCache").get("testKey"));


        StrongCounter aCounter = counterManager.getStrongCounter("someCounter");


        aCounter.incrementAndGet().whenComplete((result, throwable) -> System.out.println("Counter: " + result));

        UUID id = UUID.randomUUID();
        UUID auctionId = UUID.randomUUID();
        UUID listingId = UUID.randomUUID();
        UUID userID = UUID.randomUUID();
        Integer price = 11;
        Integer currentPrice = 11;
        Date timestamp = new Date();

        Bid bid = new Bid(auctionId, price, currentPrice);

        BidPlacedEvent bidPlacedEvent = new BidPlacedEvent(id, userID, timestamp, bid);

        time = System.currentTimeMillis();
        this.jmsTemplate.setDeliveryPersistent(true);
        //this.jmsTemplate.convertAndSend("example", bidPlacedEvent);

        jmsTemplate.convertAndSend("example", bidPlacedEvent, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException {



message.setStringProperty("JMS_AMQP_MESSAGE_ANNOTATIONS", "{ \"x-opt-delivery-time\": " + System.currentTimeMillis() + 5000 + "} ");

                //message.setLongProperty("x-opt-delivery-time", );
                return message;
            }
        });


        return true;
    }

    long time;

    @JmsListener(destination = "example")
    public void processMsg(BidPlacedEvent bidPlacedEvent) {
        System.out.println("============= " + (System.currentTimeMillis() - time));
        System.out.println("============= Received: " + bidPlacedEvent);
    }

}
