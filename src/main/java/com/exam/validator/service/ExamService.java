package com.exam.validator.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.exam.validator.constants.Constants;
import com.exam.validator.entity.Exam;
import com.exam.validator.entity.Student;

public class ExamService {

   private final Exam exam;

   /**Accepts
    * @param args To be passed to Exam constructor.
    * */

   public ExamService(String... args) {
      this.exam = new Exam(args);
      File directory = new File(Constants.REPORTS_DIRECTORY);
      if (!directory.exists()) {
         directory.mkdirs();
      }
   }

   /**
    * Prints aggregated report, for each student is printed the highest amount of answers identical to answers of his neighbour
    * and the name of such neighbour.
    * @throws IOException if path is not found
    */
   public void printAggregatedReport() throws IOException {
      Map<Student, Map<Student,Integer>> report = new TreeMap<>(exam.getStudentComparator());
      exam.getStudentList().forEach(student -> report.put(student, exam.getAnswersComparisonForThisStudent(student)));

      BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.REPORTS_DIRECTORY + "/"
            + Constants.AGGREGATED_REPORT_FILENAME));
      writer.write(Constants.AGGREGATED_REPORT_HEADER);
      StringBuilder reportLineSb = new StringBuilder("");
      exam.getStudentList().forEach(student -> report.put(student, exam.getAnswersComparisonForThisStudent(student)));
      report.forEach((key, value) -> {
         int maximumNumberOfIdenticalQuestions = exam.getAnswersComparisonForThisStudent(key)
               .entrySet()
               .stream()
               .max(Comparator.comparingInt(Map.Entry::getValue)).get().getValue();
         List<Map.Entry<Student, Integer>> maxEntryList = exam.getAnswersComparisonForThisStudent(key)
               .entrySet()
               .stream()
               .filter(studentIntegerEntry -> studentIntegerEntry.getValue() == maximumNumberOfIdenticalQuestions)
               .collect(Collectors.toList());
         reportLineSb.append(String.format("%-11.11s %5s", key.getName(), " has ")).append(maximumNumberOfIdenticalQuestions)
               .append(" answers identical to ")
               .append(maxEntryList.stream().map(studentIntegerEntry -> studentIntegerEntry.getKey().getName()).collect(
                     Collectors.joining(", ")
               )).append(". \n");
      });
      writer.write(reportLineSb.toString());
      writer.close();
   }

   /**
    * Prints, for each student, list of neighbours and the amount of identical questions.
    * @throws IOException if path is not found
    */
   public void printDetailedReport() throws IOException {
      BufferedWriter writer =
            new BufferedWriter(new FileWriter(Constants.REPORTS_DIRECTORY + "/" + Constants.DETAILED_REPORT_FILENAME));
      writer.write(Constants.DETAILED_REPORT_HEADER);
      StringBuilder reportLineSb = new StringBuilder("");
      exam.getStudentList().forEach(student -> {
         reportLineSb.append("Comparison of identical answers for ").append(String.format("%-11.11s", student.getName()));
         exam.getAnswersComparisonForThisStudent(student).forEach((key, value) -> {
            reportLineSb.append("  {").append(key.getName()).append(" : ").append(value).append("}\t");
         });
         reportLineSb.append("\n");
      });
      writer.write(reportLineSb.toString());
      writer.close();
   }

   /**
    * Print all the three reports.
    */
   public  void printReports()  {
      try {
         printThresholdBasedCheatingStudentList();
         printDetailedReport();
         printAggregatedReport();
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   /**
    * Prints the list of cheating students, i.e. the list of students with an amount of answer identical to the answers of
    * neighbour higher thana predefined value.
    * @throws IOException if path is not found
    */
   public void printThresholdBasedCheatingStudentList() throws IOException {
      BufferedWriter writer =
            new BufferedWriter(new FileWriter(Constants.REPORTS_DIRECTORY + "/" + Constants.THRESHOLD_BASED_REPORT_FILENAME));
      writer.write(Constants.THRESHOLD_BASED_REPORT_HEADER);
      writer.write("The predefined threshold is equal to " + exam.getCheatingThreshold() + "\n\n\n");
      StringBuilder reportLineSb = new StringBuilder("");
      exam.getStudentList().stream().filter(exam::isThisStudentCheating).collect(Collectors.toList()).forEach(student -> {
         reportLineSb.append(student.toString()).append("\n");
      });
      writer.write(reportLineSb.toString());
      writer.close();
   }

}
