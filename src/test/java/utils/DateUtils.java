package utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    public static long getCountOfDaysForDates(String startDate, String endDate) {
        LocalDate startDay = LocalDate.parse(startDate);
        LocalDate endDay = LocalDate.parse(endDate);

        return ChronoUnit.DAYS.between(startDay, endDay) + 1;
    }
}
