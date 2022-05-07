package com.exam.validator.entity;

import com.exam.validator.util.CSVReader;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static com.exam.validator.constants.Constants.AUDITORIUM_NUMBER_OF_COLUMNS;
import static com.exam.validator.constants.Constants.CHEATING_THRESHOLD;

public class Exam {
   private int cheatingThreshold;
   private final List<Student> studentList = CSVReader.parse();
    private final ToIntFunction<Student> studentToIntFunction = student ->{
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
    } ;

    private final Comparator<Student> studentComparator = Comparator.comparingInt(studentToIntFunction).reversed().thenComparing(Student::getName);

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
     * Accepts a vararg parameter of type String. If no or invalid String is passed a predefined threshold is used.
     * @param args A String containing an integer number representing the threshold for determining if the student
     * has cheated. If the students has a number of answers identical to a neighbour he is considered to have cheated.
     */
   public Exam(String... args) {
       if(args.length == 0) {
           cheatingThreshold = CHEATING_THRESHOLD;
       } else {
           try {
               int userThreshold = Integer.parseInt(args[0]);
               cheatingThreshold = (userThreshold > 0 && userThreshold < 17) ? userThreshold : CHEATING_THRESHOLD;
           } catch(NumberFormatException ex) {
               cheatingThreshold = CHEATING_THRESHOLD;
           }
       }
   }

    /**
     * Given a coordinate it retrieves the Student sitting at this coordinate and wraps it within an Optional.
     * @param coordinate the coordinate from which is required to get the relevant Student instance.
     * @return the Student with sitting location equal to the given coordinate.
     */
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

    /**
     * Determines the list of  students neighbouring to the student passed as parameter.
     * @param thisStudent the student for which is determined the list of neighbours.
     * @return the list of neighbours.
     */
    List<Student> getNeighboursList(Student thisStudent) {
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