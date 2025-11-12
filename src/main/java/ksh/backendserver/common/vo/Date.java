package ksh.backendserver.common.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class Date {

    private int year;
    private int month;
    private int day;

    public static Date from(LocalDate date) {
        return new Date(
            date.getYear(),
            date.getMonthValue(),
            date.getDayOfMonth()
        );
    }
}
