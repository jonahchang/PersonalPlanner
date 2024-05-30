package cs3500.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cs3500.model.DayOfWeek;
import cs3500.model.ICentralSystem;
import cs3500.model.ScheduleWriteException;
import cs3500.model.User;

/**
 * Helper class for loading and saving schedules.
 */
public class FileOperations {

  /**
   * Writes a given schedule to an XML file at the specified file path.
   * This method overwrites any existing content, use FileWriter to append to an existing file.
   *
   * @param userId   the user who has events to be added
   * @param filePath the file path where the XML file will be written. If the file already
   *                 exists, it will be overwritten
   * @throws ScheduleWriteException if the program was unable to write an XML file for the user
   */
  public static void saveSchedule(ICentralSystem system, String userId, String filePath)
          throws ScheduleWriteException {
    // Holds all the information for all events related to the user
    system.setUser(userId);
    Map<List<String>, List<String>> eventInfo = system.userSchedule(false);
    try (Writer file = new FileWriter(filePath)) {
      // Write the XML version
      tagWrite(file, "?xml version=\"1.0\"?", 0, true);
      // Write the schedule ID
      tagWrite(file, "schedule id=\"" + userId + "\"", 0, true);
      // Iterate through the events
      for (Map.Entry<List<String>, List<String>> event : eventInfo.entrySet()) {
        List<String> info = event.getKey();
        // Write the event information to the file
        tagWrite(file, "event", 1, true);
        // Write the name of the event
        fileWrite(file, "name", info.get(0), 2);
        // Write the time information
        tagWrite(file, "time", 2, true);
        timeWrite(file, info);
        tagWrite(file, "time", 2, false);

        // Write the location and online status
        tagWrite(file, "location", 2, true);
        fileWrite(file, "online", info.get(6), 3);
        fileWrite(file, "place", info.get(5), 3);
        tagWrite(file, "location", 2, false);

        // Write the host and invited users
        tagWrite(file, "users", 2, true);
        fileWrite(file, "uid", info.get(7), 3); // Host
        for (String invitee : event.getValue()) { // Iterate through the invitees list
          fileWrite(file, "uid", invitee, 3);
        }
        tagWrite(file, "users", 2, false);

        tagWrite(file, "event", 1, false);
      }
      tagWrite(file, "schedule", 0, false);
    } catch (IOException ex) {
      // ScheduleWriteException is a custom exception created for clarity when getting an error
      throw new ScheduleWriteException("Failed to write schedule to XML file: " + filePath, ex);
    }
  }

  /**
   * Helper method to consolidate writing XML lines with the tag and information on the same line.
   *
   * @param file        the file being written to
   * @param tag         the tag that surrounds the information
   * @param info        the information
   * @param indentation the amount of indentation (used for readability)
   * @throws IOException if there is an error with writing to the file
   */
  private static void fileWrite(Writer file, String tag,
                                String info, int indentation) throws IOException {
    // Amount of indentation
    String indent = addIndentation(indentation);
    // Writes information with the beginning and end tag on the same line
    file.write(indent + "<" + tag + ">" + info + "</" + tag + ">\n");
  }

  /**
   * Helper method for writing just the tag without any information following it.
   *
   * @param file        the file being written to
   * @param tag         the tag being added
   * @param indentation the amount of indentation
   * @param startTag    boolean variable for tracking whether it is a start/opening tag or an
   *                    end/closing tag
   * @throws IOException if there is an error with writing to the file
   */
  private static void tagWrite(Writer file, String tag,
                               int indentation, boolean startTag) throws IOException {
    // Amount of indentation
    String indent = addIndentation(indentation);
    // Writes the tag based on whether it is a start or end tag
    if (startTag) {
      file.write(indent + "<" + tag + ">\n");
    } else {
      file.write(indent + "</" + tag + ">\n");
    }
  }

  /**
   * Helper method for writing all the time and date-related information of an event.
   *
   * @param file     the file being written to
   * @param timeInfo a list of String containing the time and date information
   * @throws IOException if there is an error writing to the file
   */
  private static void timeWrite(Writer file, List<String> timeInfo) throws IOException {
    // Writes the start and end times to the file
    int timeIndent = 3;
    fileWrite(file, "start-day", timeInfo.get(1), timeIndent);
    fileWrite(file, "start", timeInfo.get(2), timeIndent);
    fileWrite(file, "end-day", timeInfo.get(3), timeIndent);
    fileWrite(file, "end", timeInfo.get(4), timeIndent);
  }

  /**
   * Helper method for setting how much indentation a line should have.
   *
   * @param indentation the level of indentation
   * @return the level of indentation times 4 spaces per level
   */
  private static String addIndentation(int indentation) {
    // Sets the indentation amount to 4 spaces per indentation level
    // Yes, unnecessary, but helps with readability
    return " ".repeat(indentation * 4);
  }

  /**
   * Reads a schedule from an XML file located at the specified file path and constructs a
   * User object from it if the user is not already in the system. Each Event's name, time,
   * location, and invited users are extracted and used to create or add to a User's Schedule.
   *
   * @param filePath the file path of the XML file to be read
   * @param system   the planner model being used
   * @throws IllegalStateException if there is an error in reading or parsing the file
   */
  public static void uploadSchedule(ICentralSystem system, String filePath)
          throws IllegalStateException {
    String userId; // Initialize variable to hold the user ID
    User user; // Initialize the variable for the user involved
    try {
      // Initializes process to parse XML file
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(new File(filePath));
      doc.getDocumentElement().normalize();
      // Get the user ID
      userId = doc.getDocumentElement().getAttribute("id");
      user = system.findUserById(userId); // Try finding the user in the system
      if (user == null) {
        user = new User(userId); // If user does not exist, make a new one
        system.addUser(user);
      }
      system.setUser(user.userId());
      // At this point, userId should have the value from the XML.
      NodeList nodeList = doc.getElementsByTagName("event");
      // Parse the document for specific tags under the list of events
      addEvent(nodeList, user, system);
    } catch (ParserConfigurationException ex) {
      throw new IllegalStateException("Error in creating the builder");
    } catch (IOException ioEx) {
      throw new IllegalStateException("Error in opening the file");
    } catch (SAXException saxEx) {
      throw new IllegalStateException("Error in parsing the file");
    }
  }

  /**
   * Helper method for parsing XML files and adding events to schedules.
   *
   * @param nodeList the list of events found in the XML file
   * @param user     the user being added to
   * @param system   the model used to store the user and events
   */
  private static void addEvent(NodeList nodeList, User user, ICentralSystem system)
          throws IllegalStateException {
    for (int temp = 0; temp < nodeList.getLength(); temp++) { // Iterate through the list of events
      Node node = nodeList.item(temp); // Get the event
      try {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element) node;
          // Collect the name from the XML
          String name = parseAttribute(element, "name");
          // Collect the location from the XML
          Element locationElement = (Element) element.getElementsByTagName("location").item(0);
          String location = parseAttribute(locationElement, "place");
          // Collect whether the event is online or not
          String online = parseAttribute(element, "online").toLowerCase();
          if (!online.equals("true") && !online.equals("false")) {
            throw new IllegalArgumentException("Invalid online status");
          }
          boolean isOnline = Boolean.parseBoolean(online);

          // Collect the start and end times
          List<LocalTime> times = parseTime(element);
          LocalTime startTime = times.get(0);
          LocalTime endTime = times.get(1);

          // Collect the day(s) event starts and ends
          String startDayString = parseAttribute(element, "start-day").toUpperCase();
          DayOfWeek startDay = DayOfWeek.valueOf(startDayString);
          String endDayString = parseAttribute(element, "end-day").toUpperCase();
          DayOfWeek endDay = DayOfWeek.valueOf(endDayString);

          // Collect the invited users
          List<User> invitedUsers = parseUsers(element, system);
          User host;
          if (invitedUsers.get(0).equals(user)) {
            host = user;
          } else {
            host = invitedUsers.get(0); // Store the host
          }
          invitedUsers.remove(0); // Remove the host from the invited users list
          try {
            system.createEvent(name, location, isOnline, startTime, endTime,
                    startDay, endDay, host, invitedUsers); // Create the event from the data
          } catch (IllegalStateException | IllegalArgumentException ex) {
            throw new IllegalStateException("Could not create event: " + ex.getMessage());
          }
        }
      } catch (NullPointerException ex) {
        throw new IllegalStateException("The file does not contain the proper tag(s)");
      }
    }
  }

  /**
   * Helper method for parsing the start and end times of an event.
   *
   * @param element the node/event being evaluated
   * @return a list of the start and end times of an event as LocalTime objects
   */
  private static List<LocalTime> parseTime(Element element) throws DateTimeParseException {
    List<LocalTime> times = new ArrayList<>();
    try {
      // Collect the "start" and "end" tags that contain the start and end times
      String start = element.getElementsByTagName("start").item(0).getTextContent().trim();
      String end = element.getElementsByTagName("end").item(0).getTextContent().trim();
      times.add(LocalTime.parse(start, DateTimeFormatter.ofPattern("HHmm")));
      times.add(LocalTime.parse(end, DateTimeFormatter.ofPattern("HHmm")));
      return times;
    } catch (DateTimeParseException ex) {
      throw new IllegalStateException("Time(s) are not in the correct format");
    }
  }

  /**
   * Helper method for parsing a certain tag.
   *
   * @param element   the event/node being parsed
   * @param attribute the tag name as a String
   * @return the String associated with the tag
   */
  private static String parseAttribute(Element element, String attribute)
          throws IllegalStateException {
    try {
      // Get the String associated with the tag
      return element.getElementsByTagName(attribute).item(0).getTextContent().trim();
    } catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
      throw new IllegalStateException(attribute + " tag does not exist or it has no elements");
    }
  }

  /**
   * Helper method for parsing the users list of an event.
   *
   * @param element the event/node being parsed
   * @param system  the system/model being used
   * @return a list of all users invited to the event, starting with the host
   */
  private static List<User> parseUsers(Element element, ICentralSystem system)
          throws IllegalStateException {
    List<User> invitedUsers = new ArrayList<>();
    NodeList userNodes = element.getElementsByTagName("uid");
    User user;
    try {
      for (int index = 0; index < userNodes.getLength(); index++) {
        // Iterate through all the users under the "uid" tag
        String userId = userNodes.item(index).getTextContent().trim();
        user = system.findUserById(userId); // Set the user to that ID
        if (user == null) { // If the user is not already in the system
          user = new User(userId); // Create a new user with the ID
          system.addUser(user); // Add the user to the system
        }
        invitedUsers.add(user); // Add the user to the invited users list
      }
      return invitedUsers;
    } catch (NullPointerException ex) {
      throw new IllegalStateException("Users tag does not exist");
    }
  }
}
