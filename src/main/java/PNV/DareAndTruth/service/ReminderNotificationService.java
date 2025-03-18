package PNV.DareAndTruth.service;

import PNV.DareAndTruth.entity.Reminder;
import PNV.DareAndTruth.repository.ReminderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReminderNotificationService {
    final ReminderRepository reminderRepository;

    final NotificationService notificationService;

    final TaskScheduler taskScheduler;


    @Value("${app.timezone:Asia/Ho_Chi_Minh}")
    private String timezone;

    // Chạy vào 00:00 mỗi ngày để lên lịch các reminder trong ngày
    @Scheduled(cron = "0 0 0 * * ?") // Chạy lúc 00:00 mỗi ngày
    public void scheduleDailyReminders() {
        LocalDate today = LocalDate.now();
        List<Reminder> reminders = reminderRepository.findByEndDateGreaterThanEqual(today);

        for (Reminder reminder : reminders) {
            LocalTime triggerTime = reminder.getReminderTime() != null ? reminder.getReminderTime() : reminder.getStartTime();
            if (triggerTime == null) continue;

            LocalDateTime nextTime = LocalDateTime.of(today, triggerTime);
            if (nextTime.isAfter(LocalDateTime.now())) {
                scheduleReminderNotification(reminder, nextTime);
            }
        }
    }

    // Lập lịch gửi thông báo cho một Reminder tại một thời điểm cụ thể
    public void scheduleReminderNotification(Reminder reminder, LocalDateTime nextTime) {
        ZoneId zoneId = ZoneId.of(timezone);
        taskScheduler.schedule(
                () -> notificationService.sendNotification(reminder, reminder.getUser()),
                nextTime.atZone(zoneId).toInstant()
        );
    }
}
