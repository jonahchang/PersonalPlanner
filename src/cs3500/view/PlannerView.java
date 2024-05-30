package cs3500.view;

/**
 * An interface defining how a schedule should be viewed.
 */
public interface PlannerView {

  /**
   * Creates a text view that is printed to the system for viewing.
   *
   * @return a string representation of every user's schedule
   */
  String textView();
}
