call git clone  https://github.com/ruggeromontesi/exam-validator.git
cd  exam-validator
call mvn clean install -f pom.xml
call mvn compile exec:java -Dexec.mainClass="com.exam.validator.Main"
REM mvn compile exec:java -Dexec.mainClass="com.exam.validator.Main"  -Dexec.args="8"
cd target/generated-reports
start notepad aggregated-report.txt
start threshold-based-report.txt
start notepad detailed-report.txt

cmd /k


REM if the user wants to pass a different threshold to determine the cheating students
REM he can do so running the command below and typing the desired value among double quotes.
REM this parameters should be between 1 and 16 to have a real meaning as the exam consists of 16 questions.
REM if the user specify a Sring not containing any int, or containing integer not meaningful 
REM the application simply ignores it and uses a predefined threshold of 5.
REM
REM
REM
REM mvn compile exec:java -Dexec.mainClass="com.exam.validator.Main"  -Dexec.args="8"
