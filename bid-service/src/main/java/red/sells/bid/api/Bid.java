package red.sells.bid.api;

import lombok.*;

import java.util.UUID;

@Value
public final class Bid {
    @NonNull private UUID auctionId;
    @NonNull private Integer currentPrice;
    @NonNull private Integer newPrice;
}
