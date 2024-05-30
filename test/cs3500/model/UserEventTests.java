package cs3500.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Test class for testing the functionalities within the User and Event classes.
 */
public class UserEventTests {

  private User jonah;
  private User david;
  private Event wakeUp;
  private Event earlyWakeUp;
  private Event notInvited;
  private List<User> invited;

  @Before
  public void init() {
    LocalTime start = LocalTime.of(9, 0);
    LocalTime end = LocalTime.of(9, 30);
    LocalTime earlyStart = LocalTime.of(8, 30);

    jonah = new User("jonah");
    david = new User("david");

    wakeUp = new Event("wake up", "bed", false, start, end,
            DayOfWeek.TUESDAY, DayOfWeek.TUESDAY, jonah, new ArrayList<>());
    earlyWakeUp = new Event("early wake up", "bed", false, earlyStart, start,
            DayOfWeek.TUESDAY, DayOfWeek.TUESDAY, jonah, new ArrayList<>());
    notInvited = new Event("not invited", "no", false, start, end,
            DayOfWeek.WEDNESDAY, DayOfWeek.WEDNESDAY, david, new ArrayList<>());
    invited = new ArrayList<>();
    invited.add(david);
  }


  /**
   * User Tests Here.
   */
  @Test
  public void testUserConstructor() {
    assertThrows(IllegalArgumentException.class, () -> new User(null));
    assertThrows(IllegalArgumentException.class, () -> new User(""));
    assertEquals(jonah.userId(), "jonah");
    assertNotEquals(jonah.userId(), "james");
  }

  @Test
  public void addEvent() {
    // Add events the user is part of
    jonah.addEvent(wakeUp);
    assertEquals(wakeUp, jonah.findEventByName("wake up"));
    jonah.addEvent(earlyWakeUp);
    assertEquals(earlyWakeUp, jonah.findEventByName("early wake up"));
  }

  @Test
  public void addInvalidEvent() {
    // Add an event the user is not part of
    assertThrows(IllegalArgumentException.class, () -> jonah.addEvent(notInvited));

    assertThrows(NullPointerException.class, () -> jonah.addEvent(null));
    jonah.addEvent(wakeUp);
    assertThrows(IllegalArgumentException.class, () -> jonah.addEvent(wakeUp));
    // 0-minute event
    Event zeroMinute = new Event("zero", "0", false,
            LocalTime.of(1, 1), LocalTime.of(1, 1),
            DayOfWeek.FRIDAY, DayOfWeek.FRIDAY, jonah, invited);
    assertThrows(IllegalArgumentException.class, () -> jonah.addEvent(zeroMinute));
  }

  @Test
  public void overLappingEvents() {
    // Happens between the scheduled event
    Event between = new Event("between", "between", false,
            LocalTime.of(9, 10), LocalTime.of(9, 20),
            DayOfWeek.TUESDAY, DayOfWeek.TUESDAY, jonah, invited);
    // Happens before scheduled event and cuts into it
    Event before = new Event("before", "before", false,
            LocalTime.of(6, 10), LocalTime.of(9, 15),
            DayOfWeek.TUESDAY, DayOfWeek.TUESDAY, jonah, invited);
    // Starts during event and ends afterwards
    Event after = new Event("after", "after", false,
            LocalTime.of(9, 29), LocalTime.of(9, 30),
            DayOfWeek.TUESDAY, DayOfWeek.TUESDAY, jonah, invited);
    jonah.addEvent(wakeUp);
    assertThrows(IllegalArgumentException.class, () -> jonah.addEvent(between));
    assertThrows(IllegalArgumentException.class, () -> jonah.addEvent(before));
    assertThrows(IllegalArgumentException.class, () -> jonah.addEvent(after));
  }

  @Test
  public void nextWeekEvents() {
    Event nextWeek = new Event("next week", "next week", false,
            LocalTime.of(9, 10), LocalTime.of(9, 20),
            DayOfWeek.dayOf(5), DayOfWeek.dayOf(3), jonah, invited);
    Event sameWeek = new Event("same week", "same week", false,
            LocalTime.of(9, 0), LocalTime.of(9, 30),
            DayOfWeek.dayOf(2), DayOfWeek.dayOf(2), jonah, invited);
    jonah.addEvent(wakeUp);
    jonah.addEvent(nextWeek);
    assertThrows(IllegalArgumentException.class, () -> jonah.addEvent(sameWeek));
  }

  @Test
  public void removeEvent() {
    // No events in the schedule yet
    assertThrows(IllegalArgumentException.class, () -> jonah.removeEvent(wakeUp));
    // Add events
    jonah.addEvent(wakeUp);
    jonah.removeEvent(wakeUp);
    assertThrows(IllegalArgumentException.class, () -> jonah.removeEvent(wakeUp));

    assertThrows(NullPointerException.class, () -> jonah.addEvent(null));
  }

  @Test
  public void findEvents() {
    jonah.addEvent(wakeUp);
    assertEquals(wakeUp, jonah.findEventByName("wake up"));
    assertThrows(IllegalArgumentException.class, () -> jonah.findEventByName("wakeup"));

    // Find event after removing it
    jonah.removeEvent(wakeUp);
    assertThrows(IllegalArgumentException.class, () -> jonah.findEventByName("wake up"));
  }

  @Test
  public void userSchedule() {
    List<Event> wake = new ArrayList<>();
    wake.add(wakeUp);
    jonah.addEvent(wakeUp);
    assertEquals(wake, jonah.userSchedule());

    // After removing Event
    wake = new ArrayList<>();
    jonah.removeEvent(wakeUp);
    assertEquals(wake, jonah.userSchedule());
  }

  @Test
  public void checkTime() {
    assertNull(jonah.checkTime(DayOfWeek.TUESDAY, LocalTime.of(9, 0)));

    List<Event> events = new ArrayList<>();

    jonah.addEvent(wakeUp);
    events.add(wakeUp);
    assertEquals(events, jonah.checkTime(DayOfWeek.TUESDAY, LocalTime.of(9, 1)));
    assertNotEquals(events, jonah.checkTime(DayOfWeek.FRIDAY, LocalTime.of(9, 0)));
  }

  @Test
  public void testEquals() {
    // Our central system ensures user ID's are unique, but creating lone instances allow us to
    // test the equals override
    assertNotEquals(jonah, david);
    User user = new User("jonah");
    assertEquals(jonah, user);
    assertEquals(jonah, jonah);

    // Our central system ensures names of events are unique, but creating a lone instance that is
    // not part of the system allows for duplicate names
    Event copyEvent = new Event("wake up", "nowhere", true,
            LocalTime.of(0, 0), LocalTime.of(23, 59),
            DayOfWeek.MONDAY, DayOfWeek.THURSDAY, david, invited);
    assertEquals(copyEvent, wakeUp);
  }

  /**
   * Event Tests Here.
   */
  @Test
  public void testEventConstructor() {
    assertThrows(NullPointerException.class, () -> new Event(null, null,
            false, null, null,
            null, null, null, null));
    assertThrows(IllegalArgumentException.class, () -> new Event("", "",
            false, LocalTime.of(9, 0), LocalTime.of(9, 30),
            DayOfWeek.dayOf(2), DayOfWeek.dayOf(2), jonah, invited));

    // Initialized properly with all the required info
    List<String> info = List.of("wake up", "Tuesday", "0900", "Tuesday", "0930", "bed",
            "false", "jonah");
    assertEquals(info, wakeUp.eventInfo());
  }

  @Test
  public void noParameterMethods() {
    List<Integer> days = List.of(3, 3);
    List<LocalTime> times = List.of(LocalTime.of(9, 0), LocalTime.of(9, 30));

    assertEquals(wakeUp.dayInfo(), days);
    assertEquals(wakeUp.timeInfo(), times);

    assertEquals(wakeUp.invitees(), List.of());
  }

  @Test
  public void checkEventTime() {
    // Check the beginning, middle, and end of the event
    assertTrue(wakeUp.checkTime(LocalTime.of(9, 1), DayOfWeek.TUESDAY));
    assertTrue(wakeUp.checkTime(LocalTime.of(9, 15), DayOfWeek.TUESDAY));
    assertTrue(wakeUp.checkTime(LocalTime.of(9, 29), DayOfWeek.TUESDAY));

    // Check invalid time
    assertFalse(wakeUp.checkTime(LocalTime.of(6, 0), DayOfWeek.FRIDAY));
  }

  @Test
  public void changeEventInfo() {
    // List of updated event info
    List<String> eventInfo = List.of("sleep", "Friday", "1550", "Saturday", "2359",
            "floor", "true", "jonah");
    // Update every possible piece of event info
    wakeUp.updateName("sleep");
    wakeUp.updateStartEnd(LocalTime.of(15, 50), LocalTime.of(23, 59),
            DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);
    wakeUp.updateLocation("floor");
    wakeUp.updateIsOnline(true);

    assertEquals(eventInfo, wakeUp.eventInfo());
  }

  @Test
  public void changeInvitees() {
    List<User> invitedUsers = new ArrayList<>();
    assertEquals(wakeUp.invitees(), invitedUsers);
    // Add someone not on the invitee list
    wakeUp.changeInvited(david);
    invitedUsers.add(david);
    assertEquals(wakeUp.invitees(), invitedUsers);
    // Remove someone from invitee list
    wakeUp.changeInvited(david);
    invitedUsers.remove(david);
    assertEquals(wakeUp.invitees(), invitedUsers);
  }
}
