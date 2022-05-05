package com.exam.validator.entity;

import static com.exam.validator.constants.Constants.AUDITORIUM_NUMBER_OF_COLUMNS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.exam.validator.util.CSVReader;

public class Student {
   private String name;
   private String sittingLocation;
   private Map<Integer, String> answers = new HashMap<Integer, String>();
   private static final List<Student> studentList = CSVReader.parse();

   public String getName() {
      return name;
   }

   public Student setName(String name) {
      this.name = name;
      return this;
   }

   public String getSittingLocation() {
      return sittingLocation;
   }

   public Student setSittingLocation(String sittingLocation) {
      this.sittingLocation = sittingLocation;
      return this;
   }

   public Map<Integer, String> getAnswers() {
      return answers;
   }

   public Student setAnswers(Map<Integer, String> answers) {
      this.answers = answers;
      return this;
   }

   List<Student> getNeighbours() {
      //TODO
      List<String> possibleCoordinateOfNeighbours = getPossibleCoordinatesOfNeighbours();
      String coordinate1 = possibleCoordinateOfNeighbours.get(0);
      studentList.stream().filter(student -> student.getSittingLocation().equals(coordinate1)).collect(Collectors.toList());

      Function<String,List<Student>> getNeighbours =  coordinate -> studentList.stream().filter(student -> student.getSittingLocation().equals(coordinate)).collect(Collectors.toList());

            /*possibleCoordinateOfNeighbours.stream().map(
                  coordinate -> studentList.stream().filter(student -> student.getSittingLocation().equals(coordinate)).collect(
            Collectors.toList())
            );*/

      return null;
   }

   public boolean canCopyFrom(Student other) {
      List<String> possibleCoordinateOfNeighbours = getPossibleCoordinatesOfNeighbours();
      return possibleCoordinateOfNeighbours.stream().filter(
            coordinate -> studentList.stream()
                  .filter(student -> student.getSittingLocation().equals(coordinate))
                  .count() == 1
            ).count() == 1;



   }

   List<String> getPossibleCoordinatesOfNeighbours() {
      int rowIndex = Integer.parseInt(this.sittingLocation.substring(0,1));
      int colIndex = Integer.parseInt(this.sittingLocation.substring(2,3));
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



   @Override
   public String toString() {
      return new StringJoiner(", ", Student.class.getSimpleName() + "[", "]")
            .add("name='" + name + "'")
            .add("sittingLocation='" + sittingLocation + "'")
            .add("answers=" + answers)
            .toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      Student student = (Student) o;

      if (name != null ? !name.equals(student.name) : student.name != null) {
         return false;
      }
      if (sittingLocation != null ? !sittingLocation.equals(student.sittingLocation) : student.sittingLocation != null) {
         return false;
      }
      return answers != null ? answers.equals(student.answers) : student.answers == null;
   }

   @Override
   public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (sittingLocation != null ? sittingLocation.hashCode() : 0);
      result = 31 * result + (answers != null ? answers.hashCode() : 0);
      return result;
   }
}
