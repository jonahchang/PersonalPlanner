package cs3500.view;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

/**
 * Helper class for setting up frames and parsing information in frames.
 */
public class FrameSetupHelper {

  protected static void setUp(JFrame frame, JPanel panel) {
    frame.setSize(400, 800);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);
  }

  protected static void commonFields(JTextArea eventName, JTextArea location,
                              JComboBox<String> onlineStatus, JPanel panel) {
    // Event name field:
    eventName.setBorder(BorderFactory.createTitledBorder("Event Name:"));
    addPreferredTextBoxSize(eventName, panel);

    // Event location field:
    location.setBorder(BorderFactory.createTitledBorder("Location:"));
    addPreferredTextBoxSize(location, panel);

    // Online status
    onlineStatus.setBorder(BorderFactory.createTitledBorder("Online Status:"));
    panel.add(onlineStatus);
  }

  protected static void addPreferredTextBoxSize(JTextArea textBox, JPanel panel) {
    JScrollPane resized = new JScrollPane(textBox);
    resized.setPreferredSize(new Dimension(200, 80));
    panel.add(resized);
  }

  protected static JList<String> invitedUsers(JPanel panel, JList<String> userList) {
    userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    userList.setLayoutOrientation(JList.VERTICAL);
    userList.setVisibleRowCount(-1);

    JScrollPane scroller = new JScrollPane(userList);
    scroller.setPreferredSize(new Dimension(250, 150));
    scroller.setBorder(BorderFactory.createTitledBorder("Available Users:"));
    panel.add(scroller);

    return userList;
  }
}
