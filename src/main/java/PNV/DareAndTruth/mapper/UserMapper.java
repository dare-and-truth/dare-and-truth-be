package PNV.DareAndTruth.mapper;

import PNV.DareAndTruth.dto.request.auth.SignUpRequest;
import PNV.DareAndTruth.dto.request.user.UpdateUserRequest;
import PNV.DareAndTruth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User signUpRequestToUser(SignUpRequest signUpRequest);

    void mapUserFromUpdateUserRequest(@MappingTarget User user, UpdateUserRequest request);
}
