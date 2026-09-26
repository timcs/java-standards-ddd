package top.binaryx.common.util;

import com.github.f4b6a3.uuid.UuidCreator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class UUIDUtil {
    public static UUID getTimeOrdered() {
        return UuidCreator.getTimeOrdered();
    }

    public static UUID getTimeOrderedEpoch() {
        return UuidCreator.getTimeOrderedEpoch();
    }

    public static UUID getTimeOrderedEpoch(String localDateTime) {
        return UuidCreator.getTimeOrderedEpoch(Instant.parse(localDateTime));
    }
}
