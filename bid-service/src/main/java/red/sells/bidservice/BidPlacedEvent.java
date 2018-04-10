package red.sells.bidservice;

import lombok.NonNull;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Value
public final class BidPlacedEvent {
    @NonNull private UUID id;
    @NonNull private UUID userId;
    @NonNull private Date timestamp;
    @NonNull private Bid bid;
}