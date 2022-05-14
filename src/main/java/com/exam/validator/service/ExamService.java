package com.exam.validator.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import com.exam.validator.constants.Constants;
import com.exam.validator.entity.Student;
import com.exam.validator.util.CSVReader;

public class ExamService {

   private int cheatingThreshold;

   private final List<Student> studentList = CSVReader.parse();

   /**Accepts a vararg parameter of type String. If no or invalid String is passed a predefined threshold is used.
    * @param args A String containing an integer number representing the threshold for determining if the student has cheated.
    * If the students has a number of answers identical to a neighbour he is considered to have cheated.
    * */
   public ExamService(String... args) {
       File directory = new File(Constants.REPORTS_DIRECTORY);
      if (!directory.exists()) {
         directory.mkdirs();
      }

      if (args.length == 0) {
         cheatingThreshold = Constants.CHEATING_THRESHOLD;
      } else {
         try {
            int userThreshold = Integer.parseInt(args[0]);
            cheatingThreshold = (userThreshold > 0 && userThreshold < 17) ? userThreshold : Constants.CHEATING_THRESHOLD;
         } catch (NumberFormatException ex) {
            cheatingThreshold = Constants.CHEATING_THRESHOLD;
         }
      }
   }

   /**
    * Prints aggregated report, for each student is printed the highest amount of answers identical to answers of his neighbour
    * and the name of such neighbour.
    * @throws IOException if path is not found
    */
   public void printAggregatedReport() throws IOException {
      Map<Student, Map<Student,Integer>> report = new TreeMap<>(getStudentComparator());
      getStudentList().forEach(student -> report.put(student, getAnswersComparisonForThisStudent(student)));

      BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.REPORTS_DIRECTORY + "/"
            + Constants.AGGREGATED_REPORT_FILENAME));
      writer.write(Constants.AGGREGATED_REPORT_HEADER);
      StringBuilder reportLineSb = new StringBuilder("");
      getStudentList().forEach(student -> report.put(student, getAnswersComparisonForThisStudent(student)));
      report.forEach((key, value) -> {
         int maximumNumberOfIdenticalQuestions = getAnswersComparisonForThisStudent(key)
               .entrySet()
               .stream()
               .max(Comparator.comparingInt(Map.Entry::getValue)).get().getValue();
         List<Map.Entry<Student, Integer>> maxEntryList = getAnswersComparisonForThisStudent(key)
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
      getStudentList().forEach(student -> {
         reportLineSb.append("Comparison of identical answers for ").append(String.format("%-11.11s", student.getName()));
         getAnswersComparisonForThisStudent(student).forEach((key, value) -> {
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
      writer.write("The predefined threshold is equal to " + getCheatingThreshold() + "\n\n\n");
      StringBuilder reportLineSb = new StringBuilder("");
      getStudentList().stream().filter(this::isThisStudentCheating).collect(Collectors.toList()).forEach(student -> {
         reportLineSb.append(student.toString()).append("\n");
      });
      writer.write(reportLineSb.toString());
      writer.close();
   }

   private final ToIntFunction<Student> studentToIntFunction = student -> {
      Map<Student,Integer> singleStudentReport = new HashMap<>();
      getNeighboursList(student).forEach(
            neighbourStudent -> {
               int amountOfIdenticalAnswers = (int) neighbourStudent
                     .getAnswers()
                     .entrySet()
                     .stream()
                     .filter(e -> e.getValue().equals(student.getAnswers().get(e.getKey()))).count();
               singleStudentReport.put(neighbourStudent,amountOfIdenticalAnswers);
            }
      );
      return singleStudentReport.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getValue();
   };

   private final Comparator<Student> studentComparator = Comparator.comparingInt(studentToIntFunction)
         .reversed().thenComparing(Student::getName);

   public Comparator<Student> getStudentComparator() {
      return studentComparator;
   }

   public int getCheatingThreshold() {
      return cheatingThreshold;
   }

   public List<Student> getStudentList() {
      return studentList;
   }

   /**
    * Given a coordinate it retrieves the Student sitting at this coordinate and wraps it within an Optional.
    * @param coordinate the coordinate from which is required to get the relevant Student instance.
    * @return the Student with sitting location equal to the given coordinate.
    */
   public Optional<Student> getByCoordinate(String coordinate) {
      return studentList.stream().filter(
            student -> student.getSittingLocation().equals(coordinate)
      ).collect(
            Collectors.collectingAndThen(
                  Collectors.toList(),
                  list -> {
                     if (list.size() > 1) {
                        throw new RuntimeException("More than a student with with same sitting location!");
                     }
                     return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
                  }
            ));
   }

   /**
    * Determines the list of coordinates of neighbouring positions for the Student passed as parameter .
    * @param thisStudent The students for which is required to determine the neighbouring positions.
    * @return the list of neighbouring positions.
    */
   public List<String> getCoordinatesOfNeighbouringSittingLocations(Student thisStudent) {
      int rowIndex = Integer.parseInt(thisStudent.getSittingLocation().substring(0,1));
      int colIndex = Integer.parseInt(thisStudent.getSittingLocation().substring(2,3));
      List<String> possibleCoordinateOfNeighbours = new ArrayList<>();
      //EAST neighbour
      if (colIndex > 1) {
         possibleCoordinateOfNeighbours.add(rowIndex + "." + (colIndex - 1));
      }
      //WEST neighbour
      if (colIndex < Constants.AUDITORIUM_NUMBER_OF_COLUMNS) {
         possibleCoordinateOfNeighbours.add(rowIndex + "." + (colIndex + 1));
      }
      //SOUTH-WEST neighbour
      if (rowIndex > 1 && colIndex > 1) {
         possibleCoordinateOfNeighbours.add((rowIndex - 1) + "." + (colIndex - 1));
      }
      //SOUTH neighbour
      if (rowIndex > 1) {
         possibleCoordinateOfNeighbours.add((rowIndex - 1) + "." + colIndex);
      }
      //SOUTH-EAST neighbour
      if (rowIndex > 1 && colIndex < Constants.AUDITORIUM_NUMBER_OF_COLUMNS) {
         possibleCoordinateOfNeighbours.add((rowIndex - 1) + "." + (colIndex + 1));
      }
      return possibleCoordinateOfNeighbours;
   }

   /**
    * Determines the list of  students neighbouring to the student passed as parameter.
    * @param thisStudent the student for which is determined the list of neighbours.
    * @return the list of neighbours.
    */
   public List<Student> getNeighboursList(Student thisStudent) {
      List<String> possibleCoordinateOfNeighbours = getCoordinatesOfNeighbouringSittingLocations(thisStudent);
      return possibleCoordinateOfNeighbours
            .stream()
            .map(this::getByCoordinate)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
   }

   /**
    * Determines if the student has cheated based on the amount of answers identical to his neighbours.
    * @param thisStudent the student for which is required to determine if he has cheated or not.
    * @return if this student has cheated or not.
    */
   public boolean isThisStudentCheating(Student thisStudent) {
      List<Student> neighboursList = getNeighboursList(thisStudent);
      return neighboursList
            .stream().anyMatch(student -> student
                  .getAnswers()
                  .entrySet()
                  .stream()
                  .filter(e -> e.getValue().equals(thisStudent.getAnswers().get(e.getKey()))).count() > cheatingThreshold);
   }

   /**Creates a map where keys are the neighbouring students and values the amount fo identical answers.
    * @param thisStudent student for which the map is generated
    * @return map Student/ amount of identical values
    */
   public Map<Student,Integer> getAnswersComparisonForThisStudent(Student thisStudent) {
      Map<Student,Integer> singleStudentReport = new HashMap<>();
      getNeighboursList(thisStudent).forEach(
            neighbourStudent -> {
               int amountOfIdenticalAnswers = (int) neighbourStudent
                     .getAnswers()
                     .entrySet()
                     .stream()
                     .filter(e -> e.getValue().equals(thisStudent.getAnswers().get(e.getKey()))).count();
               singleStudentReport.put(neighbourStudent,amountOfIdenticalAnswers);
            }
      );
      return singleStudentReport;
   }

}
