package cs3500.controller;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cs3500.model.DayOfWeek;
import cs3500.model.Event;
import cs3500.model.ICentralSystem;
import cs3500.model.ScheduleWriteException;
import cs3500.model.User;
import cs3500.view.EventFrame;
import cs3500.view.IMainSystemFrame;
import cs3500.view.SchedulePanel;
import cs3500.view.SchedulingFrame;

/**
 * Controller specified to handle inputs and outputs for a GUI-based view.
 */
public class GUIController implements Features {

  private final ICentralSystem model;
  private IMainSystemFrame view;
  private SchedulingStrategy strategy;

  /**
   * Creates a controller that takes in a mutable model and a view as parameters.
   * @param model the mutable model
   * @param view the view
   */
  public GUIController(ICentralSystem model, IMainSystemFrame view) {
    this.model = Objects.requireNonNull(model);
    this.view = Objects.requireNonNull(view);
  }

  @Override
  public void makeVisible() {
    view.displayMainFrame();
  }

  @Override
  public void setView(IMainSystemFrame view) {
    this.view = view;
    view.addFeatures(this);
  }

  @Override
  public void setStrategy(SchedulingStrategy strategy) {
    this.strategy = strategy;
  }

  @Override
  public void createEvent(List<String> eventInfo, List<String> invitedUsers) {
    boolean isOnline = Boolean.parseBoolean(eventInfo.get(2));

    LocalTime startTime = LocalTime.parse(eventInfo.get(3), DateTimeFormatter.ofPattern("HHmm"));
    LocalTime endTime = LocalTime.parse(eventInfo.get(4), DateTimeFormatter.ofPattern("HHmm"));

    DayOfWeek startDay = DayOfWeek.valueOf(eventInfo.get(5).toUpperCase());
    DayOfWeek endDay = DayOfWeek.valueOf(eventInfo.get(6).toUpperCase());

    List<User> invitees = new ArrayList<>();
    for (String userId : invitedUsers) {
      invitees.add(model.findUserById(userId));
    }

    try {
      model.createEvent(eventInfo.get(0), eventInfo.get(1), isOnline, startTime, endTime,
              startDay, endDay, model.findUserById(model.currentUser()), invitees);
      view.setPanel(new SchedulePanel(model));
      view.addFeatures(this);
    } catch (IllegalArgumentException | IllegalStateException ex) {
      view.errorMessage(ex.getMessage());
    }
  }

  @Override
  public void modifyEvent(List<String> eventInfo, List<String> selectedUsers, String eventName) {
    try {
      Map<String, String> changes = new HashMap<>();
      changes.put("name", eventInfo.get(0));
      changes.put("location", eventInfo.get(1));
      changes.put("online", eventInfo.get(2));
      changes.put("start-time", eventInfo.get(3));
      changes.put("end-time", eventInfo.get(4));
      changes.put("start", eventInfo.get(5));
      changes.put("end", eventInfo.get(6));
      changes.put("invited", String.join(" ", selectedUsers));
      model.modifyEvent(model.findEventByName(eventName), changes);
      view.setPanel(new SchedulePanel(model));
      view.addFeatures(this);
    } catch (IllegalArgumentException ex) {
      view.errorMessage("Could not modify event: " + ex.getMessage());
    }
  }

  @Override
  public void removeEvent(String eventName) {
    try {
      model.updateEventInvited(model.findEventByName(eventName),
              model.findUserById(model.currentUser()));
      view.setPanel(new SchedulePanel(model));
      view.addFeatures(this);
    } catch (IllegalArgumentException ex) {
      view.errorMessage("Could not remove event: " + ex.getMessage());
    }
  }

  @Override
  public void scheduleEvent(int duration, List<String> eventInfo, List<String> users) {
    try {
      List<String> allUsers = new ArrayList<>(users);
      Event event = strategy.findTime(duration, allUsers, eventInfo);

      model.addEvent(event);
      view.setPanel(new SchedulePanel(model));
      view.addFeatures(this);
    } catch (IllegalArgumentException ex) {
      view.errorMessage("Could not find an open slot of this duration");
    }
  }

  @Override
  public void setUser(String userId) {
    model.setUser(userId);
  }

  @Override
  public void createEventFrame() {
    EventFrame eventFrame = new EventFrame(model, true, false);
    eventFrame.setVisible(true);
    eventFrame.addFeatures(this);
  }

  @Override
  public void createSchedulingFrame() {
    SchedulingFrame schedulingFrame = new SchedulingFrame(model);
    schedulingFrame.setVisible(true);
    schedulingFrame.addFeatures(this);
  }

  @Override
  public void saveSchedule(File selectedDir) throws ScheduleWriteException {
    try {
      for (String userId : model.findAllUsers()) {
        File fullFilePath = new File(selectedDir, userId + ".xml");
        FileOperations.saveSchedule(model, userId, fullFilePath.getAbsolutePath());
      }
    } catch (ScheduleWriteException ex) {
      view.errorMessage(ex.getMessage());
    }
  }

  @Override
  public void uploadSchedule(String filePath) throws IllegalStateException {
    try {
      FileOperations.uploadSchedule(model, filePath);
      view.updateUsers();
    } catch (IllegalStateException ex) {
      view.errorMessage(ex.getMessage());
    }
  }
}
