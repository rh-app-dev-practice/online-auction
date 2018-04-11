package red.sells.bid.api;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/api")
public interface BidServiceApi {

    @GetMapping("/login")
    String helloWorld();

    @GetMapping("/hi")
    String helloWorld(Principal principal, @RequestAttribute String userId);

    @PostMapping("/submit")
    boolean submitBid(@RequestAttribute String userId, @RequestBody Bid bid);
}
