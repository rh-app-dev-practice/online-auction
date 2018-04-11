package red.sells.bidservice;

import org.infinispan.counter.EmbeddedCounterManagerFactory;
import org.infinispan.counter.api.*;
import org.infinispan.manager.EmbeddedCacheManager;
import org.keycloak.KeycloakPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.security.Principal;
import java.util.Date;
import java.util.UUID;

@RestController
public class BidServiceController {

    private int ALLOWED_BID_RETRIES = 100;

    private final EmbeddedCacheManager cacheManager;
    private final CounterManager counterManager;

    @Autowired
    public JmsTemplate jmsTemplate;

    public BidServiceController(EmbeddedCacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.counterManager = EmbeddedCounterManagerFactory.asCounterManager(cacheManager);


    }

    @GetMapping("/login")
    public String helloWorld() {

        return "Login ";
    }

    @GetMapping("/hi")
    public String helloWorld(Principal principal, @RequestAttribute String userId) {
        return "Hello World " + userId + "    " + principal.getName();
    }

    /**
     * Execute the bid against the counter and retry if there is a new price lower than the bid price.
     * @param counter The StrongCounter for the current auction
     * @param expected The expected current price
     * @param updated The new price for the executed bid
     * @param attempt The attempt number for retries, starting at 0
     */
    private void executeBid(StrongCounter counter, long expected, long updated, int attempt) {
        counter.compareAndSwap(expected, updated).whenComplete((result, throwable) -> {
            // "result" is the current state of the counter
            if(result == expected) {
                // TODO: Send messages
                //this.jmsTemplate.convertAndSend("example", bidPlacedEvent);

                // Completed Successfully
                return;
            }
            else if(result < updated && attempt < ALLOWED_BID_RETRIES) {
                // New bid present which is still lower then current bid
                // Retry up to ALLOWED_BID_RETRIES times, which would be an extreme scenario
                executeBid(counter, result, updated, attempt+1);
            }
            else {
                // Cannot successfully execute bid
                // TODO: Send failure message
                if(attempt >= ALLOWED_BID_RETRIES) {
                    // Too many retries
                }
                else {
                    // Was outbid during execution of the bid
                }
            }
        });
    }

    @PostMapping("/submit")
    public boolean submitBid(@RequestAttribute String userId, @RequestBody String string) {

        cacheManager.getCache("testCache").put("testKey", "testValue");
        System.out.println("Received value from cache: " + cacheManager.getCache("testCache").get("testKey"));

        UUID bidId = UUID.randomUUID();
        UUID auctionId = UUID.randomUUID();
        UUID listingId = UUID.randomUUID();
        UUID userID = UUID.randomUUID();
        Integer newPrice = 11;
        Integer currentPrice = 10;
        Date timestamp = new Date();

        if (!counterManager.isDefined(auctionId.toString())) {
            counterManager.defineCounter(auctionId.toString(), CounterConfiguration.builder(CounterType.UNBOUNDED_STRONG).initialValue(currentPrice).storage(Storage.VOLATILE).build());
        }

        StrongCounter counter = counterManager.getStrongCounter(auctionId.toString());

        executeBid(counter, currentPrice, newPrice, 0);


        ///////////////////


        Bid bid = new Bid(auctionId, newPrice, currentPrice);

        BidPlacedEvent bidPlacedEvent = new BidPlacedEvent(bidId, userID, timestamp, bid);

        time = System.currentTimeMillis();

        this.jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.setDeliveryDelay(5000);

        this.jmsTemplate.convertAndSend("example", bidPlacedEvent);


        return true;
    }

    long time;

    @JmsListener(destination = "example")
    public void processMsg(BidPlacedEvent bidPlacedEvent) {
        System.out.println("============= " + (System.currentTimeMillis() - time));
        System.out.println("============= Received: " + bidPlacedEvent);
    }

}
