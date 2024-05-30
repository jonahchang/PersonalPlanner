package cs3500.controller;

import cs3500.model.ICentralSystem;
import cs3500.model.ScheduleWriteException;

/**
 * A controller used solely to test the model's capability to save and upload schedules.
 */
public class TextUI implements IPlannerController {

  private ICentralSystem model;

  /**
   * Creates a text controller that is used solely for testing.
   */
  public TextUI(ICentralSystem model) {
    this.model = model;
  }

  @Override
  public void saveSchedule(String filePath, String userId)
          throws ScheduleWriteException {
    FileOperations.saveSchedule(model, userId, filePath);
  }

  @Override
  public void uploadSchedule(String filePath) throws IllegalStateException {
    FileOperations.uploadSchedule(model, filePath);
  }
}
