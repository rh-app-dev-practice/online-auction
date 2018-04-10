package red.sells.bidservice;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Value
public final class Bid {
    @NonNull private UUID auctionId;
    @NonNull private Integer price;
    @NonNull private Integer currentPrice;
}
