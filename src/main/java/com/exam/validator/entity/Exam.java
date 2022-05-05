package com.exam.validator.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.exam.validator.util.CSVReader;

import static com.exam.validator.constants.Constants.*;

public class Exam {
   private List<Student> studentList = CSVReader.parse();

   private List<Student> cheatingStudentList = determineCheatingStudentList();

   public List<Student> getStudentList() {
      return studentList;
   }

   public List<Student> getCheatingStudentList() {
      return cheatingStudentList;
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

    List<Student> getNeighboursList(Student thisStudent) {
      List<String> possibleCoordinateOfNeighbours = thisStudent.getPossibleCoordinatesOfNeighbours();
      return possibleCoordinateOfNeighbours.stream().map(this::getByCoordinate).filter(Optional::isPresent).map(Optional::get).collect(
            Collectors.toList());
   }

   public boolean isThisStudentCheating(Student thisStudent) {
      List<Student> neighboursList = getNeighboursList(thisStudent);
      return neighboursList.stream().filter(
            student -> student.getAnswers().entrySet().stream().filter(
                  e -> e.getValue().equals(thisStudent.getAnswers().get(e.getKey()))).count() > CHEATING_THRESHOLD
      ).count() > 0;

   }

   public List<Student> determineCheatingStudentList(){
      return studentList.stream().filter(this::isThisStudentCheating).collect(Collectors.toList());
   }


}
