package cs3500.view;

import java.awt.Graphics2D;

import cs3500.controller.Features;

/**
 * The behaviors of the main system's schedule panel. This panel provides the UI components such as
 * buttons, dropdown menus, etc. for the user to interact with.
 */
public interface IMainPanel {

  /**
   * Adds the feature listener to the panel.
   * @param features the associated feature listener
   */
  void addFeatures(Features features);

  /**
   * Displays the schedule of the current User.
   *
   * @param userId the ID of the user to be displayed
   * @param g2d    the graphics tool for drawing the events
   */
  void displaySchedule(String userId, Graphics2D g2d);

  /**
   * Updates the user dropdown list.
   */
  void updateUsers();
}
