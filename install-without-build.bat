call git clone  https://github.com/ruggeromontesi/exam-validator.git
cd  exam-validator
call java -jar exam-validator-2.0.jar
cd target/generated-reports
start notepad aggregated-report.txt
start threshold-based-report.txt
start notepad detailed-report.txt

cmd /k