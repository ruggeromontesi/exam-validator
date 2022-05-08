GENERAL DESCRIPTION
The application attempts to determine which students cheated during the exam session.
The application provides two different approaches:
1)Threshold based approach
A threshold-based approach, which identifies cheating students as those students who have a number 
of their questions identical to the answers of one of their neighbours bigger than a pre-defined 
amount.
The students considered "cheaters" are listed within file threshold-based-report.txt located within 
./target/generated-reports.
The report indicates the predefined amount to create the list of cheating students.
This amount can be set by the user, at the moment of launching application, through a command line
parameter.(See section installation and running for more details).

2)Sorting student according to similarities in the answers with one of their  neighbours.
The second approach is a softer one in that it analyzes and sort the students according to the 
amount of their questions identical to the answers of one of their neighbours.
This sorted list is available within file aggregated-report.txt.

In addition, a third report is generated, showing for each student the amount of identical answers
in comparison to each of the neighbours.
###################################################################################################

RUNNING AND INSTALLATION
Instruction to download, build and run  the application
1) crate a new folder, get in to console mode typing cmd on address bar
2) CLONE REPOSITORY: git clone https://github.com/ruggeromontesi/exam-validator.git
3) get into root folder: exam-validator
4) build :mvn clean install -f pom.xml
5) type the command mvn compile exec:java -Dexec.mainClass="com.exam.validator.Main"
By default the "cheating-threshold" is 5.
If the user wants to set a different threshold he/she can do so by adding to the maven command 
the parameter -Dexec.args="<threshold>"

mvn compile exec:java -Dexec.mainClass="com.exam.validator.Main"  -Dexec.args="11" 
Meaningful values for threshold range from 1 to 16, since 16 is the number of questions of the 
exam.
If the parameter is not a parseable integer or if it is an integer but not in the range above,
the application uses the default value 5.

6) Result file are located within /target/generated-reports


Instruction to download and run (WITHOUT BUILDING) the application.
1) crate a new folder, get in to console mode typing cmd on address bar
2) CLONE REPOSITORY: git clone https://github.com/ruggeromontesi/exam-validator.git
3) get into root folder: exam-validator
4) type java -jar exam-validator-2.0.jar


###################################################################################################

RUNNING AND INSTALLATION WIZARD(Windows environment)
Launch the script install.bat (Required maven and java 11 installed).
This will clone github repository, build, launch the application and show the three abovementioned 
reports.

###################################################################################################
NOTE:
The information relevant to correct answers was not taken into account.
Reasons for this choice is that this information appears not to be relevant to determine if the 
student cheated or not.
The information about correct answers is useful for grading purposes but not for the specific scope
of this task.

###################################################################################################
DEFINITION
Given a certain student, another student is considered neighbour if is sitting in the neighbouring
left and right seats in the same row or in the three seats in the front row in the central, left 
and right position.