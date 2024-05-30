package cs3500;

import cs3500.controller.GUIController;
import cs3500.model.ICentralSystem;
import cs3500.model.NUPlanner;
import cs3500.view.MainSystemFrame;

/**
 * Main class for running the program for the planner GUI.
 */
public final class PlannerRunner {

  /**
   * Main method for running the program.
   *
   * @param args the arguments provided
   */
  public static void main(String[] args) {
    ICentralSystem system = new NUPlanner();
    MainSystemFrame view = new MainSystemFrame(system);

    Strategy.StrategyType strategyType;
    if (args.length < 1) {
      throw new IllegalArgumentException("No arguments passed");
    }

    switch (args[0].toLowerCase()) {
      case "anytime":
        strategyType = Strategy.StrategyType.ANYTIME;
        break;
      case "workhours":
        strategyType = Strategy.StrategyType.WORKHOURS;
        break;
      default:
        throw new IllegalArgumentException("Invalid strategy type: " + args[0]);
    }

    GUIController controller = new GUIController(system, view);
    Strategy.setStrategy(controller, strategyType, system);
    controller.setView(view);
    controller.makeVisible();
  }
}
