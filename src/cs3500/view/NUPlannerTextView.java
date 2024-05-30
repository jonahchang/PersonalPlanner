package cs3500.view;

import java.util.List;
import java.util.Map;

import cs3500.model.DayOfWeek;
import cs3500.model.ICentralSystem;

/**
 * The view for our NUPlanner schedule. It can display a string representation of every user's
 * schedule, and later on, display as a GUI.
 */
public class NUPlannerTextView implements PlannerView {
  private final ICentralSystem system;

  /**
   * Creates a Planner view that can show a string representation or render a view.
   *
   * @param system the central system being represented
   */
  public NUPlannerTextView(ICentralSystem system) {
    this.system = system;
  }

  @Override
  public String textView() {
    StringBuilder view = new StringBuilder();
    Map<List<String>, List<String>> eventInfo = system.userSchedule(true);
    for (String id : system.findAllUsers()) {
      system.setUser(id);
      // Write the user info
      writeMessage(view, "User: " + id, false);
      for (DayOfWeek day : DayOfWeek.values()) {
        //Write th  e day of the week
        writeMessage(view, day.toString() + ":", false);
        for (List<String> info : eventInfo.keySet()) {
          if (id.equals(info.get(7)) || eventInfo.get(info).contains(id)) {
            // Info is a list of Strings in this order:
            // name, start-day, start-time, end-day, end-time, location, online-status, host ID
            if (info.get(1).equals(day.toString())) {
              // Write the rest of the event info
              writeMessage(view, "name: " + info.get(0), true);
              writeMessage(view, timeMessage(info), true);
              writeMessage(view, "location: " + info.get(5), true);
              writeMessage(view, "online: " + info.get(6), true);
              writeMessage(view, "invitees: " + info.get(7), true); // invitees + host
              inviteesList(view, eventInfo.get(info));
            }
          }
        }
      }
    }
    return view.toString();
  }

  /**
   * Helper method for writing messages to the StringBuilder with a new line at the end.
   *
   * @param view    the StringBuilder being appended to
   * @param message the message to append to the StringBuilder
   * @param indent  whether the lines should be indented or not
   */
  private void writeMessage(StringBuilder view, String message, boolean indent) {
    String indentation = "";
    if (indent) {
      indentation = " ".repeat(8);
    }
    view.append(indentation).append(message).append("\n");
  }

  /**
   * Writes the information regarding time to the StringBuilder.
   *
   * @param info a list containing the time information of the event
   * @return a String with all the time formatted properly for the text view
   */
  private String timeMessage(List<String> info) {
    return "time: " + info.get(1)
            // Remove all the leading zeros in the String
            + ": " + info.get(2).replaceFirst("^0+(?!$)", "")
            + " -> " + info.get(3) + ": "
            // Remove all the leading zeros in the String
            + info.get(4).replaceFirst("^0+(?!$)", "");
  }

  /**
   * Helper method for writing the invitee list to the StringBuilder.
   *
   * @param view     the StringBuilder being appended to
   * @param invitees the list of all invited users, minus the host
   */
  private void inviteesList(StringBuilder view, List<String> invitees) {
    for (String userId : invitees) {
      writeMessage(view, userId, true);
    }
  }
}
