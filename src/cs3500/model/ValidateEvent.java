package cs3500.model;

import java.time.LocalTime;
import java.util.List;

/**
 * Helper class for validating that an event does not overlap with another.
 */
public class ValidateEvent {

  /**
   * Helper method for validating an event, or for an individual user to verify their own schedule.
   *
   * @param evStart      the start day of the proposed event
   * @param evEnd        the end day of the proposed event
   * @param evStartTime  the start time of the proposed event
   * @param evEndTime    the end time of the proposed event
   * @param schStart     the start day of the scheduled event
   * @param schEnd       the end day of the scheduled event
   * @param schStartTime start time of the scheduled event
   * @param schEndTime   end time of the scheduled event
   * @return true if the event is valid, false otherwise
   */
  protected static boolean validTime(int evStart, int evEnd,
                                     LocalTime evStartTime, LocalTime evEndTime,
                                     int schStart, int schEnd,
                                     LocalTime schStartTime, LocalTime schEndTime) {

    if (evStart == evEnd && schStart == schEnd && evStart != schStart
            && evStartTime.compareTo(evEndTime) < 0 && schStartTime.compareTo(schEndTime) < 0) {
      return true;
    }
    return (schEnd < evStart
            || (schEnd == evStart && schEndTime.compareTo(evStartTime) <= 0)
            || (schStart == evEnd && schStartTime.compareTo(evEndTime) >= 0));
  }

  /**
   * Returns if the event spans more
   * than 0 minutes. Returns true if valid event, false if 0 minutes.
   */
  protected static boolean nonZeroMinuteEvent(int startDay, int endDay,
                                              LocalTime startTime, LocalTime endTime) {
    return !startTime.equals(endTime) || startDay != endDay; // Event cannot be 0 minutes
  }

  /**
   * Checks if event is valid to add to the system.
   *
   * @param event    the event being checked
   * @param schedule the list of events being checked against
   * @return true if the timing of the event is valid, false otherwise
   */
  protected static boolean validEvent(Event event, List<Event> schedule) {
    // Get the start and end time info for the proposed event
    int evStart = event.dayInfo().get(0);
    int evEnd = event.dayInfo().get(1);
    LocalTime evStartTime = event.timeInfo().get(0);
    LocalTime evEndTime = event.timeInfo().get(1);
    for (Event scheduled : schedule) {
      int schStart = scheduled.dayInfo().get(0);
      int schEnd = scheduled.dayInfo().get(1);
      LocalTime schStartTime = scheduled.timeInfo().get(0);
      LocalTime schEndTime = scheduled.timeInfo().get(1);

      boolean nextWeek = (schStart > schEnd
              || (schStart == schEnd && schStartTime.isAfter(schEndTime)));
      if (nextWeek) {
        schEnd = 6;
        schEndTime = LocalTime.of(23, 59);
      }

      // Check if the times are valid.
      if (!validTime(evStart, evEnd, evStartTime, evEndTime,
              schStart, schEnd, schStartTime, schEndTime)) {
        return false;
      }
    }
    return true;
  }
}


