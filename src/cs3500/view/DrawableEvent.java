package cs3500.view;

import java.awt.Point;
import java.awt.Rectangle;
import java.time.LocalTime;
import java.util.List;

import cs3500.model.Event;

/**
 * Custom class for keeping track of which cells on the schedule are events.
 */
public class DrawableEvent {
  private final Rectangle bounds;
  protected final Event event;
  private final boolean host;

  protected final String name;
  protected final String location;
  protected final boolean online;
  protected final int startDay;
  protected final int endDay;
  protected final LocalTime startTime;
  protected final LocalTime endTime;

  /**
   * Creates a DrawableEvent that is represented by the bounds of a Rectangle, event information,
   * and a boolean value for the host.
   * @param bounds the bounds of the event's timing
   * @param event the event with information to be parsed
   * @param host boolean value to assert whether the user is the host of this event
   */
  public DrawableEvent(Rectangle bounds, Event event, boolean host) {
    this.bounds = bounds;
    this.event = event;
    this.host = host;

    List<String> eventInfo = event.eventInfo();
    this.name = eventInfo.get(0);
    this.location = eventInfo.get(5);
    this.online = Boolean.parseBoolean(eventInfo.get(6));

    this.startDay = event.dayInfo().get(0);
    this.endDay = event.dayInfo().get(1);

    this.startTime = event.timeInfo().get(0);
    this.endTime = event.timeInfo().get(1);
  }

  protected boolean containsPoint(Point p) {
    return bounds.contains(p);
  }

  protected String observeEvent() {
    return this.name;
  }

  protected boolean observeHost() {
    return this.host;
  }
}
