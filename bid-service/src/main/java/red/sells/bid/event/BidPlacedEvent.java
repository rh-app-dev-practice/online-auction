package red.sells.bid.event;

import lombok.NonNull;
import lombok.Value;
import red.sells.bid.api.Bid;

import java.util.Date;
import java.util.UUID;

@Value
public final class BidPlacedEvent {
    @NonNull private UUID id;
    @NonNull private UUID userId;
    @NonNull private Date timestamp;
    @NonNull private Bid bid;
}