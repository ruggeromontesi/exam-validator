package com.exam.validator.entity;

import com.exam.validator.util.CSVReader;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.exam.validator.constants.Constants.AUDITORIUM_NUMBER_OF_COLUMNS;

public class Exam {
   private int cheatingThreshold = 8;
   private final List<Student> studentList = CSVReader.parse();
   private final List<Student> cheatingStudentList = determineCheatingStudentList();
   private Map<String, Map<String,Integer>> detailedReport;
   private  Map<String,Detail> aggregatedReport;

   public Exam(int cheatingThreshold) {
      this();
      this.cheatingThreshold = cheatingThreshold;
   }

   public Exam() {
       try {
           generateDetailedReports();
           generateAggregatedReport();
       } catch (IOException e) {
           e.printStackTrace();
       }
    }

   public List<Student> getStudentList() {
      return studentList;
   }

   public List<Student> getCheatingStudentList() {
      return cheatingStudentList;
   }

   public Map<String, Map<String, Integer>> getDetailedReport() {
      return detailedReport;
   }

    public Map<String, Detail> getAggregatedReport() {
        return aggregatedReport;
    }

   Optional<Student> getByCoordinate(String coordinate) {
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

   public List<String> getCoordinatesOfNeighbouringSittingLocations(Student thisStudent) {
      int rowIndex = Integer.parseInt(thisStudent.getSittingLocation().substring(0,1));
      int colIndex = Integer.parseInt(thisStudent.getSittingLocation().substring(2,3));
      List<String> possibleCoordinateOfNeighbours = new ArrayList<>();
      //EAST neighbour
      if(colIndex > 1) {
         possibleCoordinateOfNeighbours.add(rowIndex + "." + (colIndex - 1));
      }
      //WEST neighbour
      if (colIndex < AUDITORIUM_NUMBER_OF_COLUMNS) {
         possibleCoordinateOfNeighbours.add(rowIndex + "." + (colIndex+1));
      }
      //SOUTH-WEST neighbour
      if(rowIndex > 1 && colIndex > 1) {
         possibleCoordinateOfNeighbours.add((rowIndex - 1) + "." + (colIndex - 1));
      }
      //SOUTH neighbour
      if(rowIndex > 1){
         possibleCoordinateOfNeighbours.add((rowIndex - 1) + "." + colIndex);
      }
      //SOUTH-EAST neighbour
      if(rowIndex > 1 && colIndex < AUDITORIUM_NUMBER_OF_COLUMNS) {
         possibleCoordinateOfNeighbours.add((rowIndex - 1) + "." + (colIndex + 1));
      }
      return possibleCoordinateOfNeighbours;
   }

    List<Student> getNeighboursList(Student thisStudent) {
      List<String> possibleCoordinateOfNeighbours = getCoordinatesOfNeighbouringSittingLocations(thisStudent);
      return possibleCoordinateOfNeighbours
            .stream()
            .map(this::getByCoordinate)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
   }

   public boolean isThisStudentCheating(Student thisStudent) {
      List<Student> neighboursList = getNeighboursList(thisStudent);
      return neighboursList
            .stream().anyMatch(student -> student
                  .getAnswers()
                  .entrySet()
                  .stream()
                  .filter(e -> e.getValue().equals(thisStudent.getAnswers().get(e.getKey()))).count() > cheatingThreshold);
   }

   public List<Student> determineCheatingStudentList(){
      return studentList.stream().filter(this::isThisStudentCheating).collect(Collectors.toList());
   }

   private void generateDetailedReports() throws IOException {
       Comparator<String> stringComparator = Comparator.comparingInt(s -> Integer.parseInt(s.substring(s.indexOf('s') + 1)));

      Map<String, Map<String,Integer>> outputReports = new TreeMap<>(stringComparator);
      studentList.forEach(
           student -> {
              List<Student> neighboursList = getNeighboursList(student);
              Map<String,Integer> report = new TreeMap<>(stringComparator);

              neighboursList.forEach(
                    neighbourStudent -> {
                       int amountOfIdenticalAnswers = (int) neighbourStudent
                             .getAnswers()
                             .entrySet()
                             .stream()
                             .filter(e -> e.getValue().equals(student.getAnswers().get(e.getKey()))
                       ).count();
                       report.put(neighbourStudent.getName(),amountOfIdenticalAnswers);
                    }
              );
              outputReports.put(student.getName(),report);
           }
      );
      detailedReport = outputReports;
   }

   public void generateAggregatedReport() throws IOException{
      Map<String,Detail> tempAggregatedReports = new HashMap<>();
      detailedReport.forEach(
            (k,v) -> {
               double maxAmountOfIdenticalAnswers =
                     v.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getValue();
               String fromWhomTheStudentCopied = v.entrySet()
                       .stream()
                       .max(Comparator.comparingInt(Map.Entry::getValue))
                       .get()
                       .getKey();
               int ratio = (int) ((100*maxAmountOfIdenticalAnswers)/16);
               tempAggregatedReports.put(k,new Detail(fromWhomTheStudentCopied,ratio));
            }
      );

      aggregatedReport =
            tempAggregatedReports.entrySet().stream()
                  .sorted(Collections.reverseOrder(
                          Comparator.comparingDouble(e -> e.getValue().percentageOfIdenticalAnswersWithSuspectedNeighbour)))
                  .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)
                  );
   }

   public class Detail {
      private final String fromWhomThisStudentCopied;
      private final int percentageOfIdenticalAnswersWithSuspectedNeighbour;

       public String getFromWhomThisStudentCopied() {
           return fromWhomThisStudentCopied;
       }

       public int getPercentageOfIdenticalAnswersWithSuspectedNeighbour() {
           return percentageOfIdenticalAnswersWithSuspectedNeighbour;
       }

       public Detail(String fromWhomThisStudentCopied, int percentageOfIdenticalAnswersWithSuspectedNeighbour) {
         this.fromWhomThisStudentCopied = fromWhomThisStudentCopied;
         this.percentageOfIdenticalAnswersWithSuspectedNeighbour = percentageOfIdenticalAnswersWithSuspectedNeighbour;
      }
   }
}