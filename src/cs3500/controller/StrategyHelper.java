package cs3500.controller;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for determining the end time of a scheduled event.
 */
public class StrategyHelper {

  protected static Map<LocalTime, Integer> calculateEndTime(int hour, int minute, int duration) {
    int extraDays = 0;
    hour += duration / 60;
    minute += duration % 60;

    if (minute >= 60) {
      hour += minute / 60;
      minute %= 60;
    }

    if (hour >= 24) {
      extraDays += hour / 24;
      hour %= 24;
    }

    LocalTime endTime = LocalTime.of(hour, minute);

    Map<LocalTime, Integer> result = new HashMap<>();
    result.put(endTime, extraDays);
    return result;
  }
}
