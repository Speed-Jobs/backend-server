package ksh.backendserver.company.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DateRange {

    YEARLY(365),
    MONTHLY(30),
    WEEKLY(7),
    DAILY(1);

    private final int duration;
}
