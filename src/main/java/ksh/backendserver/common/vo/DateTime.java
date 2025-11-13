package ksh.backendserver.common.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DateTime {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public static DateTime from(LocalDateTime dateTime) {
        return new DateTime(
            dateTime.getYear(),
            dateTime.getMonthValue(),
            dateTime.getDayOfMonth(),
            dateTime.getHour(),
            dateTime.getMinute(),
            dateTime.getSecond()
        );
    }
}