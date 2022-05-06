package com.exam.validator.constants;

public interface Constants {
   int AUDITORIUM_NUMBER_OF_ROWS = 8;
   int AUDITORIUM_NUMBER_OF_COLUMNS = 8;
   //A student is considered to be cheating when the number of answers identical to a neighbour from which he could have copied
   // is higher than this value
   int CHEATING_THRESHOLD = 5;

   String REPORTS_DIRECTORY = "target/generated-reports/";

   String DETAILED_REPORT_FILENAME = "detailed-report.txt";

   String DETAILED_REPORTS_HEADER  = "AGGREGATED REPORTS\n";

   String AGGREGATED_REPORTS_HEADER  = "AGGREGATED REPORTS\n" +
           "This file provides an aggregated report for each student. A singler ecord is shown on each line.\n\n" +
           "Each line consists of the following:\n" +
           "1) Student name.\n" +
           "2) highest percentage of answers identical to neighbours, i.e.:  is first calculated the percentage of \n" +
           "identical answers with all the neighbours, is then considered the highest percentage.\n"+
           "3)Name of the neighbouring student who has the highest percentage of similar answers with the considered student."+
           " \n\n\n";

}
