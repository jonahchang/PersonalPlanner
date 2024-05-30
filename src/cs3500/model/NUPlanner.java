package cs3500.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cs3500.controller.FileOperations;

/**
 * A central system to keep track of all events, users, and each user's schedule.
 */
public class NUPlanner implements ICentralSystem {
  private final Map<String, User> users; // A map of all users and their associated ID's
  // Invariant: value is never empty
  private final List<Event> events; // A list of all events in the system
  // Invariant: value never contains overlapping events
  private String currentUser; // The user whose schedule is currently being viewed
  // Invariant: value is never null

  /**
   * Creates a NUPlanner system with an empty map of users and list of events.
   * The default user is "admin", who can access and modify any event.
   */
  public NUPlanner() {
    this.users = new LinkedHashMap<>();
    this.events = new ArrayList<>();
    User adminUser = new User("admin");
    this.currentUser = "admin"; // Admin is the default "user"
    users.put("admin", adminUser); // The default user represents the state where no schedule
    // has been selected for viewing yet
  }

  /**
   * Constructor for creating a model with users and schedules already loaded in.
   *
   * @param filePath the file to load in
   */
  public NUPlanner(String filePath) {
    this.users = new LinkedHashMap<>();
    this.events = new ArrayList<>();
    User adminUser = new User("admin");
    this.currentUser = "admin";
    users.put("admin", adminUser);

    try {
      FileOperations.uploadSchedule(this, filePath);
    } catch (IllegalStateException ex) {
      throw new IllegalArgumentException("Invalid file path or error parsing file");
    }
  }

  @Override
  public void setUser(String userId) throws IllegalArgumentException {
    User newUser = users.get(userId); // Check if the user is in the database
    if (newUser == null) {
      throw new IllegalArgumentException("User is not in the system");
    }
    if (!userId.isEmpty()) {
      newUser = findUserById(userId); // If not, create a new user
    }
    this.currentUser = newUser.userId(); // Change the current user to the new user
  }

  @Override
  public void addUser(User userId) throws IllegalArgumentException {
    if (findUserById(userId.userId()) == null) { // If the user does not exist in the system
      users.put(userId.userId(), userId); // Put the new user in the users database
      for (Event event : events) {
        if (event.invitees().contains(userId)) { // If the user is invited to the event
          try {
            userId.addEvent(event); // Add event to the user's schedule
          } catch (IllegalArgumentException ex) {
            // User already has event in schedule
          }
        }
      }
      User tempUser = users.get(currentUser);
      setUser(userId.userId());
      for (Event event : findUserById(currentUser).userSchedule()) {
        // Add all the user's events to the database
        try {
          addEvent(event);
        } catch (IllegalArgumentException ex) {
          // Do not add event
        }
      }
      setUser(tempUser.userId());
    } else {
      throw new IllegalArgumentException("User already exists in the system.");
    }
  }

  @Override
  public String currentUser() {
    return currentUser;
  }

  @Override
  public User findUserById(String userId) {
    return users.get(userId); // Null if the user is not in the system
  }

  @Override
  public Event findEventByName(String eventName) {
    for (Event event : events) {
      if (event.toString().equals(eventName)) {
        return event;
      }
    }
    return null;
  }

  @Override
  public List<Event> findEventsByTime(String day, String time) {
    // Format the time String
    LocalTime checkTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));
    // Convert the day String into a DayOfWeek constant
    DayOfWeek checkDay = DayOfWeek.valueOf(day.toUpperCase().trim());
    return findUserById(currentUser).checkTime(checkDay, checkTime);
  }

  @Override
  public void addEvent(Event event) throws IllegalArgumentException {
    for (Event scheduled : events) {
      if (event.equals(scheduled)) { // Check if the name matches any existing events
        throw new IllegalArgumentException("Event already exists in system");
      }
    }
    events.add(event);
    try {
      event.hostInfo().addEvent(event);
    } catch (IllegalArgumentException ex) {
      // Do nothing, event already in user's schedule
    }
    for (User invitee : event.invitees()) {
      try {
        invitee.addEvent(event);
      } catch (IllegalArgumentException ex) {
        // Do nothing, event already added
      }
    }
  }

  @Override
  public void modifyEvent(Event event, Map<String, String> change) {
    if (!events.contains(event)) {
      throw new IllegalArgumentException("Event does not exist");
    }
    if (!change.get("online").equalsIgnoreCase("false")
            && !change.get("online").equalsIgnoreCase("true")) {
      // If the new value isn't exactly true or false, throw an exception
      throw new IllegalArgumentException("Not a valid boolean type");
    }
    String[] updatedUsers;
    if (change.get("invited").trim().isEmpty()) {
      updatedUsers = new String[0];
    } else {
      updatedUsers = change.get("invited").split("\\s+");
    }
    temporaryInvitee(event, updatedUsers);

    try {
      temporaryTimes(event, change);
    } catch (IllegalArgumentException ex) {
      temporaryInvitee(event, updatedUsers);
      throw new IllegalArgumentException("Invalid changes to the event");
    }

    // All checks passed
    applyChanges(event, change);
  }

  private void applyChanges(Event event, Map<String, String> change) {
    event.updateName(change.get("name"));
    event.updateLocation(change.get("location"));
    event.updateIsOnline(Boolean.parseBoolean(change.get("online").toLowerCase()));
  }

  private void temporaryInvitee(Event event, String[] updatedUsers) {
    for (String userId : updatedUsers) {
      event.changeInvited(findUserById(userId));
    }
  }

  private void temporaryTimes(Event event, Map<String, String> change) {
    List<LocalTime> oldTimes = event.timeInfo();
    List<Integer> oldDays = event.dayInfo();
    LocalTime newStartTime;
    LocalTime newEndTime;

    try {
      newStartTime = LocalTime.parse(change.get("start-time"),
              DateTimeFormatter.ofPattern("HHmm"));
      newEndTime = LocalTime.parse(change.get("end-time"),
              DateTimeFormatter.ofPattern("HHmm"));
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("Invalid new time");
    }
    DayOfWeek newStartDay = DayOfWeek.valueOf(change.get("start").toUpperCase());
    DayOfWeek newEndDay = DayOfWeek.valueOf(change.get("end").toUpperCase());

    event.updateStartEnd(newStartTime, newEndTime, newStartDay, newEndDay);
    if (event.hostInfo().checkTime(newStartDay, newStartTime) != null
            || event.hostInfo().checkTime(newEndDay, newEndTime) != null) {
      event.updateStartEnd(oldTimes.get(0), oldTimes.get(1),
              DayOfWeek.dayOf(oldDays.get(0)), DayOfWeek.dayOf(oldDays.get(1)));
      throw new IllegalArgumentException("Invalid new time");
    }
    for (User user : event.invitees()) {
      if (user.checkTime(newStartDay, newStartTime) != null
              || user.checkTime(newEndDay, newEndTime) != null) {
        event.updateStartEnd(oldTimes.get(0), oldTimes.get(1),
                DayOfWeek.dayOf(oldDays.get(0)), DayOfWeek.dayOf(oldDays.get(1)));
        throw new IllegalArgumentException("Invalid new time");
      }
    }
  }

  @Override
  public void updateEventInvited(Event event, User user) {
    if (!events.contains(event)) {
      throw new IllegalArgumentException("Event does not exist in the system");
    }
    if (user.equals(event.hostInfo())) { // If the user is the host
      events.remove(event); // Remove the event entirely
      user.removeEvent(event);
      for (User invited : event.invitees()) {
        invited.removeEvent(event); // Remove all invited users too
      }
    } else if (event.invitees().contains(user)) { // If the user is just an invitee
      event.changeInvited(user); // Remove the user from the invited list
      user.removeEvent(event); // Remove the event from the user's schedule
    } else {
      event.changeInvited(user); // Add the user to the event and the event to the user's schedule
      user.addEvent(event);
    }
  }

  @Override
  public boolean scheduleEvent(Event event, List<String> users) {
    User temp = findUserById(currentUser);
    if (!ValidateEvent.validEvent(event, temp.userSchedule())) {
      return false;
    }
    for (String userId : users) {
      temp = findUserById(userId);
      if (!ValidateEvent.validEvent(event, temp.userSchedule())) {
        return false;
      }
    }
    return true;
  }

  @Override
  public List<String> findAllUsers() {
    List<String> allUsers = new ArrayList<>(users.keySet());
    allUsers.remove("admin");
    return allUsers;
  }

  @Override
  public Map<List<String>, List<String>> userSchedule(boolean allUsers) {
    Map<List<String>, List<String>> userEvents = new LinkedHashMap<>();
    if (allUsers) { // Case for getting all users' schedules
      for (User user : users.values()) {
        if (!user.userId().equals("admin")) {
          currentUser = user.userId();
          scheduleHelper(findUserById(currentUser), userEvents);
        }
      }
    } else {
      scheduleHelper(findUserById(currentUser), userEvents); // Get just one user's schedule
    }
    // Return a sorted-by-time version of the map
    return userEvents.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey(Comparator.comparing(
              list -> Integer.parseInt(list.get(2)))))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
              (oldValue, newValue) -> oldValue, LinkedHashMap::new));
  }

  @Override
  public void createEvent(String name, String location, boolean isOnline, LocalTime startTime,
                          LocalTime endTime, DayOfWeek startDay, DayOfWeek endDay,
                          User host, List<User> invitedUsers) throws IllegalStateException {
    Event event = new Event(name, location, isOnline, startTime, endTime,
            startDay, endDay, host, invitedUsers); // Create the new event

    User current = findUserById(currentUser);
    if (current != findUserById("admin")) {
      addEvent(event); // Add the event to the overall database
      try {
        current.addEvent(event);
      } catch (IllegalArgumentException ex) {
        // Do nothing; the event is already there or overlaps with another
      }
      for (User invited : event.invitees()) { // Add the event to the invitees' schedules
        try {
          invited.addEvent(event);
        } catch (IllegalArgumentException ex) {
          // Already in their schedule
        }
      }
    } else { // For when the client has not yet selected a user from the GUI dropdown menu
      throw new IllegalStateException("Please select a user to create an event for");
    }
  }

  /**
   * Helper method for getting the schedule info for a certain user.
   *
   * @param user       the user whose schedule is being queried
   * @param userEvents the map to add the event information to
   */
  private void scheduleHelper(User user, Map<List<String>, List<String>> userEvents) {
    for (Event event : user.userSchedule()) { // Iterate through the events in a user's schedule
      List<String> eventInfo = event.eventInfo();
      List<String> userInfo = new ArrayList<>(); // Create new list
      for (User invited : event.invitees()) { // Add the invitees' IDs to the new list
        userInfo.add(invited.userId());
      }
      userEvents.put(eventInfo, userInfo);
    }
  }
}
