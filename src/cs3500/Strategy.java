package cs3500;

import cs3500.controller.AnyTimeSchedulingStrategy;
import cs3500.controller.Features;
import cs3500.controller.WorkHoursSchedulingStrategy;
import cs3500.model.ICentralSystem;

/**
 * Factory class for creating a Planner controller with a certain strategy for scheduling events.
 */
public class Strategy {

  /**
   * Enum class for defining strategy types.
   */
  public enum StrategyType {
    ANYTIME,
    WORKHOURS
  }

  /**
   * Sets the strategy for the controller.
   * @param controller the controller used
   * @param type the type of strategy
   * @param model the model associated with it
   */
  public static void setStrategy(Features controller, StrategyType type, ICentralSystem model) {
    switch (type) {
      case ANYTIME:
        controller.setStrategy(new AnyTimeSchedulingStrategy(model));
        break;
      case WORKHOURS:
        controller.setStrategy(new WorkHoursSchedulingStrategy(model));
        break;
      default:
        throw new IllegalArgumentException("Unknown strategy type: " + type);
    }
  }
}
