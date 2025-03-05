package PNV.DareAndTruth.mapper;

import PNV.DareAndTruth.dto.response.notification.NotificationResponse;
import PNV.DareAndTruth.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(source = "sender", target = "sender", qualifiedByName = "mapUserToUserDTO")
    NotificationResponse toNotificationResponse(Notification notification);

    @Named("mapUserToUserDTO")
    default NotificationResponse.UserDTO mapUserToUserDTO(User user) {
        if (user == null) {
            return null;
        }
        NotificationResponse.UserDTO userDTO = new NotificationResponse.UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        return userDTO;
    }

    @Named("mapPostToPostDTO")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "hashtag", target = "hashtag")
    default NotificationResponse.PostDTO mapPostToPostDTO(Post post) {
        if (post == null) {
            return null;
        }
        NotificationResponse.PostDTO postDTO = new NotificationResponse.PostDTO();
        postDTO.setId(post.getId());
        postDTO.setHashtag(post.getHashtag());
        return postDTO;
    }

    @Named("mapChallengeToDTO")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "hashtag", target = "hashtag")
    default NotificationResponse.ChallengeDTO mapChallengeToDTO(Challenge challenge) {
        if (challenge == null) {
            return null;
        }
        NotificationResponse.ChallengeDTO challengeDTO = new NotificationResponse.ChallengeDTO();
        challengeDTO.setId(challenge.getId());
        challengeDTO.setHashtag(challenge.getHashtag());
        return challengeDTO;
    }

    @Named("mapRequestToDTO")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "hashtag", target = "hashtag")
    default NotificationResponse.RequestDTO mapRequestToDTO(Request request) {
        if (request == null) {
            return null;
        }
        NotificationResponse.RequestDTO requestDTO = new NotificationResponse.RequestDTO();
        requestDTO.setId(request.getId());
        return requestDTO;
    }

    @Named("mapReminderToDTO")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    default NotificationResponse.ReminderDTO mapReminderToDTO(Reminder reminder) {
        if (reminder == null) {
            return null;
        }
        NotificationResponse.ReminderDTO reminderDTO = new NotificationResponse.ReminderDTO();
        reminderDTO.setId(reminder.getId());
        reminderDTO.setTitle(reminder.getTitle());
        reminderDTO.setContent(reminder.getReminderContent());
        return reminderDTO;
    }
}