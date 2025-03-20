package PNV.DareAndTruth.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.projection.reminder.ReminderSummaryProjection;
import PNV.DareAndTruth.dto.projection.user.UserWithIdProjection;
import PNV.DareAndTruth.dto.request.reminder.CreateReminderRequest;
import PNV.DareAndTruth.dto.request.reminder.UpdateReminderRequest;
import PNV.DareAndTruth.dto.response.hashtag.HashtagForDoChallengeResponse;
import PNV.DareAndTruth.entity.Reminder;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.mapper.ReminderMapper;
import PNV.DareAndTruth.repository.ReminderRepository;
import PNV.DareAndTruth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReminderService {
    UserRepository userRepository;
    ReminderRepository reminderRepository;
    ReminderMapper reminderMapper;
    ReminderNotificationService reminderNotificationService;

    private User getUserByEmail(String userEmail) {
        return userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public void createNewReminder(CreateReminderRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);

        Optional<Reminder> existingReminder =
                reminderRepository.findByUserAndTitleAndHashtagAndStartDateAndEndDateAndStartTimeAndEndTime(
                        user,
                        request.getTitle(),
                        request.getHashtag(),
                        request.getStartDate(),
                        request.getEndDate(),
                        request.getStartTime(),
                        request.getEndTime());
        if (existingReminder.isPresent()) {
            throw new AppException(ErrorCode.REMINDER_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        Reminder reminder = reminderMapper.toReminder(request, user);

        Reminder savedReminder = reminderRepository.save(reminder);

        // Nếu Reminder có thông báo trong ngày, lập lịch ngay
        LocalDate today = LocalDate.now();
        LocalTime triggerTime =
                reminder.getReminderTime() != null ? reminder.getReminderTime() : reminder.getStartTime();

        if (triggerTime != null && triggerTime.isAfter(LocalTime.now())) {
            log.info("Reminder Time: {}", triggerTime);
            LocalDateTime nextTime = LocalDateTime.of(today, triggerTime);
            if (nextTime.isAfter(LocalDateTime.now())) {
                reminderNotificationService.scheduleReminderNotification(savedReminder, nextTime);
            }
        }
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
        Reminder reminder = reminderRepository
                .findById(reminderId)
                .orElseThrow(() -> new AppException(ErrorCode.REMINDER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!reminder.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED, HttpStatus.BAD_REQUEST);
        }

        reminderMapper.updateReminderFromRequest(reminder, request);
        reminderRepository.save(reminder);
    }

    public void deleteReminder(UUID reminderId, String userEmail) {
        User user = getUserByEmail(userEmail);

        Reminder reminder = reminderRepository
                .findById(reminderId)
                .orElseThrow(() -> new AppException(ErrorCode.REMINDER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!reminder.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED, HttpStatus.BAD_REQUEST);
        }

        reminderRepository.deleteById(reminderId);
    }

    public List<HashtagForDoChallengeResponse> getHashtagsForUserToday(String userEmail) {

        Optional<UserWithIdProjection> exitingUser = userRepository.findByEmailAndIsDeletedFalse(userEmail);
        if (exitingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        LocalDate today = LocalDate.now(); // 2025-03-01
        LocalDateTime startOfDay = today.atStartOfDay(); // 2025-03-01 00:00:00

        return reminderRepository.findHashtagsForUserToday(exitingUser.get().getId(), today, startOfDay);
    }
}
