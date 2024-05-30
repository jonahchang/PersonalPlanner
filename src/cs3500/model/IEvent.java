package cs3500.model;

import java.time.LocalTime;
import java.util.List;

/**
 * Represents an event within the NUPlanner system. An event encapsulates its
 * name, start and end times, location, and participants.
 */
public interface IEvent {

  /**
   * Creates and returns a list of all the event information as strings.
   *
   * @return a list of strings detailing the event information
   */
  List<String> eventInfo();

  /**
   * Returns the host of the event as a User object for the central system to keep track of.
   *
   * @return the host
   */
  User hostInfo();

  /**
   * Creates and returns a list of the start and end days of the event.
   *
   * @return a list of the start and end days
   */
  List<Integer> dayInfo();

  /**
   * Creates and returns a list of the fields relating to time for an event.
   *
   * @return a list of the start and end time information
   */
  List<LocalTime> timeInfo();

  /**
   * Creates a copy of the invited users list for the system to use.
   *
   * @return a list of users that is a copy of the invitedUsers field.
   */
  List<User> invitees();

  /**
   * Checks if the given time and day is in between the start
   * and end times of this IEvent (non-inclusive).
   *
   * @param time the given time
   * @param day  the given day
   * @return true if time is in between the start and end times of this IEvent, false otherwise
   */
  boolean checkTime(LocalTime time, DayOfWeek day);

  /**
   * Removes a user from the invited list if user is already on the list. Otherwise,
   * add the user to the list.
   *
   * @param user the user to be added or removed
   */
  void changeInvited(User user);

  /**
   * Updates the name of the event.
   *
   * @param newName the new name of the event
   * @throws IllegalArgumentException if the new name is invalid
   */
  void updateName(String newName) throws IllegalArgumentException;

  /**
   * Updates the location of the event.
   *
   * @param newLocation the new location
   * @throws IllegalArgumentException if the new location is invalid
   */
  void updateLocation(String newLocation) throws IllegalArgumentException;

  /**
   * Updates the online status of the event.
   *
   * @param isOnline the new status
   */
  void updateIsOnline(boolean isOnline);

  /**
   * Updates the start and end information of the event.
   *
   * @param startTime the new starting time
   * @param endTime the new ending time
   * @param startDay the new starting day
   * @param endDay the new ending day
   */
  void updateStartEnd(LocalTime startTime, LocalTime endTime, DayOfWeek startDay, DayOfWeek endDay);
}

