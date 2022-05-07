package com.exam.validator.entity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ExamTest {

   private Exam exam;

   @Before
   public void setUp() {
      exam = new Exam();
   }

   @Test
   public void testCreateStudentList(){
      Assert.assertFalse(exam.getStudentList().isEmpty());
   }

   @Test
   public void testGetByCoordinate(){
      String coordinateStudent29 = "4.5";
      Assert.assertEquals("Studentas29", exam.getByCoordinate(coordinateStudent29).get().getName());
   }

   @Test
   public void testGetCoordinatesOfNeighbouringSittingLocations() {
      String sittingLocationStudent3 = "1.6";
      String sittingLocationStudent4 = "1.5";
      String sittingLocationStudent5 = "1.4";
      String sittingLocationStudent11 = "2.6";
      String sittingLocationStudent13 = "2.4";
      List<String> coordinatesList = Arrays.asList(
              sittingLocationStudent3,
              sittingLocationStudent4,
              sittingLocationStudent5,
              sittingLocationStudent11,
              sittingLocationStudent13
      );

      Student student12 = exam.getStudentList().get(11);


      exam.getCoordinatesOfNeighbouringSittingLocations(student12).forEach(
              coordinate ->
              Assert.assertTrue(coordinatesList.contains(coordinate))
      );
      Assert.assertEquals(5,exam.getCoordinatesOfNeighbouringSittingLocations(student12).size());

   }


   @Test
   public void testGetNeighboursList(){
      List<Student> studentList = exam.getStudentList();
      List<Student> neighbours = Arrays.asList(
              studentList.get(3),
              studentList.get(4),
              studentList.get(5),
              studentList.get(11),
              studentList.get(13)
      );

      Student student13 = studentList.get(12);

      Assert.assertEquals(neighbours.size(),
            neighbours.stream().filter(student -> exam.getNeighboursList(student13).contains(student) ).count());
   }

   @Test
   public void testIsThisStudentCheating() {
      Student student12 = exam.getStudentList().get(11);
      Assert.assertTrue(exam.isThisStudentCheating(student12));
   }

   @Test
   public void testGetAnswersComparisonForThisStudent(){
      Map<Student,Integer> mapForStudent1 = new HashMap<>();
      mapForStudent1.put(exam.getStudentList().get(1),6);
      Assert.assertEquals(mapForStudent1, exam.getAnswersComparisonForThisStudent(exam.getStudentList().get(0)));


   }



   public void testComparator(){

   }

}
