package com.exam.validator.entity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import com.exam.validator.service.ExamService;

public class ExamServiceTest {

   private ExamService examService;

   @Before
   public void setUp() {
      examService = new ExamService();
   }

   @Test
   public void testCreateStudentList(){
      Assert.assertFalse(examService.getStudentList().isEmpty());
   }

   @Test
   public void testGetByCoordinate(){
      String coordinateStudent29 = "4.5";
      Assert.assertEquals("Studentas29", examService.getByCoordinate(coordinateStudent29).get().getName());
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

      Student student12 = examService.getStudentList().get(11);
      examService.getCoordinatesOfNeighbouringSittingLocations(student12).forEach(
              coordinate ->
              Assert.assertTrue(coordinatesList.contains(coordinate))
      );
      Assert.assertEquals(5,examService.getCoordinatesOfNeighbouringSittingLocations(student12).size());

   }

   @Test
   public void testGetNeighboursList(){
      List<Student> studentList = examService.getStudentList();
      List<Student> neighbours = Arrays.asList(
              studentList.get(3),
              studentList.get(4),
              studentList.get(5),
              studentList.get(11),
              studentList.get(13)
      );

      Student student13 = studentList.get(12);

      Assert.assertEquals(neighbours.size(),
            neighbours.stream().filter(student -> examService.getNeighboursList(student13).contains(student) ).count());
   }

   @Test
   public void testIsThisStudentCheating() {
      Student student12 = examService.getStudentList().get(11);
      Assert.assertTrue(examService.isThisStudentCheating(student12));
   }

   @Test
   public void testGetAnswersComparisonForThisStudent(){
      Map<Student,Integer> mapForStudent1 = new HashMap<>();
      mapForStudent1.put(examService.getStudentList().get(1),6);
      Assert.assertEquals(mapForStudent1, examService.getAnswersComparisonForThisStudent(examService.getStudentList().get(0)));
   }

   @Test
   public void testComparator(){
      List<Student> studentList = examService.getStudentList();
      Student student1 = studentList.get(0);
      Student student6 = studentList.get(5);
      Map<Student, Map<Student,Integer>> newOutputReports = new TreeMap<>(examService.getStudentComparator());
      newOutputReports.put(student1,examService.getAnswersComparisonForThisStudent(student1) );
      newOutputReports.put(student6,examService.getAnswersComparisonForThisStudent(student6) );
      //student 6 has highest number of identical answers then should come first in the map.
      newOutputReports.entrySet().stream().limit(1).forEach(e -> Assert.assertEquals("Studentas6", e.getKey().getName()));
   }

}
