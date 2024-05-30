package cs3500.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class for keeping track of a User who has a unique ID and an associated schedule of events.
 */
public class User implements IUser {
  private final String id;
  // Invariant: value is never empty or null
  private final List<Event> schedule;
  // Invariant: value never contains null Events

  /**
   * Public constructor for a User.
   * Includes the user's id and schedule.
   *
   * @param id The id of the user to be created.
   */
  public User(String id) {
    if (id == null || id.isEmpty()) {
      throw new IllegalArgumentException("ID cannot be null or empty");
    }
    this.id = id;
    this.schedule = new ArrayList<>();
  }

  @Override
  public String userId() {
    return this.id;
  }

  @Override
  public void addEvent(Event event) throws IllegalArgumentException {
    Objects.requireNonNull(event);
    if (ValidateEvent.validEvent(event, schedule) && userInEvent(event)
            && ValidateEvent.nonZeroMinuteEvent(event.dayInfo().get(0), event.dayInfo().get(1),
            event.timeInfo().get(0), event.timeInfo().get(1))) {
      schedule.add(event);
    } else {
      throw new IllegalArgumentException("Event overlaps with another event or is too short");
    }
  }

  /**
   * Validates whether a user is part of an event or not, to decide if the event should be added to
   * the user's schedule.
   *
   * @param event the event being checked
   * @return true if the user is in the event, false otherwise
   */
  private boolean userInEvent(Event event) {
    return event.hostInfo().equals(this) || event.invitees().contains(this);
  }

  @Override
  public void removeEvent(Event event) throws IllegalArgumentException {
    if (!schedule.remove(event)) {
      throw new IllegalArgumentException("Event is not on the schedule");
    }
  }

  @Override
  public Event findEventByName(String eventName) throws IllegalArgumentException {
    for (Event event : this.schedule) {
      if (event.toString().equals(eventName)) {
        return event;
      }
    }
    throw new IllegalArgumentException("Event does not exist in this user's schedule");
  }

  @Override
  public List<Event> userSchedule() {
    return new ArrayList<>(schedule);
  }

  @Override
  public List<Event> checkTime(DayOfWeek day, LocalTime time) {
    List<Event> events = new ArrayList<>();
    for (Event event : schedule) {
      if (event.checkTime(time, day)) {
        events.add(event);
      }
    }
    if (events.isEmpty()) {
      return null;
    } else {
      return events;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    User user = (User) obj;
    return id.equals(user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}

