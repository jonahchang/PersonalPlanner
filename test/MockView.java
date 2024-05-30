import cs3500.controller.Features;
import cs3500.model.IReadOnlyCentralSystem;
import cs3500.view.IMainPanel;
import cs3500.view.IMainSystemFrame;

/**
 * Mock view class for testing.
 */
public class MockView implements IMainSystemFrame {

  @Override
  public void updateUsers() {
    // Filler
  }

  @Override
  public void displayMainFrame() {
    // Filler
  }

  @Override
  public void setPanel(IMainPanel panel) {
    // Filler
  }

  @Override
  public void addFeatures(Features features) {
    // Filler
  }

  @Override
  public void errorMessage(String message) {
    // Filler
  }
}
