package red.sells.bid.api;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/api")
public interface BidApi {

    @GetMapping("/login")
    String helloWorld();

    @GetMapping("/hi")
    String helloWorld(Principal principal, String userId);

    @PostMapping("/submit")
    boolean submitBid(String userId, @RequestBody Bid bid);
}
