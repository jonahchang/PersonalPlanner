package cs3500.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An Event object that holds information on an event taking place to be added to a schedule.
 * The information can be accessed to give the client information about the Event.
 */
public class Event implements IEvent {
  private String name;
  // Invariant: value is never null or empty
  private String location;
  // Invariant: value is never null or empty
  private boolean isOnline;
  private LocalTime startTime;
  private LocalTime endTime;
  private DayOfWeek startDay;
  private DayOfWeek endDay;
  private final User host; // The host cannot change
  // Invariant: value is never null
  /**
   * NOTE: It is possible for an event to have no invited users.
   * (e.g. someone going out for a solo run; it makes no sense
   * to have invited users for that event because we just have
   * that person as the host and invitedUsers be null).
   */
  private final List<User> invitedUsers;

  protected boolean nextWeek;

  /**
   * Creates an event object with all parameters initialized.
   *
   * @param name         name of the event
   * @param location     location of the event
   * @param isOnline     true if the event is online, false otherwise
   * @param startTime    start time of the event, given as a LocalTime object
   * @param endTime      end time of the event, given as a LocalTime object
   * @param startDay     start day of the event
   * @param endDay       end day of the event
   * @param host         the host of the event
   * @param invitedUsers a list of all users that are invited to the event
   */
  public Event(String name, String location, Boolean isOnline,
               LocalTime startTime, LocalTime endTime, DayOfWeek startDay, DayOfWeek endDay,
               User host, List<User> invitedUsers) {
    this.name = Objects.requireNonNull(name, "Name cannot be null");
    this.location = Objects.requireNonNull(location, "Location cannot be null");
    this.isOnline = Objects.requireNonNull(isOnline, "isOnline cannot be null");
    this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
    this.endTime = Objects.requireNonNull(endTime, "End time cannot be null");
    this.startDay = Objects.requireNonNull(startDay, "Start day cannot be null");
    this.endDay = Objects.requireNonNull(endDay, "End day cannot be null");
    this.host = Objects.requireNonNull(host, "Host cannot be null");
    this.invitedUsers = invitedUsers != null ? new ArrayList<>(invitedUsers) : new ArrayList<>();

    if (this.name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }
    if (this.location.trim().isEmpty()) {
      throw new IllegalArgumentException("Location cannot be empty");
    }

    removeHost();
  }

  private void removeHost() {
    invitedUsers.removeIf(host -> host.equals(this.host));
  }

  @Override
  public List<String> eventInfo() {
    List<String> eventInfo = new ArrayList<>();

    eventInfo.add(this.name); // name

    eventInfo.add(startDay.toString()); // start day
    eventInfo.add(formatTime(startTime)); // start time
    eventInfo.add(endDay.toString()); // end day
    eventInfo.add(formatTime(endTime)); // end time

    eventInfo.add(location); // location
    eventInfo.add(String.valueOf(isOnline)); // online status
    eventInfo.add(host.userId()); // host's ID
    return eventInfo;
  }

  @Override
  public User hostInfo() {
    return this.host;
  }

  @Override
  public List<Integer> dayInfo() {
    List<Integer> dayInfo = new ArrayList<>();
    dayInfo.add(this.startDay.getValue());
    dayInfo.add(this.endDay.getValue());
    return dayInfo;
  }

  @Override
  public List<LocalTime> timeInfo() {
    List<LocalTime> timeInfo = new ArrayList<>();
    timeInfo.add(this.startTime);
    timeInfo.add(this.endTime);
    return timeInfo;
  }

  @Override
  public List<User> invitees() {
    return new ArrayList<>(invitedUsers);
  }

  @Override
  public boolean checkTime(LocalTime time, DayOfWeek day) {
    int adjustedEndDay = endDay.getValue();
    LocalTime adjustedEndTime = endTime;
    if (startDay.getValue() > endDay.getValue()
            || (startDay.getValue() == endDay.getValue() && startTime.compareTo(endTime) > 0)) {
      adjustedEndDay += 7;
      adjustedEndTime = LocalTime.of(23, 59);
    }
    // Checks if the given time is between the start and end times
    return (day.getValue() >= startDay.getValue() && day.getValue() <= adjustedEndDay)
            && (time.compareTo(startTime) > 0 && time.compareTo(adjustedEndTime) < 0);
  }

  @Override
  public void changeInvited(User user) {
    if (invitedUsers.contains(user)) { // If user is on the list
      invitedUsers.remove(user);
    } else if (!user.equals(host)) { // If the user was not invited
      invitedUsers.add(user);
    } else { // If the user is the host
      throw new IllegalArgumentException("User is the host");
    }
  }

  @Override
  public void updateName(String newName) throws IllegalArgumentException {
    if (newName == null || newName.trim().isEmpty()) {
      throw new IllegalArgumentException("Invalid new name");
    }
    this.name = newName;
  }

  @Override
  public void updateLocation(String newLocation) throws IllegalArgumentException {
    if (newLocation == null || newLocation.trim().isEmpty()) {
      throw new IllegalArgumentException("Invalid new location");
    }
    this.location = newLocation;
  }

  @Override
  public void updateIsOnline(boolean isOnline) {
    this.isOnline = isOnline;
  }

  @Override
  public void updateStartEnd(LocalTime startTime, LocalTime endTime,
                             DayOfWeek startDay, DayOfWeek endDay) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.startDay = startDay;
    this.endDay = endDay;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Event other = (Event) obj;
    return name.equals(other.name); // Since names for events must be unique, the equals method
    // only checks if the names are equal
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, host);
  }

  @Override
  public String toString() {
    return this.name;
  }

  private String formatTime(LocalTime time) {
    // Format the time into two digits for the hour and two digits for the minutes
    return time.format(DateTimeFormatter.ofPattern("HHmm"));
  }
}

