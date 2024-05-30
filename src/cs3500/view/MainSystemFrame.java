package cs3500.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.Objects;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import cs3500.controller.Features;
import cs3500.model.IReadOnlyCentralSystem;
import cs3500.model.ScheduleWriteException;

/**
 * The frame for the overall main system.
 */
public class MainSystemFrame extends JFrame implements IMainSystemFrame {

  private final IReadOnlyCentralSystem model;
  private IMainPanel schedulePanel;
  private JMenuItem loadMenuItem;
  private JMenuItem saveMenuItem;

  /**
   * Creates a GUI view of the schedule with no user selected initially.
   */
  public MainSystemFrame(IReadOnlyCentralSystem model) {
    super("Central System");
    this.model = model;
    initialize();
  }

  private void initialize() {
    setTitle("NUPlanner Main System");
    setSize(800, 800);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setupMenuBar();
    setupSchedulePanel();
  }

  private void setupMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    loadMenuItem = new JMenuItem("Load Schedule");
    saveMenuItem = new JMenuItem("Save Schedule");

    fileMenu.add(loadMenuItem);
    fileMenu.add(saveMenuItem);
    menuBar.add(fileMenu);
    setJMenuBar(menuBar);
  }

  private File loadSchedule() {
    JFileChooser fileChooser = new JFileChooser(".");
    FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files",
            "xml");
    fileChooser.setFileFilter(filter);

    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      return selectedFile;
    }
    return null;
  }

  private File saveSchedule() throws IllegalArgumentException {
    JFileChooser dirChooser = new JFileChooser(".");
    dirChooser.setDialogTitle("Select a directory to save schedules");
    dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    dirChooser.setAcceptAllFileFilterUsed(false);

    int result = dirChooser.showSaveDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedDir = dirChooser.getSelectedFile();
      return selectedDir;
    }
    return null;
  }

  private void setupSchedulePanel() {
    schedulePanel = new SchedulePanel(model);
    getContentPane().add((Component) schedulePanel, BorderLayout.CENTER);
    ((Component) schedulePanel).setVisible(true);
  }

  @Override
  public void updateUsers() {
    schedulePanel.updateUsers();
    repaint();
  }

  @Override
  public void displayMainFrame() {
    setVisible(true);
  }

  @Override
  public void setPanel(IMainPanel panel) {
    if (panel instanceof Component) {
      getContentPane().remove((Component) this.schedulePanel);

      this.schedulePanel = panel;
      getContentPane().add((Component) panel, BorderLayout.CENTER);
      validate();
      repaint();
    } else {
      throw new IllegalArgumentException("Invalid panel provided");
    }
  }

  @Override
  public void addFeatures(Features features) {
    schedulePanel.addFeatures(features);
    loadMenuItem.addActionListener(e -> {
      try {
        File selectedFile = Objects.requireNonNull(loadSchedule());
        features.uploadSchedule(selectedFile.getAbsolutePath());
      } catch (IllegalStateException ex) {
        throw new IllegalStateException("Error: " + ex.getMessage());
      }
    });
    saveMenuItem.addActionListener(e -> {
      try {
        File selectedDir = Objects.requireNonNull(saveSchedule());
        features.saveSchedule(selectedDir);
      } catch (ScheduleWriteException | NullPointerException ex) {
        JOptionPane.showMessageDialog(this,
                "Could not save files to this directory");
      }
    });
  }

  @Override
  public void errorMessage(String message) {
    JOptionPane.showMessageDialog(this, message);
  }
}
