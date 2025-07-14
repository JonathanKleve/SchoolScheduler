# School Scheduler 
## Project Overview

The School Scheduler is an application designed to assist educational institutions in managing and organizing their academic schedules efficiently. It provides functionalities to manage courses, students, instructors, and their respective assignments, aiming to optimize class scheduling and resource allocation while minimizing conflicts.

This project demonstrates foundational software development concepts, including database management, user interface design, and logical organization for complex data.

## Features

* Term Management:
  * Add, view, update, and delete term details

* Course Management:

  * Add, view, update, and delete course details

* Assignment Management:

  * Add, view, update, and delete assignment details

* Instructor Management:

  * Add, view, update, and delete instructor details

* Alert Creation:
  * Create alerts for the start and end date of courses and assignments

* Data Persistence: All data (courses, students, instructors, schedules) is securely stored and retrieved from a relational database.


## Technologies Used

* Programming Language: Java 17

* Database: SQLite (integrated within Android)

* Development Environment: Android Studio Meercat

* Version Control: Git

## Getting Started
Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites
Before you begin, ensure you have the following installed:

* Git: For cloning the repository.

  * [Download Git](https://git-scm.com/downloads)

* Java Development Kit (JDK): 17

  * [Download JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.java.net/install/)

* Android Studio: For opening and running the Android application.

  * [Download Android Studio](https://developer.android.com/studio/install)

* Installation
1. Clone the repository:

``` 
git clone https://github.com/JonathanKleve/SchoolScheduler.git
 
cd SchoolScheduler
```

2. Open and Build the Project in Android Studio:

  * Open Android Studio.

  * Select File > Open and navigate to the SchoolScheduler directory.

  * Android Studio should automatically detect the project and synchronize Gradle. Allow it to download any necessary dependencies.

  * Build the project: Build > Make Project.

## Usage

* To run the Android application:
  * From Android Studio, select your desired emulator or connected device from the dropdown menu.
  * Click the green "Run" button (looks like a play icon).
* After launching the application, you will be taken to the main menu.
  * To return to the main menu at any time, tap the home icon in the top left of the application. 
* To create a new term, course, assignment, or instructor, select the appropriate option from the drop down menu under the create new label and then tap the go button.
  * Keep in mind that a course will require a term and instructor in the database in order to create and an assignment will require a course in the database in order to create.
* In order to view all of the terms, courses, assignments, or instructors currently saved, select the corresponding option from the drop down menu under the view all label and then tap the go button.
* In order to view detailed information on a specific term, course, assignment, or instructor, proceed to the previously explained View All Menu and then tap on the specific item from within that list.
  * From this menu, you can edit or delete the item, see associated courses for terms, create alerts for courses and assignments, and share notes for courses.


## Project Documentation
This project is accompanied by some documentation.

* Project Reflection: This document addresses a few key aspects of the application, the development process, issues encountered, and how the developer would approach the project again with different perspectives.

  * [Link to Project Reflection](docs/School%20Scheduler%20Reflection.pdf)

* UI/UX Diagrams:

  * [Link to Storyboard](docs/School%20Scheduler%20Storyboard.png)

* APK Generation Documentation: Show the process of creating the signed APK file via Android Studio for proof as required by the assignment.

  * [Link to Screenshot 1](docs/APK%20Screenshot%201.png)
  * [Link to Screenshot 2](docs/APK%20Screenshot%202.png)
  * [Link to Screenshot 3](docs/APK%20Screenshot%203.png)
  * [Link to Screenshot 4](docs/APK%20Screenshot%204.png)

## Contributing

As a personal portfolio project, direct contributions are not actively sought. However, if you find any issues or have suggestions, feel free to open an issue on this repository.

## License

This project is licensed under the MIT License - see the ```LICENSE``` file for details.

## Contact

Jonathan Kleve

Email: 194426067+JonathanKleve@users.noreply.github.com
