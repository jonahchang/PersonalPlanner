import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs3500.model.DayOfWeek;
import cs3500.model.Event;
import cs3500.model.ICentralSystem;
import cs3500.model.NUPlanner;
import cs3500.model.User;
import cs3500.view.NUPlannerTextView;
import cs3500.view.PlannerView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

/**
 * Test class for testing overall system interactions with the model.
 */
public class CentralSystemTests {

  private User david;
  private User jonah;
  private List<User> pianoClassRoster;
  private Event party;
  private Event pianoLesson;
  private ICentralSystem centralSchedule;
  private PlannerView view;

  @Before
  public void init() {
    List<User> violinClassRoster = new ArrayList<>();
    pianoClassRoster = new ArrayList<>();
    centralSchedule = new NUPlanner();
    view = new NUPlannerTextView(centralSchedule);
    List<User> partyRoster = new ArrayList<>();

    david = new User("david");
    jonah = new User("jonah");
    User teacherLee = new User("teacher lee");
    User teacherBryan = new User("teacher bryan");

    violinClassRoster.add(david);

    pianoClassRoster.add(jonah);

    partyRoster.add(jonah);
    partyRoster.add(david);

    Event violinLesson = new Event("Violin Lesson", "Churchill Hall", false,
            LocalTime.of(19, 0), LocalTime.of(21, 0),
            DayOfWeek.WEDNESDAY, DayOfWeek.WEDNESDAY, teacherLee, violinClassRoster);
    pianoLesson = new Event("Piano Lesson", "Snell Library", true,
            LocalTime.of(19, 30), LocalTime.of(21, 0),
            DayOfWeek.WEDNESDAY, DayOfWeek.WEDNESDAY, teacherBryan, pianoClassRoster);
    party = new Event("party", "my house", false,
            LocalTime.of(21, 0), LocalTime.of(1, 0),
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, jonah, partyRoster);

    jonah.addEvent(pianoLesson);
    jonah.addEvent(party);
    david.addEvent(violinLesson);
    david.addEvent(party);
  }

  /**
   * Tests adding users to the system.
   */
  @Test
  public void addUser() {
    centralSchedule.addUser(david);
    assertThrows(IllegalArgumentException.class, () -> centralSchedule.addUser(david));
    centralSchedule.addUser(jonah);
  }

  @Test
  public void setAddUser() {
    // User does not exist
    assertThrows(IllegalArgumentException.class, () -> centralSchedule.setUser("jonah"));
    assertNull(centralSchedule.findUserById("jonah"));
    // Valid setUser
    centralSchedule.addUser(jonah);
    centralSchedule.setUser(jonah.userId());
    assertEquals(centralSchedule.findUserById("jonah"), new User("jonah"));
    // Invalid addUser
    assertThrows(IllegalArgumentException.class, () -> centralSchedule.addUser(jonah));
  }


  @Test
  public void eventsAddedWithUsers() {
    assertNull(centralSchedule.findEventByName("Violin Lesson"));

    centralSchedule.addUser(jonah);
    centralSchedule.setUser("jonah");
    assertThrows(IllegalArgumentException.class, () -> centralSchedule.addEvent(pianoLesson));
    assertEquals(centralSchedule.findEventByName("Piano Lesson"), pianoLesson);
    // The party event has not been added yet
    assertEquals(centralSchedule.findEventByName("party"), party);
  }

  @Test
  public void addExistingEvents() {
    // Add event on its own
    centralSchedule.addEvent(pianoLesson);
    assertThrows(IllegalArgumentException.class, () -> centralSchedule.addEvent(pianoLesson));
    // Add user first, then add events
    centralSchedule.addUser(jonah);
    assertThrows(IllegalArgumentException.class, () -> jonah.addEvent(pianoLesson));
  }

  @Test
  public void modifyEvent() {
    centralSchedule.addUser(jonah);
    Map<String, String> changes = new HashMap<>();
    changes.put("name", "new name");
    changes.put("location", "curry center");
    changes.put("online", "tRuE");
    changes.put("start-time", "0030");
    changes.put("end-time", "1930");
    changes.put("start", "wednesday");
    changes.put("end", "wednesday");
    changes.put("invited", "");

    centralSchedule.modifyEvent(pianoLesson, changes);

    // Invalid online status change
    changes.put("online", "12ab");
    assertThrows(IllegalArgumentException.class,
        () -> centralSchedule.modifyEvent(pianoLesson, changes));

    changes.put("online", "true");
    changes.put("start-time", "hello");
    // Invalid time format
    assertThrows(IllegalArgumentException.class,
        () -> centralSchedule.modifyEvent(pianoLesson, changes));
    changes.put("start-time", "0030");
    changes.put("end-time", "03121");
    assertThrows(IllegalArgumentException.class,
        () -> centralSchedule.modifyEvent(pianoLesson, changes));
    // Valid start time
    changes.put("end-time", "1930");
    // Check the modified information
    List<String> expected = List.of("new name", "Wednesday", "0030", "Wednesday", "1930",
            "curry center", "true", "teacher bryan");
    assertEquals(centralSchedule.findEventByName("new name").eventInfo(), expected);
  }

  @Test
  public void allUsers() {
    List<String> expected = List.of("jonah", "david");

    centralSchedule.addUser(jonah);
    centralSchedule.addUser(david);
    assertEquals(expected, centralSchedule.findAllUsers());
  }

  @Test
  public void createEvent() {
    // When a user has not been selected yet
    assertThrows(IllegalStateException.class, () -> centralSchedule.createEvent(
            "hi", "hi", true,
            LocalTime.of(1, 30), LocalTime.of(2, 20),
            DayOfWeek.MONDAY, DayOfWeek.MONDAY, jonah, pianoClassRoster));

    centralSchedule.addUser(jonah);
    centralSchedule.setUser("jonah");
    centralSchedule.createEvent(
            "hi", "hi", true,
            LocalTime.of(1, 30), LocalTime.of(2, 20),
            DayOfWeek.MONDAY, DayOfWeek.MONDAY, jonah, pianoClassRoster);
    Event mockEvent = new Event("hi", "hi", false,
            LocalTime.of(1, 30), LocalTime.of(2, 20),
            DayOfWeek.MONDAY, DayOfWeek.MONDAY, jonah, pianoClassRoster);
    assertEquals(mockEvent, centralSchedule.findEventByName("hi"));
  }

  @Test
  public void removeEvent() {
    assertThrows(IllegalArgumentException.class,
        () -> centralSchedule.updateEventInvited(party, jonah));
    centralSchedule.addUser(jonah);
    centralSchedule.addUser(david);
    centralSchedule.updateEventInvited(party, david);
    assertEquals(party.invitees(), List.of());
  }

  @Test
  public void removeHost() {
    centralSchedule.addUser(jonah);
    centralSchedule.updateEventInvited(party, jonah);
    assertNull(centralSchedule.findEventByName("party"));
    assertThrows(IllegalArgumentException.class, () -> jonah.findEventByName("party"));
  }

  @Test
  public void findEventByTime() {
    centralSchedule.addUser(jonah);
    centralSchedule.setUser("jonah");

    List<Event> pianoEvent = new ArrayList<>();
    pianoEvent.add(pianoLesson);
    assertEquals(pianoEvent, centralSchedule.findEventsByTime("Wednesday", "2059"));

    // No event at time
    assertNull(centralSchedule.findEventsByTime("Monday", "1111"));
  }

  /**
   * TESTING FOR VIEW COMPONENT.
   */
  @Test
  public void viewTesting() {
    centralSchedule.addUser(jonah);
    assertEquals("User: jonah\n"
                    + "Sunday:\n"
                    + "Monday:\n"
                    + "Tuesday:\n"
                    + "Wednesday:\n"
                    + "        name: Piano Lesson\n"
                    + "        time: Wednesday: 1930 -> Wednesday: 2100\n"
                    + "        location: Snell Library\n"
                    + "        online: true\n"
                    + "        invitees: teacher bryan\n"
                    + "        jonah\n"
                    + "        name: party\n"
                    + "        time: Wednesday: 2100 -> Thursday: 100\n"
                    + "        location: my house\n"
                    + "        online: false\n"
                    + "        invitees: jonah\n"
                    + "        david\n"
                    + "Thursday:\n"
                    + "Friday:\n"
                    + "Saturday:\n",
            view.textView());
  }

  @Test
  public void twoUsers() {
    centralSchedule.addUser(jonah);
    centralSchedule.addUser(david);
    assertEquals("User: jonah\nSunday:\nMonday:\nTuesday:\nWednesday:\n"
            + "        name: Piano Lesson\n"
            + "        time: Wednesday: 1930 -> Wednesday: 2100\n"
            + "        location: Snell Library\n"
            + "        online: true\n"
            + "        invitees: teacher bryan\n"
            + "        jonah\n"
            + "        name: party\n"
            + "        time: Wednesday: 2100 -> Thursday: 100\n"
            + "        location: my house\n"
            + "        online: false\n"
            + "        invitees: jonah\n"
            + "        david\n"
            + "Thursday:\nFriday:\nSaturday:\n"
            + "User: david\nSunday:\nMonday:\nTuesday:\nWednesday:\n"
            + "        name: Violin Lesson\n"
            + "        time: Wednesday: 1900 -> Wednesday: 2100\n"
            + "        location: Churchill Hall\n"
            + "        online: false\n"
            + "        invitees: teacher lee\n"
            + "        david\n"
            + "        name: party\n"
            + "        time: Wednesday: 2100 -> Thursday: 100\n"
            + "        location: my house\n"
            + "        online: false\n"
            + "        invitees: jonah\n"
            + "        david\n"
            + "Thursday:\nFriday:\nSaturday:\n", view.textView());
  }
}
