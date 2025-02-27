package PNV.DareAndTruth.service;

import PNV.DareAndTruth.dto.projection.reminder.ReminderSummaryProjection;
import PNV.DareAndTruth.dto.request.reminder.CreateReminderRequest;
import PNV.DareAndTruth.dto.request.reminder.UpdateReminderRequest;
import PNV.DareAndTruth.entity.Reminder;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.ReminderRepository;
import PNV.DareAndTruth.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReminderService {
    UserRepository userRepository;
    ReminderRepository reminderRepository;

    private User getUserByEmail(String userEmail) {
        return userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public void createNewReminder(@Valid CreateReminderRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);

        Optional<Reminder> existingReminder = reminderRepository.findByUserAndTitleAndHashtagAndStartDateAndEndDateAndStartTimeAndEndTime(
                user, request.getTitle(), request.getHashtag(), request.getStartDate(),
                request.getEndDate(), request.getStartTime(),request.getEndTime());
        if (existingReminder.isPresent()) {
            throw new AppException(ErrorCode.REMINDER_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        Reminder reminder = Reminder.builder()
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

        reminderRepository.save(reminder);
    }

    public List<ReminderSummaryProjection> getRemindersByDayAndUserId(String dateStr, String userEmail) {
        if (dateStr == null) {
            throw new AppException(ErrorCode.INVALID_DATE_FORMAT, HttpStatus.BAD_REQUEST);
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new AppException(ErrorCode.INVALID_DATE_FORMAT, HttpStatus.BAD_REQUEST);
        }

        User user = getUserByEmail(userEmail);
        return reminderRepository.findByUserAndDate(user, date);
    }

    public void updateReminder(UUID reminderId, UpdateReminderRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new AppException(ErrorCode.REMINDER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!reminder.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED, HttpStatus.BAD_REQUEST);
        }

        if (request.getTitle() != null) reminder.setTitle(request.getTitle());
        if (request.getHashtag() != null) reminder.setHashtag(request.getHashtag());
        if (request.getStartDate() != null) reminder.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) reminder.setEndDate(request.getEndDate());
        if (request.getReminderContent() != null) reminder.setReminderContent(request.getReminderContent());
        if (request.getReminderTime() != null) reminder.setReminderTime(request.getReminderTime());
        if (request.getStartTime() != null) reminder.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) reminder.setEndTime(request.getEndTime());

        reminderRepository.save(reminder);
    }

    public void deleteReminder(UUID reminderId, String userEmail) {
        User user = getUserByEmail(userEmail);

        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new AppException(ErrorCode.REMINDER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!reminder.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED, HttpStatus.BAD_REQUEST);
        }

        reminderRepository.deleteById(reminderId);
    }
}