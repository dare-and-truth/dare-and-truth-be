package PNV.DareAndTruth.service;

import PNV.DareAndTruth.dto.projection.calendar.CalendarSummaryProjection;
import PNV.DareAndTruth.dto.request.calendar.CreateCalendarRequest;
import PNV.DareAndTruth.dto.request.calendar.UpdateCalendarRequest;
import PNV.DareAndTruth.entity.Calendar;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.CalendarRepository;
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
import java.util.Set;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CalendarService {
    UserRepository userRepository;
    CalendarRepository calendarRepository;


    private User getUserByEmail(String userEmail) {
        return userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public void createNewCalendar(@Valid CreateCalendarRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);

        Optional<Calendar> existingCalendar = calendarRepository.findByUserAndTitleAndStartDateAndEndDateAndStartTimeAndEndTimeAndRepeatTypeAndIsChallenge(
                user, request.getTitle(), request.getStartDate(), request.getEndDate(),
                request.getStartTime(), request.getEndTime(), request.getRepeatType(), request.getIsChallenge());
        if (existingCalendar.isPresent()) {
            throw new AppException(ErrorCode.CALENDAR_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        Calendar calendar = Calendar.builder()
                .user(user)
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .repeatType(request.getRepeatType())
                .isChallenge(request.getIsChallenge())
                .build();

        calendarRepository.save(calendar);
    }

    public List<CalendarSummaryProjection> getCalendarsByDayAndUserId(String dateStr, String userEmail) {
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
        return calendarRepository.findByUserAndDate(user, date);
    }

    public void updateCalendar(UUID calendarId, UpdateCalendarRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new AppException(ErrorCode.CALENDAR_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!calendar.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED, HttpStatus.BAD_REQUEST);
        }

        if (request.getTitle() != null) calendar.setTitle(request.getTitle());
        if (request.getStartDate() != null) calendar.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) calendar.setEndDate(request.getEndDate());
        if (request.getStartTime() != null) calendar.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) calendar.setEndTime(request.getEndTime());
        if (request.getRepeatType() != null) calendar.setRepeatType(request.getRepeatType());
        if (request.getIsChallenge() != null) calendar.setIsChallenge(request.getIsChallenge());

        calendarRepository.save(calendar);
    }

    public void deleteCalendar(UUID calendarId, String userEmail) {
        User user = getUserByEmail(userEmail);

        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new AppException(ErrorCode.CALENDAR_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!calendar.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED, HttpStatus.BAD_REQUEST);
        }

    }
}
