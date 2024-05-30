package cs3500.model;

import java.util.List;
import java.util.Map;

/**
 * Read-only version of our
 * ICentralSystem interface.
 * Implements all observation methods of the
 * NUPlanner system.
 */
public interface IReadOnlyCentralSystem {

  /**
   * Returns the current user as their String ID.
   *
   * @return the current user's ID
   */
  String currentUser();

  /**
   * Sets who the current user is.
   *
   * @param userId the user's ID
   */
  void setUser(String userId);

  /**
   * Finds a user within the central system database.
   *
   * @param userId The user to find in the system.
   * @return The found user, or null if the user was not found in the database
   */
  User findUserById(String userId);

  /**
   * Finds the event in the system using the name, assumes all events have unique names.
   *
   * @param eventName the name of the event to be found
   * @return the found Event, or null if the event is not found
   */
  Event findEventByName(String eventName);

  /**
   * Finds an event in the system for the current user.
   *
   * @param day  the day being checked
   * @param time the time being checked
   * @return the event happening at the time, null if no event is happening at the given time
   */
  List<Event> findEventsByTime(String day, String time);

  /**
   * Returns a list of all events that a user is involved in.
   *
   * @param allUsers whether we should find all users' schedules or just the current user's
   * @return a map of events on the user's schedule with the event information mapped to the
   *         list of invited users
   */
  Map<List<String>, List<String>> userSchedule(boolean allUsers);

  /**
   * Finds all users in the system.
   *
   * @return a list of all users in the system as their String ID
   */
  List<String> findAllUsers();
}
