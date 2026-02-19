# User documentation 

# How to run

To run the program you have to be in root directory of git repository. This can be achieved by running this commands: 
* using git: `git clone https://github.com/JakubOliver/HospitalSystem.git && cd HospitalSystem`
* from zip: `unzip HospitalSystem.zip && cd HospitalSystem`

Program can be run in terminal by using following command: `mvn compile exec:java`.

For the best working program and user experience it is advisable to use **Java 25** and also is required to have installed **Maven** (to run the start command).

Note: It is also possible to run program directly inside IntelliJ, but it is not advised, because the IntelliJ terminal works differently than terminal in general and does not provide same options, for example running shell commands etc.

# Controls 

Whole program is controlled by user via terminal user interface (TUI). User is always prompted with the specific options and required actions, that help him to seamlessly control hospital system. 

## Main menu 

First thing that the user sees when runs the program is main menu, where can by inputting valid option open submenus with more specific options.

![Main menu of hospital system.](src/mainMenu.png)

## Submenus menu 

By selecting first option can user open submenus such as menu for patients, doctors or appointments. In these menus is possible to add, edit, delete or view all entities (patients, doctors, appointments) based on the specific menu.

For example in the picture bellow you can example how to add new patient into hospital system. So the user is prompted to **Select an option** then inputs valid option **1**, after selecting option starts process of inputting data about new patient that will be added to the database, so the user inputs first name and last name of patient, date of birth and the issue (anamnesis) which troubles the patient.  

<img alt="Adds patient into system via patient menu." src="src/addPatient.png" width="420"/>

Last submenu is dedicated for exports of hospital system data. In this menu the user can create exports for the whole hospital system or can choose what data (patients, doctors or appointments) will be exported. 

After selecting the option will be created (if not already exists) directory exports in the root directory of the program and in this directory you can find files with exports in CSV format.

* `patients.csv` is export of patients
* `doctors.csv` is export of doctors
* `appointments.csv` is export of appointments

<img alt="img_1.png" src="src/exportMenu.png" width="420"/>

## How to exit

Every submenu can be exited (going back to main menu) by selecting options **Back**. And the program can be ended in the main menu by selecting option **End**. 