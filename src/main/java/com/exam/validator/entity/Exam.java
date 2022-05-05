package com.exam.validator.entity;

import static com.exam.validator.constants.Constants.AUDITORIUM_NUMBER_OF_COLUMNS;
import static com.exam.validator.constants.Constants.CHEATING_THRESHOLD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.exam.validator.util.CSVReader;

public class Exam {
   private final List<Student> studentList = CSVReader.parse();

   private final List<Student> cheatingStudentList = determineCheatingStudentList();

   private final Map<String, Map<String,Integer>> reports = createReports();

   public List<Student> getStudentList() {
      return studentList;
   }

   public List<Student> getCheatingStudentList() {
      return cheatingStudentList;
   }

   public Map<String, Map<String, Integer>> getReports() {
      return reports;
   }

   Optional<Student> getByName(String name) {
      return studentList.stream().filter(
            student -> student.getName().equals(name)
      ).collect(
            Collectors.collectingAndThen(
                  Collectors.toList(),
                  list -> {
                     if (list.size() > 1) {
                        throw new RuntimeException("More than a student with with same name!");
                     }
                     return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
                  }
            )
      );
   }

   Optional<Student> getByCoordinate(String coordinate) {
      return studentList.stream().filter(
            student -> student.getSittingLocation().equals(coordinate)
      ).collect(
            Collectors.collectingAndThen(
                  Collectors.toList(),
                  list -> {
                     if (list.size() > 1) {
                        throw new RuntimeException("More than a student with with same name!");
                     }
                     return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
                  }
            ));
   }

   public List<String> getCoordinatesOfNeighbouringSittingLocations(Student thisStudent) {
      //TODO
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
            .stream()
            .filter(
            student -> student
                        .getAnswers()
                        .entrySet()
                        .stream()
                  .filter(e -> e.getValue().equals(thisStudent.getAnswers().get(e.getKey()))).count() > CHEATING_THRESHOLD
      ).count() > 0;

   }

   public List<Student> determineCheatingStudentList(){
      return studentList.stream().filter(this::isThisStudentCheating).collect(Collectors.toList());
   }

   private Map<String, Map<String,Integer>> createReports() {
      Comparator<String> c = (s1, s2) -> {
         int id1    =  Integer.parseInt(s1.substring(s1.indexOf('s') + 1));
         int id2    =  Integer.parseInt(s2.substring(s2.indexOf('s') + 1));
         return id1 - id2;
      };
      Map<String, Map<String,Integer>> outputReports = new TreeMap<>(c);
      studentList.forEach(
           student -> {
              List<Student> neighboursList = getNeighboursList(student);
              Map<String,Integer> report = new TreeMap<>(c);

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
      return outputReports;
   }

   public void printAggregateReport(){
      Map<String,Double> aggregateReport = new TreeMap<>();
      reports.forEach(
            (k,v) -> {
               double maxAmountOfIdenticalAnswers =
                     v.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getValue();
               double ratio = 100*(maxAmountOfIdenticalAnswers/16);
               aggregateReport.put(k,ratio);
            }
      );

      Map<String,Double> sortedAggregateReport =
            aggregateReport.entrySet().stream()
                  .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                  .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

      sortedAggregateReport.forEach(
            (k,v) -> {

               String otherStudentName =
                     reports.get(k).entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
               System.out.println(k + "  ---->  has " + v +"% of identical answers with student with name " + otherStudentName);
            }
      );
   }

}
