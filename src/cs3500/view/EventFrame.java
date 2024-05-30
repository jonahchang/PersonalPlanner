package cs3500.view;

import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;

import cs3500.controller.Features;
import cs3500.model.DayOfWeek;
import cs3500.model.IReadOnlyCentralSystem;

/**
 * The event frame and panel for creating, modifying, or removing an event.
 */
public class EventFrame extends JFrame implements IEventFrame {

  private String event;
  private final boolean host; // Whether the frame is being used by the host of the event.
  private final boolean modify;
  private final IReadOnlyCentralSystem model;

  private JButton createButton;
  private JButton modifyButton;
  private JButton removeButton;

  private JTextArea eventName;
  private JTextArea location;
  private JComboBox<String> onlineStatus;
  private JComboBox<String> startDay;
  private JSpinner startTime;
  private JComboBox<String> endDay;
  private JSpinner endTime;
  private JList<String> userList;

  /**
   * Creates an event frame that can create, modify, or remove an event.
   */
  public EventFrame(IReadOnlyCentralSystem model, boolean host, boolean modify) {
    this.model = model;
    this.host = host;
    this.modify = modify;
    initializeUI();
  }

  private void initializeUI() {
    setTitle("Event Frame");
    JPanel panel = new JPanel();
    FrameSetupHelper.setUp(this, panel);
    // Event name field:
    eventName = new JTextArea(2, 20);
    // Event location field:
    location = new JTextArea(2, 20);
    // Online status
    onlineStatus = new JComboBox<>(new String[]{"true", "false"});
    FrameSetupHelper.commonFields(eventName, location, onlineStatus, panel);

    // Start and end days
    List<String> days = new ArrayList<>();
    for (DayOfWeek day : DayOfWeek.values()) {
      days.add(day.toString());
    }
    createDaysAndTime(days, panel);

    // Multi-select list of users
    List<String> users = model.findAllUsers();
    if (host) {
      users.remove(model.currentUser());
    }
    DefaultListModel<String> userModel = new DefaultListModel<>();
    users.forEach(userModel::addElement);
    userList = new JList<>(userModel);
    FrameSetupHelper.invitedUsers(panel, userList);
    // Add buttons at the bottom
    createButtons(panel);
    // Add the panel to the frame
    getContentPane().add(panel);
  }

  private void createDaysAndTime(List<String> days, JPanel panel) {
    // Initialize start and end day dropdown menu
    startDay = new JComboBox<>(days.toArray(new String[0]));
    startDay.setBorder(BorderFactory.createTitledBorder("Start Day: "));
    endDay = new JComboBox<>(days.toArray(new String[0]));
    endDay.setBorder(BorderFactory.createTitledBorder("End Day: "));
    // Initialize time spinner menu
    Calendar calendar = Calendar.getInstance();
    Date time = calendar.getTime();
    SpinnerDateModel startSpinner = new SpinnerDateModel(time, null, null,
            Calendar.HOUR_OF_DAY);
    SpinnerDateModel endSpinner = new SpinnerDateModel(time, null, null,
            Calendar.HOUR_OF_DAY);

    startTime = new JSpinner(startSpinner);
    startTime.setBorder(BorderFactory.createTitledBorder("Start Time: "));
    endTime = new JSpinner(endSpinner);
    endTime.setBorder(BorderFactory.createTitledBorder("End Time: "));

    JSpinner.DateEditor startFormat = new JSpinner.DateEditor(startTime, "HH:mm");
    JSpinner.DateEditor endFormat = new JSpinner.DateEditor(endTime, "HH:mm");
    startTime.setEditor(startFormat);
    endTime.setEditor(endFormat);

    panel.add(startDay);
    panel.add(startTime);
    panel.add(endDay);
    panel.add(endTime);
  }

  private void createButtons(JPanel panel) {
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    createButton = new JButton("Create Event");
    removeButton = new JButton("Remove Event");
    modifyButton = new JButton("Modify Event");

    if (modify) {
      buttonPanel.add(modifyButton);
      buttonPanel.add(removeButton);
    } else {
      buttonPanel.add(createButton);
    }

    // Add buttons to the panel
    panel.add(buttonPanel);
  }

  @Override
  public void setFields(String name, String location, boolean online, int startDay, int endDay,
                        LocalTime startTime, LocalTime endTime) {
    eventName.setText(name);
    this.location.setText(location);
    onlineStatus.setSelectedItem(online ? "is online" : "offline");
    this.startDay.setSelectedIndex(startDay - 1);
    this.endDay.setSelectedIndex(endDay - 1);
    this.startTime.setValue(localTimeToDate(startTime));
    this.endTime.setValue(localTimeToDate(endTime));
  }

  private Date localTimeToDate(LocalTime time) {
    LocalDate today = LocalDate.now();
    Instant instant = time.atDate(today).atZone(ZoneId.systemDefault()).toInstant();
    return Date.from(instant);
  }

  @Override
  public void addFeatures(Features feature) {

    createButton.addActionListener(e -> {
      if (validEntries()) {
        List<String> eventInfo = parseEventInfo();
        feature.createEvent(eventInfo,
                userList.getSelectedValuesList());
        dispose();
      } else {
        errorMessage("Invalid fields");
      }
    });

    modifyButton.addActionListener(e -> {
      if (validEntries()) {
        List<String> eventInfo = parseEventInfo();
        feature.modifyEvent(eventInfo,
                userList.getSelectedValuesList(), event);
        dispose();
      } else {
        errorMessage("Invalid fields");
      }
    });

    removeButton.addActionListener(e -> {
      feature.removeEvent(event);
      dispose();
    });
  }

  private List<String> parseEventInfo() {
    return List.of(eventName.getText(), location.getText(),
            onlineStatus.getItemAt(onlineStatus.getSelectedIndex()),
            new SimpleDateFormat("HHmm").format(startTime.getValue()),
            new SimpleDateFormat("HHmm").format(endTime.getValue()),
            startDay.getItemAt(startDay.getSelectedIndex()),
            endDay.getItemAt(endDay.getSelectedIndex()));
  }

  @Override
  public void setEvent(String eventName) {
    this.event = eventName;
  }

  @Override
  public void errorMessage(String message) {
    JOptionPane.showMessageDialog(this, message);
  }

  private boolean validEntries() {
    return !eventName.getText().trim().isEmpty()
            && !location.getText().trim().isEmpty();
  }

  private String eventInfo() {
    StringBuilder info = new StringBuilder();
    // Extract from text boxes
    appendInfo(info, "Name: " + eventName.getText());
    appendInfo(info, "Location: " + location.getText());
    // Extract online info and date and time info
    appendInfo(info, "Online status: " + onlineStatus.getSelectedItem());

    appendInfo(info, "Start day: " + startDay.getSelectedItem());
    extractTime(true, info);

    appendInfo(info, "End day: " + endDay.getSelectedItem());
    extractTime(false, info);
    extractUsers(info);
    return info.toString();
  }

  private void extractTime(boolean start, StringBuilder info) {
    SimpleDateFormat formatter = new SimpleDateFormat("HHmm");
    if (start) {
      // Extract time information
      Date selectedStart = (Date) startTime.getValue();
      appendInfo(info, "Start time: " + formatter.format(selectedStart));
    } else {
      Date selectedEnd = (Date) endTime.getValue();
      appendInfo(info, "End time: " + formatter.format(selectedEnd));
    }
  }

  private void extractUsers(StringBuilder info) {
    appendInfo(info, "Host: " + model.currentUser());
    List<String> selectedUsers = userList.getSelectedValuesList();
    selectedUsers.remove(model.currentUser());
    appendInfo(info, "Update users: " + selectedUsers);
  }

  private void appendInfo(StringBuilder info, String message) {
    info.append(message).append("\n");
  }
}
