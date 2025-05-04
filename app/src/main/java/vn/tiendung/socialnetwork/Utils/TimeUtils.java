package vn.tiendung.socialnetwork.Utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static String formatRelativeTime(String createdAt) {
        try {
            // Định dạng ISO 8601 từ backend
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime createdTime = LocalDateTime.parse(createdAt, formatter);

            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(createdTime, now);

            if (duration.toMinutes() < 1) {
                return "Vừa xong";
            } else if (duration.toMinutes() < 60) {
                return duration.toMinutes() + " phút trước";
            } else if (duration.toHours() < 24) {
                return duration.toHours() + " giờ trước";
            } else if (duration.toDays() < 7) {
                return duration.toDays() + " ngày trước";
            } else if (duration.toDays() < 30) {
                return (duration.toDays() / 7) + " tuần trước";
            } else {
                return (duration.toDays() / 30) + " tháng trước";
            }

        } catch (Exception e) {
            return "Không xác định";
        }
    }

}

