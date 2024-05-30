package cs3500.view;

import java.awt.FlowLayout;
import java.util.List;


import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;


import cs3500.controller.Features;
import cs3500.model.IReadOnlyCentralSystem;


/**
 * Frame for displaying the fields to schedule an event with certain users.
 */
public class SchedulingFrame extends JFrame implements ISchedulingFrame {

  private final IReadOnlyCentralSystem model;
  private JButton scheduleButton;

  private JTextArea eventName;
  private JTextArea location;
  private JComboBox<String> onlineStatus;
  private JTextArea durationTextArea;
  private JList<String> userList;

  /**
   * Creates a scheduling frame that can find an open duration for all invited users to attend
   * a planned event.
   */
  public SchedulingFrame(IReadOnlyCentralSystem model) {
    this.model = model;
    initializeUI();
  }

  private void initializeUI() {
    setTitle("Schedule Frame");

    JPanel panel = new JPanel();
    FrameSetupHelper.setUp(this, panel);
    // Event name field:
    eventName = new JTextArea(2, 20);
    // Event location field:
    location = new JTextArea(2, 20);
    // Online status
    onlineStatus = new JComboBox<>(new String[]{"is online", "offline"});
    FrameSetupHelper.commonFields(eventName, location, onlineStatus, panel);
    // Duration field:
    durationTextArea = new JTextArea(2, 20);
    durationTextArea.setBorder(BorderFactory.createTitledBorder("Duration in minutes:"));
    FrameSetupHelper.addPreferredTextBoxSize(durationTextArea, panel);

    // Multi-select list of users
    List<String> users = model.findAllUsers();
    users.remove(model.currentUser());
    DefaultListModel<String> userModel = new DefaultListModel<>();
    users.forEach(userModel::addElement);
    userList = new JList<>(userModel);
    FrameSetupHelper.invitedUsers(panel, userList);
    // Add buttons at the bottom
    createButtons(panel);
    // Add the panel to the frame
    getContentPane().add(panel);
  }

  private void createButtons(JPanel panel) {
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    scheduleButton = new JButton("Schedule Event");
    buttonPanel.add(scheduleButton);

    // Add buttons to the panel
    panel.add(buttonPanel);
  }


  private boolean validEntries() {
    return !eventName.getText().trim().isEmpty()
        && !location.getText().trim().isEmpty()
          && !durationTextArea.getText().trim().isEmpty()
            && isValidDuration(durationTextArea.getText().trim());
  }

  private boolean isValidDuration(String input) {
    try {
      int x = Integer.parseInt(input);
      return x > 0;
    } catch (NumberFormatException ex) {
      return false;
    }
  }


  private String eventInfo() {
    StringBuilder info = new StringBuilder();
    // Extract from text boxes
    appendInfo(info, "Name: " + eventName.getText());
    appendInfo(info, "Location: " + location.getText());
    // Extract online info and date and time info
    appendInfo(info, "Online status: " + onlineStatus.getSelectedItem());
    appendInfo(info, "Duration in minutes: " + durationTextArea.getText());
    extractUsers(info);
    return info.toString();
  }


  private void extractUsers(StringBuilder info) {
    appendInfo(info, "Host: " + model.currentUser());
    List<String> selectedUsers = userList.getSelectedValuesList();
    selectedUsers.remove(model.currentUser());
    appendInfo(info, "Invited users: " + selectedUsers);
  }


  private void appendInfo(StringBuilder info, String message) {
    info.append(message).append("\n");
  }

  @Override
  public void addFeatures(Features feature) {
    scheduleButton.addActionListener(e -> {
      if (validEntries()) {
        List<String> eventInfo = List.of(eventName.getText(), location.getText(),
                onlineStatus.getItemAt(onlineStatus.getSelectedIndex()));
        int duration = Integer.parseInt(durationTextArea.getText().trim());
        feature.scheduleEvent(duration, eventInfo, userList.getSelectedValuesList());
        dispose();
      } else {
        errorMessage("Invalid fields");
      }
    });
  }

  @Override
  public void errorMessage(String message) {
    JOptionPane.showMessageDialog(this, message);
  }
}
