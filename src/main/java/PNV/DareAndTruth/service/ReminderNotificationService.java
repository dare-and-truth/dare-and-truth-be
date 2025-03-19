package PNV.DareAndTruth.service;

import java.time.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import PNV.DareAndTruth.entity.Reminder;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.repository.ReminderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReminderNotificationService {
    final ReminderRepository reminderRepository;
    final TaskScheduler taskScheduler;
    final FirebaseMessaging firebaseMessaging;

    @Value("${app.timezone:Asia/Ho_Chi_Minh}")
    private String timezone;

    // Chạy vào 00:00 mỗi ngày để lên lịch các reminder trong ngày
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleDailyReminders() {
        LocalDate today = LocalDate.now();
        List<Reminder> reminders = reminderRepository.findByEndDateGreaterThanEqual(today);

        for (Reminder reminder : reminders) {
            LocalTime triggerTime =
                    reminder.getReminderTime() != null ? reminder.getReminderTime() : reminder.getStartTime();
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
                () -> sendNotification(reminder, reminder.getUser()),
                nextTime.atZone(zoneId).toInstant());
    }

    public void sendNotification(Reminder reminder, User receiver) {
        try {
            String fcmToken = receiver.getFcmToken();
            if (fcmToken != null && !fcmToken.isEmpty()) {
                Message message = Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle(reminder.getTitle())
                                .setBody(reminder.getReminderContent())
                                .build())
                        .setToken(fcmToken)
                        .build();

                String response = firebaseMessaging.send(message);
            } else {
                log.warn("⚠️ User ID: {} has no FCM token", receiver.getId());
            }
        } catch (FirebaseMessagingException e) {
            log.error("❌ Error sending push notification: {}", e.getMessage());
        }
    }
}
