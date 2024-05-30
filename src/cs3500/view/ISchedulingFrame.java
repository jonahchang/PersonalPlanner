package cs3500.view;

import cs3500.controller.Features;

/**
 * Panel interface for displaying and creating a GUI for the event panel.
 */
public interface ISchedulingFrame {

  /**
   * Uses the provided Features class to designate callbacks.
   * @param feature the feature being used for callbacks
   */
  void addFeatures(Features feature);

  /**
   * Displays an error message.
   * @param message the message to be displayed
   */
  void errorMessage(String message);
}
