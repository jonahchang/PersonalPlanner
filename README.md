CHANGES FOR PART 3

We implemented a Features interface from which our controller is implemented, with all the
behaviors that are expected of our GUI. This controller can upload and save XML files (save all
schedules in the system), create, modify, remove, and schedule events, create frames for creating,
modifying, removing, and scheduling events, and switch the view to the current user. This is done
through callbacks from the view using methods called "addFeatures", where the controller is passed
in as a parameter and is used for callbacks to the proper methods in response to certain actions 
such as clicking on certain buttons or events. The controller implementation takes in the view,
registers itself as the listener of view actions through the aforementioned method, and ensures that
the view stays up to date following an action such as creating, modifying, removing, scheduling 
events as well as loading schedules into the system.

Whenever the model throws an error due to any reason, the controller catches it and displays an
error message on the view instead of printing anything to the console.

Within the main method, we made calls to a factory class that would set the correct strategy using 
the arguments passed in.

For scheduling events, we took a brute-force approach where we just iterated through every minute 
of every hour of every day of the week and checked if there was an available time slot. For the 
workhours strategy, we limited the time to just monday through friday and only the hours of 0900
to 1700. To set the strategies pass in "anytime" for the anytime strategy, and "workhours" for the
workhours strategy.

For extra credit, we implemented resizable views by using the BoxLayout and BorderLayouts. (this was
already implemented in our previous assignments)

CHANGES FOR PART 2

ADDED VIEW CLASSES (ALL IN VIEW PACKAGE):

1. For the event frame, we added an interface IEventFrame to represent our
event frame. The event frame enables the user to create, modify, and remove events. 
For the concrete implementation 
of our event frame, we added a class EventFrame (extending JFrame and implementing
IEventFrame). The event frame is a box with:
- a textbox to enter the name of a new event
- a textbox to enter the location of a new event
- a drop-down menu to specify whether or not a new event is online
- a drop-down menu to specify the start day of a new event
- a JSpinner menu to specify the start time of a new event (from 00:00-23:59)
- a drop-down menu to specify the end day of a new event
- a JSpinner menu to specify the end time of a new event (from 00:00-23:59)
- a selection menu to select which users (out of all of the users in the central system)
are invited to a new event
- a button to create a new event, given the information entered in the boxes & menus above
- a button to modify an event
- a button to remove an event - removing an event will also print who the 
event is being removed from

In the event frame, an error message will print to the console if the client attempts to create,
modify, or remove an event with invalid fields, such as blank name and location fields. There is no
error when the client gives an invalid time, as the JSpinner component will revert the field back to
the last valid input if the client attempts to do so.

If the client clicks on an event on the schedule (colored red), then the event frame that opens up
will automatically have populated fields with all the existing event information.

2. For the schedule panel, we implemented an interface called ISchedulePanel. 
The concrete implementation is SchedulePanel.
The schedule panel provides the UI components for the event frame's buttons, dropdown menus,
and JSpinner menus.
- Along with the schedule panel, we added a custom class called DrawableEvent that is used to keep
track of the event information associated with a certain event on a specific user's schedule, as well
as whether the user is the host of the event or not, so that when the client clicks on the event,
the DrawableEvent class can be used to identify which event was clicked, as well as which users should
appear on the available users list.

3. For the main system frame, we added an interface IMainSystemFrame,
with the concrete implementation being MainSystemFrame, to represent our main
system frame, which represents the underlying framework for our GUI. It includes 
functionality to:
- setup the menu bar
- setup and display a schedule panel
- load a menu item
- save a menu item
- upload a schedule (print the file path)
- save all schedules in the system (print the destination directory/folder)

To start/run the program, the PlannerRunner main class/main method will instantiate a blank model
along with a GUI view. In order to accommodate for loading in no schedule or a list of valid schedules,
we implemented a second constructor in the NUPlanner class that takes in a List of Strings that specify
the file paths for all the schedules the client wishes to load in. To load in a file from the root directory
of the project, create a list of the strings of desired file paths or input "List.of(<filePath1>, <filePath2>)"
as the parameter of the constructor for the model.

OTHER CHANGES:

1. We refactored our model interface, ICentralSystem, into interfaces encompassing the 
read-only (IReadOnlyCentralSystem) and mutable (ICentralSystem) functionality
of our model. IReadOnlyCentralSystem only implements the observer methods; 
findUserById, findEventByName, findEventsByTime, userSchedule, and findAllUsers.
ICentralSystem implements IReadOnlyCentralSystem and adds the following
methods that mutate the model: setUser, addUser, createEvent, addEvent,
modifyEvent, updateEventInvited, and scheduleEvent. 

2. Refactored the methods for uploading/saving schedules into a static helper class in order
to be able to create a model/planner with a schedule already 
loaded in without the need of a controller.

3. Wrote the missing test (singular) from last assignment: checking if removing 
the host from an event will remove the event 
entirely from the system, as well as from all concerned parties' schedules.

4. Fixed the checkTime methods in the User and Event classes (they are linked) so that it properly
checks for any events occurring during a given time and day. (previously, it only checked if the given
time overlapped with any events' times; now it accounts for the given day as well).

5. Fixed the text view so that it doesn't print every single event in the database onto every user's schedule,
now only prints relevant events on the concerned parties' schedules.


ORIGINAL README FROM PART 1 (ASSIGNMENT 5)

Starting off, our central schedule system is implemented from the ICentralSystem interface as
the NUPlanner class/model. Our view is implemented from the PlannerView interface as the
NUPlannerTextView class. Our controller, though incomplete (as per the instructions), 
is implemented from the IPlannerControlller interface as the TextUI class.



The controller includes the methods saveSchedule and uploadSchedule.
saveSchedule writes the information in the given user’s schedule to the XML file
corresponding to filePath, and will overwrite the file if the filePath already exists in the system. 
uploadSchedule reads the schedule from the XML file
given by filePath and saves the information to the given ICentralSystem. From there, our model
can take the information parsed in the controller to form a User and their corresponding
schedule with all of their Events populating the schedule. If a user shows up in an uploaded file as
the host or invited user, the user will be automatically added to the system, and all events related
to that user will be added to their schedule as well.

The model, when instantiated, is set to use the "admin" role as default, which then needs to be
changed to the current user. The current user has power to access, modify, add, or remove
events they're involved in, but not events they're not involved in, because in an actual central
system, it wouldn't make sense for a user to see all events and modify them regardless of their
lack of involvement in the event.

To create an event, the model takes in all possible parameters
regarding necessary information about the event, and then attempts to create the event for the
selected user, throwing an error if the event is invalid or overlaps with an existing event on the
user's schedule.

To modify an event, the model takes in parameters to specify which aspect of an event is being changed,
then calls the method to check if that change can be made, specifically regarding time constraints.

To remove an event, the model takes in which event is to be removed, then removes it from the user's
schedule. If the user is the host of the event, then the entire event is removed from the database,
as well as from every invited user's schedule.

To schedule an event, the model takes in event information excluding the days of the event, then
attempts to find an open time on any day of the week when all invited users and the host are able
to attend the event. If there is no available time for all users, then the model throws an error.


Our Event class (implementing our
IEvent interface) serves as a gateway for the central system to access, add, & remove individual
events by granting access to its information. It works in conjunction with our user class 
(implementing our IUser interface) and serves as a gateway for the central system to modify users. 
Our user and event classes have fields that are private and inaccessible to the client, but do contain
methods that can observe needed fields or String representations of desired fields. An Event holds the
following information:
1. A unique name 
2. location
3. online status
4. start and end time
5. start and end day
6. host
7. invited users

Users are objects that hold schedules that hold Events. A user has the following information:
1. name/unique ID
2. One schedule of all Events that the User is involved in
The User class includes functionality to add, remove, and access Events.

Our planner spans at most one week at a time, so the maximum length of time an Event can last is
6 days, 23 hours, 59 minutes. The planner also prevents double-booking, while allowing events to be
scheduled to end at the same time another event starts.



The controller (which we haven’t fully implemented yet) is supposed to drive the control-flow
of our system by reading and writing to XML files. This gives information to the model,
represented by the ICentralSystem. Then, the central system formats that information and passes it
back to the controller so that the controller can communicate with the view and output a text
representation of the model. In essence, the controller drives the model,
which in turn drives the view.


The view is centered around a NUPlannerTextView class (implementing a PlannerView interface),
which includes functionality to print out a text-based view.


Source organization & Tests:

Within our source folder, we created a directory called cs3500,
and within that, included our controller, model, and view directories. 
Our source folder exists alongside a test folder, which contains
all of our tests. The UserEventTest class, in
our model package, tests the private methods of our
user and event classes. The CentralSystemTests and ControllerTests classes,
outside our model package, tests the public functionality of our central system
and controller, respectively.



Quick start:

public class CentralSystemExample {
User david = new User(“david”);
User teacherBaird = new User("teacher baird");
ICentralSystem centralSchedule = new NUPlanner();
centralSchedule.addUser(“david”);
centralSchedule.addUser(“teacher baird”);
List<User> pianoClassRoster = new ArrayList<>();
pianoClassRoster.add(david);
centralSchedule.createEvent.(name: “piano class”, location: “snell library”,
isOnline: false, startTime: LocalTime.of(10, 0),
endTime: LocalTime.of(12, 0), startDay: DayOfWeek.WEDNESDAY,
endDay: DayOfWeek.WEDNESDAY, host: teacherBaird,
invitedUsers: pianoClassRoster);
PlannerView sampleView = NUPlannerTextView(centralSchedule);
sampleView.textView();   /*
prints out david and teacher baird’s
schedules, each with only one event (the piano
class) on them.
*/                 
}