package PNV.DareAndTruth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import PNV.DareAndTruth.dto.request.reminder.CreateReminderRequest;
import PNV.DareAndTruth.dto.request.reminder.UpdateReminderRequest;
import PNV.DareAndTruth.entity.Reminder;
import PNV.DareAndTruth.entity.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReminderMapper {

    default Reminder toReminder(CreateReminderRequest request, User user) {
        return Reminder.builder()
                .user(user)
                .title(request.getTitle())
                .hashtag(request.getHashtag())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reminderContent(request.getReminderContent())
                .reminderTime(request.getReminderTime())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
    }

    void updateReminderFromRequest(@MappingTarget Reminder reminder, UpdateReminderRequest request);
}
