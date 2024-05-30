package cs3500.controller;

import java.util.List;

import cs3500.model.Event;

/**
 * Strategy interface for scheduling events.
 */
public interface SchedulingStrategy {

  /**
   * Schedules an event based on certain criteria such as preferred time.
   * @param duration the duration of the scheduled event
   * @param users the invited users
   * @param eventInfo the event information
   * @throws IllegalArgumentException if no time slot can be found for the invited users
   */
  Event findTime(int duration, List<String> users, List<String> eventInfo)
          throws IllegalArgumentException;
}
