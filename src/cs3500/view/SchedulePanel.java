package cs3500.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cs3500.controller.Features;
import cs3500.model.DayOfWeek;
import cs3500.model.Event;
import cs3500.model.IReadOnlyCentralSystem;
import cs3500.model.User;

/**
 * Schedule Panel for displaying the schedule in the NUPlanner main system's frame.
 */
public class SchedulePanel extends JPanel implements IMainPanel {

  private final IReadOnlyCentralSystem model;
  private final List<DrawableEvent> drawableEvents = new ArrayList<>();
  private static final int ROWS = 24; // 24 hours in a day; 24 rows
  private static final int COLS = 7; // 7 days in a week

  private JComboBox<String> userDropdown;
  private JButton createEventButton;
  private JButton scheduleEventButton;

  /**
   * Creates the schedule panel with all lines and buttons set up.
   */
  public SchedulePanel(IReadOnlyCentralSystem model) {
    this.model = model;
    initialize();
  }

  private void initialize() {
    this.setLayout(new BorderLayout());

    initializeButtons();
    String initialUser = userDropdown.getItemAt(userDropdown.getSelectedIndex());
    model.setUser(Objects.requireNonNullElse(initialUser, "admin"));
  }

  private void initializeButtons() {
    // Panel to hold buttons
    JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 0));

    userDropdown = new JComboBox<>(model.findAllUsers().toArray(new String[0]));
    buttonPanel.add(userDropdown);

    createEventButton = new JButton("Create Event");
    buttonPanel.add(createEventButton);

    scheduleEventButton = new JButton("Schedule Event");
    buttonPanel.add(scheduleEventButton);

    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  private void onEventClicked(int x, int y, Features feature) {
    int drawableHeight = getHeight() - 40;
    float hoursPerPixel = 24f / drawableHeight;
    int hour = (int) (y * hoursPerPixel);
    int minute = (int) ((y * hoursPerPixel - hour) * 60);

    String timeStr = String.format("%02d%02d", hour, minute);

    // If you also need to find out the day of the week that was clicked
    int columnWidth = getWidth() / COLS;
    int dayOfWeek = x / columnWidth + 1;
    DayOfWeek day = DayOfWeek.dayOf(dayOfWeek);

    for (DrawableEvent event : drawableEvents) {
      if (event.containsPoint(new Point(x, y))) {
        EventFrame eventFrame = new EventFrame(model, event.observeHost(), true);
        eventFrame.setFields(event.name, event.location, event.online,
                event.startDay, event.endDay, event.startTime, event.endTime);
        eventFrame.setEvent(event.name);
        eventFrame.addFeatures(feature);
        eventFrame.setVisible(true);
      }
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;

    displaySchedule(model.currentUser(), g2d);
    g2d.setColor(Color.BLACK);

    int spaceReservedForButtons = 40;
    int drawableHeight = getHeight() - spaceReservedForButtons;
    // row is indexed starting from 1 because it
    // doesn't make sense to draw a line at the very
    // top of the board
    for (int row = 1; row < ROWS; row++) {
      if (row % 4 == 0) {
        g2d.setStroke(new BasicStroke(4));
      } else {
        g2d.setStroke(new BasicStroke(1));
      }
      g2d.drawLine(0, row * drawableHeight / ROWS,
              getWidth(), row * drawableHeight / ROWS);
    }
    // column is indexed starting from 1 because it
    // doesn't make sense to draw a line at the very
    // left of the board
    for (int column = 1; column < COLS; column++) {
      g2d.drawLine(column * getWidth() / COLS, 0,
              column * getWidth() / COLS, drawableHeight);
    }
  }


  @Override
  public void addFeatures(Features features) {
    userDropdown.addActionListener(e -> {
      features.setUser(userDropdown
              .getItemAt(userDropdown.getSelectedIndex()));
      repaint();
    });
    createEventButton.addActionListener(e -> {
      if (userDropdown.getItemCount() != 0) {
        features.createEventFrame();
      } else {
        JOptionPane.showMessageDialog(this, "No users loaded in yet");
      }
    });
    scheduleEventButton.addActionListener(e -> {
      if (userDropdown.getItemCount() != 0) {
        features.createSchedulingFrame();
      } else {
        JOptionPane.showMessageDialog(this, "No users loaded in yet");
      }
    });

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        onEventClicked(e.getX(), e.getY(), features);
      }
    });
  }

  @Override
  public void displaySchedule(String userId, Graphics2D g2d) {
    User displayUser = model.findUserById(userId);
    drawableEvents.clear();

    for (Event event : displayUser.userSchedule()) {
      int startDay = event.dayInfo().get(0) - 1;
      int endDay = event.dayInfo().get(1) - 1;
      LocalTime startTime = event.timeInfo().get(0);
      LocalTime endTime = event.timeInfo().get(1);

      g2d.setColor(Color.RED);
      boolean nextWeek = (startDay > endDay
              || (startDay == endDay && startTime.isAfter(endTime)));
      if (nextWeek) {
        endDay = 6;
        endTime = LocalTime.of(23, 59);
      }

      int columnWidth = getWidth() / COLS;

      for (int day = startDay; day <= endDay; day++) {
        int currentColumn = day % COLS;
        int startY = (day == startDay) ? calcOnScreenY(startTime) : 0;
        int endY = (day == endDay) ? calcOnScreenY(endTime) : getHeight() - 40;
        boolean host = event.hostInfo().equals(displayUser);

        Rectangle rect = new Rectangle(currentColumn * columnWidth, startY,
                columnWidth, endY - startY);
        g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
        drawableEvents.add(new DrawableEvent(rect, event, host));
      }
    }
  }

  @Override
  public void updateUsers() {
    if (userDropdown.getItemCount() == 0) {
      for (String user : model.findAllUsers()) {
        userDropdown.addItem(user);
      }
    } else {
      for (String user : model.findAllUsers()) {
        if (!userExists(user)) {
          userDropdown.addItem(user);
        }
      }
    }
  }

  private boolean userExists(String user) {
    for (int index = 0; index < userDropdown.getItemCount(); index++) {
      if (user.equals(userDropdown.getItemAt(index))) {
        return true;
      }
    }
    return false;
  }

  /**
   * We had to cast the calculated y-value to an int because
   * fillRect requires the x- and y-coordinates of the
   * top left corner to be ints.
   */
  private int calcOnScreenY(LocalTime givenTime) {
    float numHoursSinceStartOfDay = givenTime.getHour() + (givenTime.getMinute() / 60f);
    // Adjust drawableHeight to consider spaceReservedForButtons.
    int drawableHeight = getHeight() - 40;
    return (int) (drawableHeight * numHoursSinceStartOfDay / 24);
  }
}
