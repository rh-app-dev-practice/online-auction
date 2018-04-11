package red.sells.bid.service;

import org.infinispan.counter.EmbeddedCounterManagerFactory;
import org.infinispan.counter.api.*;
import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RestController;
import red.sells.bid.api.Bid;
import red.sells.bid.api.BidApi;
import red.sells.bid.event.BidPlacedEvent;

import java.security.Principal;
import java.util.Date;
import java.util.UUID;

@RestController
public class BidService implements BidApi {

    private int ALLOWED_BID_RETRIES = 10;

    private final EmbeddedCacheManager cacheManager;
    private final CounterManager counterManager;

    @Autowired
    public JmsTemplate jmsTemplate;

    public BidService(EmbeddedCacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.counterManager = EmbeddedCounterManagerFactory.asCounterManager(cacheManager);
    }

    @Override
    public String helloWorld() {
        return "Login ";
    }

    @Override
    public String helloWorld(Principal principal, String userId) {
        return "Hello World " + userId + "    " + principal.getName();
    }

    @Override
    public boolean submitBid(String userId, Bid bid) {

        cacheManager.getCache("testCache").put("testKey", "testValue");
        System.out.println("Received value from cache: " + cacheManager.getCache("testCache").get("testKey"));

        UUID bidId = UUID.randomUUID();
        UUID auctionId = UUID.randomUUID();
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


        BidPlacedEvent bidPlacedEvent = new BidPlacedEvent(bidId, userID, timestamp, bid);


        this.jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.setDeliveryDelay(5000);

        this.jmsTemplate.convertAndSend("example", bidPlacedEvent);


        return true;
    }

    @JmsListener(destination = "example")
    public void processMsg(BidPlacedEvent bidPlacedEvent) {
        System.out.println("============= Received: " + bidPlacedEvent);
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
            if(result != null && result == expected) {
                // TODO: Send messages
                //this.jmsTemplate.convertAndSend("example", bidPlacedEvent);

                // Completed Successfully
                return;
            }
            else if(result != null && result < updated && attempt < ALLOWED_BID_RETRIES) {
                // New bid present which is still lower then current bid
                // Retry up to ALLOWED_BID_RETRIES times, which would be an extreme scenario
                executeBid(counter, result, updated, attempt+1);
                return;
            }
            else { // Failure cases
                // Cannot successfully execute bid
                // TODO: Send failure message
                if(result == null) {
                    // Handle throwable
                }
                else if(attempt >= ALLOWED_BID_RETRIES) {
                    // Too many retries
                }
                else {
                    // Was outbid during execution of the bid
                }
            }
        });
    }



}
