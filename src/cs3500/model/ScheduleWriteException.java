package cs3500.model;

import java.io.IOException;

/**
 * Custom exception to create more detailed messages when encountering errors with writing XML.
 */
public class ScheduleWriteException extends IOException {
  /**
   * Creates a ScheduleWriteException with a message passed to give more clarity.
   *
   * @param message the description of the error
   */
  public ScheduleWriteException(String message) {
    super(message);
  }

  /**
   * Creates a ScheduleWriteException through passing a message and a cause/error message.
   *
   * @param message the description of the error
   * @param cause   the original cause/error message
   */
  public ScheduleWriteException(String message, Throwable cause) {
    super(message, cause);
  }
}
