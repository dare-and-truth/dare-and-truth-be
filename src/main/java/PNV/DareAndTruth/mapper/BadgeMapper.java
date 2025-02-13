package PNV.DareAndTruth.mapper;

import org.mapstruct.Mapper;

import PNV.DareAndTruth.dto.request.badge.CreateBadgeRequest;
import PNV.DareAndTruth.entity.Badge;

@Mapper(componentModel = "spring")
public interface BadgeMapper {
    Badge createBadgeRequestToBadge(CreateBadgeRequest request);
}
