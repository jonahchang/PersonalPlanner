package cs3500.model;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Represents the mutable functionality of
 * the central system within the NUPlanner system. Holds
 * all the mutable methods of the NUPlanner system.
 */
public interface ICentralSystem extends IReadOnlyCentralSystem {

  /**
   * Adds a new user to the system.
   *
   * @param userId The user to add.
   * @throws IllegalArgumentException if the user already exists in the system
   */
  void addUser(User userId) throws IllegalArgumentException;

  /**
   * Creates an event attached to a user, and updates all existing user's schedules if
   * the event can be added to their schedule.
   *
   * @param name         name of the event
   * @param location     location of the event
   * @param isOnline     whether the event is online or not
   * @param startTime    start time of the event
   * @param endTime      end time of the event
   * @param startDay     start day of the event
   * @param endDay       end day of the event
   * @param host         host of the event
   * @param invitedUsers invited users of the event
   * @throws IllegalStateException if the user has not yet been selected
   */
  void createEvent(String name, String location, boolean isOnline, LocalTime startTime,
                   LocalTime endTime, cs3500.model.DayOfWeek startDay, DayOfWeek endDay,
                   User host, List<User> invitedUsers) throws IllegalStateException;

  /**
   * Adds the event to the system if it is not there already.
   *
   * @param event the event being added
   * @throws IllegalArgumentException if the event is not a valid event to put on the schedule.
   */
  void addEvent(Event event) throws IllegalArgumentException;

  /**
   * Modifies an existing event. You can modify one or multiple elements of the event without
   * having to change the rest of the event or creating a new one.
   *
   * @param event the event being modified
   */
  void modifyEvent(Event event, Map<String, String> changes);

  /**
   * Removes an event from a User's schedule. Removes the event entirely if the user
   * removing the event is the host. If the user is not on the invitees list, add the user to it.
   *
   * @param event the event being edited
   * @param user  the user whose schedule is being changed
   */
  void updateEventInvited(Event event, User user);

  /**
   * Checks if the proposed event would fit in every user's schedule.
   * @param event the event being proposed
   * @param users the invited users
   * @return true if there is a time block available, false otherwise
   */
  boolean scheduleEvent(Event event, List<String> users);
}

