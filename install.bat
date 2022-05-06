call git clone  https://github.com/ruggeromontesi/exam-validator.git
cd  exam-validator
call mvn clean install -f pom.xml
call mvn compile exec:java -Dexec.mainClass="com.exam.validator.Main"
cd target/generated-reports
start notepad aggregated-report.txt
start notepad detailed-report.txt
start threshold-based-report.txt

cmd /k
