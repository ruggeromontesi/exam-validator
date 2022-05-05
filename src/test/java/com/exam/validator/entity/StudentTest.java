package com.exam.validator.entity;

import java.util.List;
import java.util.stream.Collectors;

import com.exam.validator.Main;
import com.exam.validator.util.CSVReader;
import org.junit.Test;

public class StudentTest {

   @Test
   public void testGetPossibleCoordinatesOfNeighbours() {
      Main ref = new Main();
      List<Student> studentList = CSVReader.parse();
      //CSVReader.parse().forEach(System.out::println);
      studentList.stream().filter(student -> student.getName().equals("Studentas4")).collect(Collectors.toList()).stream().map(Student::getPossibleCoordinatesOfNeighbours).forEach(System.out::println);

   }





}
