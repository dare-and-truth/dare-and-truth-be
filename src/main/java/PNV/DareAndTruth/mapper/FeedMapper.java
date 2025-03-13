package PNV.DareAndTruth.mapper;

import PNV.DareAndTruth.dto.response.feed.GetFeedResponse;
import org.mapstruct.Mapper;

import java.sql.Timestamp;
import java.util.UUID;

@Mapper
public interface FeedMapper {
    public default GetFeedResponse mapToFeedResponse(Object[] row) {
        return new GetFeedResponse(
                (UUID) row[0],
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (String) row[4],
                row[5] != null ? row[5].toString() : null,
                row[6] != null ? row[6].toString() : null,
                ((Timestamp) row[7]).toLocalDateTime(),
                (UUID) row[8],
                (String) row[9],
                (String) row[10],
                ((Number) row[11]).intValue(),
                ((Number) row[12]).intValue(),
                (Boolean) row[13],
                (Boolean) row[14]
        );
    }
}
