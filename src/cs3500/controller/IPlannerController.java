package cs3500.controller;

import cs3500.model.ScheduleWriteException;

/**
 * A controller for the NUPlanner system for taking in user input to interact with the view
 * and model.
 */
public interface IPlannerController {

  /**
   * Writes a given schedule to an XML file at the specified file path.
   * This method overwrites any existing content, use FileWriter to append to an existing file.
   *
   * @param filePath the file path where the XML file will be written. If the file already
   *                 exists, it will be overwritten
   * @param userId the user whose schedule is being saved
   * @throws ScheduleWriteException if the program was unable to write an XML file for the user
   */
  void saveSchedule(String filePath, String userId)
          throws ScheduleWriteException;

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
