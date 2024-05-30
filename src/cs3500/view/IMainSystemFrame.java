package cs3500.view;

import cs3500.controller.Features;

/**
 * Interface for the main system's frame that defines the overall structure and capabilities of the
 * main application frame.
 */
public interface IMainSystemFrame {

  /**
   * Updates the user dropdown list.
   */
  void updateUsers();

  /**
   * Displays the main system frame, making it visible to the user.
   */
  void displayMainFrame();

  /**
   * Sets the panel within the main frame.
   *
   * @param panel The panel to be displayed in the main frame.
   */
  void setPanel(IMainPanel panel);

  /**
   * Adds the features/action listener to the view for callbacks.
   * @param features the associated features listener
   */
  void addFeatures(Features features);

  /**
   * Displays an error message.
   * @param message the message to be displayed
   */
  void errorMessage(String message);
}
