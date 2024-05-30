package cs3500.view;

import java.time.LocalTime;

import cs3500.controller.Features;

/**
 * Interface for the Event frame. It provides a frame for the user to specify the details for
 * creating, modifying, and removing an event.
 */
public interface IEventFrame {

  /**
   * Sets all the fields to pre-existing values if the user has clicked on an event to modify
   * or remove.
   */
  void setFields(String name, String location, boolean online, int startDay, int endDay,
                 LocalTime startTime, LocalTime endTime);

  /**
   * Uses the provided Features class to designate callbacks.
   * @param feature the feature being used for callbacks
   */
  void addFeatures(Features feature);

  /**
   * Sets the associated event if the client clicked on a red area.
   * @param eventName the associated event name
   */
  void setEvent(String eventName);

  /**
   * Displays an error message.
   * @param message the message to be displayed
   */
  void errorMessage(String message);
}
