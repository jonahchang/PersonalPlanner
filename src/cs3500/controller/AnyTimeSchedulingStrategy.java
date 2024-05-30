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
 * Searches for the first available time slot for all invited users starting at Sunday 00:00.
 */
public class AnyTimeSchedulingStrategy implements SchedulingStrategy {

  private final ICentralSystem model;

  /**
   * Creates the any time strategy with the model passed in.
   * @param model the associated model
   */
  public AnyTimeSchedulingStrategy(ICentralSystem model) {
    this.model = model;
  }

  @Override
  public Event findTime(int duration, List<String> users, List<String> eventInfo)
          throws IllegalArgumentException {
    Event event;
    for (DayOfWeek day : DayOfWeek.values()) {
      for (int hour = 0; hour < 24; hour++) {
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
    throw new IllegalArgumentException("Could not find an open block of time");
  }
}
