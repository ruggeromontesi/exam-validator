package com.exam.validator.constants;

public interface Constants {

   int AUDITORIUM_NUMBER_OF_COLUMNS = 8;

   int CHEATING_THRESHOLD = 5;

   String REPORTS_DIRECTORY = "target/generated-reports/";

   String DETAILED_REPORT_FILENAME = "detailed-report.txt";

   String AGGREGATED_REPORT_FILENAME = "aggregated-report.txt";

   String THRESHOLD_BASED_REPORT_FILENAME = "threshold-based-report.txt";

   String DETAILED_REPORT_HEADER = "STUDENT DETAILED REPORT\n"
         + "This file provides a detailed report for each student. A single record is shown on each line.\n"
         + "Each line consists of the following:\n"
         +  "1) Student name.\n"
         +  "2) A map where are listed the names of neighboring students and the amount of identical answers with them.\n"
         +  " \n\n";

   String AGGREGATED_REPORT_HEADER = "STUDENT AGGREGATED REPORT\n"
         + "This file provides an aggregated report for each student. A single record is shown on each line.\n\n"
         + "Each line consists of the following:\n"
         + "1) Student name.\n"
         + "2) highest number of answers identical to neighbours, i.e.:  is first calculated the amount of \n"
         + "identical answers with all the neighbours, is then considered the highest value.\n"
         + "3) Name of the neighbouring student who has the highest number of similar answers with the considered student."
         + "\n\n\n";

   String THRESHOLD_BASED_REPORT_HEADER  = "THRESHOLD BASED CHEATING STUDENT LIST\n"
         + "This file provides a list of cheating students."
         +  "A student is defined as cheating student when the amount of his answers that are identical\n"
         +  "to the answers of one of his neighbours is higher than a pre-set value.\n";

}
