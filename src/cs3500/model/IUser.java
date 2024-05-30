package cs3500.model;

import java.time.LocalTime;
import java.util.List;

/**
 * Represents a user within the NUPlanner system. A user is identified by an ID and has
 * a schedule of events that they are hosting or have been invited to.
 */
public interface IUser {

  /**
   * Returns the user's ID.
   *
   * @return the user's ID
   */
  String userId();

  /**
   * Adds an event to the user's schedule.
   *
   * @param event The event to add.
   * @throws IllegalArgumentException if the event overlaps with another event
   * @throws NullPointerException     if the event is null
   */
  void addEvent(Event event) throws IllegalArgumentException;

  /**
   * Removes an event from the user's schedule.
   *
   * @param event The event to remove.
   * @throws IllegalArgumentException if the event is not in the user's schedule or is null
   */
  void removeEvent(Event event) throws IllegalArgumentException;

  /**
   * Finds an event by its name within the user's schedule.
   *
   * @param eventName The name of the event to find.
   * @return The event if found, or null if no such event exists in the schedule.
   * @throws IllegalArgumentException if the event is not in the user's schedule
   */
  IEvent findEventByName(String eventName) throws IllegalArgumentException;

  /**
   * Returns a copy of the user's schedule to check the events they are involved in.
   *
   * @return A list of events that the user will be attending
   */
  List<Event> userSchedule();

  /**
   * Checks for any events happening at a given time.
   *
   * @param time the time being checked
   * @return a list of Events happening at the given time, null if no event is happening
   */
  List<Event> checkTime(DayOfWeek day, LocalTime time);
}

