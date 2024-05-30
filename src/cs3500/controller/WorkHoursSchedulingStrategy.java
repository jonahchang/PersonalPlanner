package cs3500.controller;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cs3500.model.DayOfWeek;
import cs3500.model.Event;
import cs3500.model.ICentralSystem;
import cs3500.model.User;

/**
 * Scheduling strategy based on availability during work hours (0900 - 1700 inclusive).
 */
public class WorkHoursSchedulingStrategy implements SchedulingStrategy {

  private final ICentralSystem model;

  /**
   * Creates the strategy with the model passed in.
   * @param model the associated model
   */
  public WorkHoursSchedulingStrategy(ICentralSystem model) {
    this.model = model;
  }

  @Override
  public Event findTime(int duration, List<String> users, List<String> eventInfo)
          throws IllegalArgumentException {
    Event event;
    if (duration > 480) {
      throw new IllegalArgumentException("Duration is too long");
    }
    int durationHours = (int) Math.ceil(duration / 60.0);
    for (DayOfWeek day = DayOfWeek.MONDAY; day.getValue() <= DayOfWeek.FRIDAY.getValue();
         day = DayOfWeek.dayOf(day.getValue() + 1)) {
      for (int hour = 9; hour <= 17 - durationHours; hour++) {
        for (int minute = 0; minute < 60; minute++) {
          LocalTime startTime = LocalTime.of(hour, minute);

          Map<LocalTime, Integer> end = StrategyHelper.calculateEndTime(hour, minute, duration);
          Map.Entry<LocalTime, Integer> entry = end.entrySet().iterator().next();

          LocalTime endTime = entry.getKey();
          int endDayNum = day.getValue() + entry.getValue();
          DayOfWeek endDay = DayOfWeek.dayOf(endDayNum % 7);

          User host = model.findUserById(model.currentUser());
          List<User> userList = new ArrayList<>();
          for (String userId : users) {
            userList.add(model.findUserById(userId));
          }

          event = new Event(eventInfo.get(0), eventInfo.get(1),
                  Boolean.parseBoolean(eventInfo.get(2)), startTime, endTime,
                  day, endDay, host, userList);
          if (model.scheduleEvent(event, users)) {
            return event;
          }
        }
      }
    }
    return null;
  }
}
