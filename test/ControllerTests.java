import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;

import cs3500.controller.IPlannerController;
import cs3500.controller.TextUI;
import cs3500.model.DayOfWeek;
import cs3500.model.Event;
import cs3500.model.ICentralSystem;
import cs3500.model.NUPlanner;
import cs3500.model.ScheduleWriteException;
import cs3500.model.User;
import cs3500.view.NUPlannerTextView;
import cs3500.view.PlannerView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

/**
 * Test class for testing controller functionality including reading and writing to XML files.
 * Note: we haven't fully implemented the controller functionality yet, but this class tests
 * the functionality we did implement.
 */
public class ControllerTests {

  private User jonah;
  private ICentralSystem system;
  private IPlannerController controller;
  private PlannerView view;


  @Before
  public void init() {
    system = new NUPlanner();
    controller = new TextUI(system);
    view = new NUPlannerTextView(system);
    jonah = new User("jonah");
    Event event = new Event("name", "location", true,
            LocalTime.of(0, 1), LocalTime.of(2, 1),
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, jonah, new ArrayList<>());
    jonah.addEvent(event);
  }

  @Test
  public void readXMLFile() {
    controller.uploadSchedule("prof.xml");
    assertEquals("User: Prof. Lucia\nSunday:\nMonday:\nTuesday:\n"
            + "        name: CS3500 Morning Lecture\n        time: Tuesday: 950 -> Tuesday: 1130\n"
            + "        location: Churchill Hall 101\n        online: false\n"
            + "        invitees: Prof. Lucia\n        Student Anon\n        Chat\n"
            + "        name: CS3500 Afternoon Lecture\n"
            + "        time: Tuesday: 1335 -> Tuesday: 1515\n        location: Churchill Hall 101\n"
            + "        online: false\n"
            + "        invitees: Prof. Lucia\n        Chat\n"
            + "Wednesday:\nThursday:\nFriday:\n"
            + "        name: Sleep\n        time: Friday: 1800 -> Sunday: 1200\n"
            + "        location: Home\n        online: true\n        invitees: Prof. Lucia\n"
            + "Saturday:\n"
            + "User: Student Anon\nSunday:\nMonday:\nTuesday:\n"
            + "        name: CS3500 Morning Lecture\n"
            + "        time: Tuesday: 950 -> Tuesday: 1130\n"
            + "        location: Churchill Hall 101\n"
            + "        online: false\n"
            + "        invitees: Prof. Lucia\n"
            + "        Student Anon\n        Chat\n"
            + "Wednesday:\nThursday:\nFriday:\nSaturday:\n"
            + "User: Chat\nSunday:\nMonday:\nTuesday:\n"
            + "        name: CS3500 Morning Lecture\n"
            + "        time: Tuesday: 950 -> Tuesday: 1130\n"
            + "        location: Churchill Hall 101\n"
            + "        online: false\n"
            + "        invitees: Prof. Lucia\n"
            + "        Student Anon\n"
            + "        Chat\n"
            + "        name: CS3500 Afternoon Lecture\n"
            + "        time: Tuesday: 1335 -> Tuesday: 1515\n"
            + "        location: Churchill Hall 101\n"
            + "        online: false\n"
            + "        invitees: Prof. Lucia\n        Chat\n"
            + "Wednesday:\nThursday:\nFriday:\nSaturday:\n", view.textView());
  }

  @Test
  public void writeXML() throws ScheduleWriteException {
    system.addUser(jonah);
    controller.saveSchedule("jonah.xml", "jonah");
    assertThrows(IllegalStateException.class,
        () -> controller.uploadSchedule("jonah.xml"));
    assertNotNull(system.findUserById(jonah.userId()));
  }
}
