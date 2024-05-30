package cs3500.controller;

import java.io.File;
import java.util.List;

import cs3500.model.ScheduleWriteException;
import cs3500.view.IMainSystemFrame;

/**
 * Interface specifying the features/behaviors that are possible and expected of a controller
 * for the GUI-based view.
 */
public interface Features {

  /**
   * Makes the main system frame visible to interact with.
   */
  void makeVisible();

  void setView(IMainSystemFrame view);

  /**
   * Sets the strategy for scheduling an event.
   */
  void setStrategy(SchedulingStrategy strategy);

  /**
   * Creates an event using the input from the view and adds it to the model.
   */
  void createEvent(List<String> eventInfo, List<String> invitedUsers);

  /**
   * Modifies an existing event in the system using input from the view.
   * @param eventInfo the information of the modified event.
   * @param selectedUsers the selected users to be added or removed.
   * @param eventName the name of the event to modify.
   */
  void modifyEvent(List<String> eventInfo, List<String> selectedUsers, String eventName);

  /**
   * Removes an existing event from the system.
   * @param eventName the name of the event.
   */
  void removeEvent(String eventName);

  /**
   * Schedules an event for all invited users at the preferred time or any time.
   * @param duration the duration of the event.
   * @param eventInfo the event information
   * @param users the list of invited users
   */
  void scheduleEvent(int duration, List<String> eventInfo, List<String> users);

  /**
   * Sets the user whose schedule is being displayed.
   * @param userId the user ID of the user being set.
   */
  void setUser(String userId);

  /**
   * Creates the event frame for the client to create an event.
   */
  void createEventFrame();

  /**
   * Creates the event frame for the client to schedule an event.
   */
  void createSchedulingFrame();

  /**
   * Writes a given schedule to an XML file at the specified file path.
   * This method overwrites any existing content, use FileWriter to append to an existing file.
   *
   * @param selectedDir the selected directory for saving files to
   * @throws ScheduleWriteException if the program was unable to write an XML file for the user
   */
  void saveSchedule(File selectedDir) throws ScheduleWriteException;

  /**
   * Reads a schedule from an XML file located at the specified file path and constructs a
   * User object from it if the user is not already in the system. Each Event's name, time,
   * location, and invited users are extracted and used to create or add to a User's Schedule.
   *
   * @param filePath the file path of the XML file to be read
   * @throws IllegalStateException if there is an error in reading or parsing the file
   */
  void uploadSchedule(String filePath) throws IllegalStateException;
}
