package com.exam.validator.entity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
   public void testGetByName(){
      for(int i= 1; i <63; i++){
         String name = "Studentas"+i;
         Assert.assertEquals(name,getByName(name).get().getName() );
      }
   }

   @Test
   public void testGetNeighboursList(){
      String studentName = "Studentas12";
      List<Student> neighbours = Arrays.asList(
            getByName("Studentas3").get(),
            getByName("Studentas4").get(),
            getByName("Studentas5").get(),
            getByName("Studentas11").get(),
            getByName("Studentas13").get()
      );
      Student studentA = getByName(studentName).get();

      Assert.assertEquals(neighbours.size(),
            neighbours.stream().filter(student -> exam.getNeighboursList(studentA).contains(student) ).count());

   }

   @Test
   public void testIsThisStudentCheating() {
      Exam exam = new Exam();
      exam.getStudentList().forEach( student -> {
         System.out.println("*************************************************");
         if(exam.isThisStudentCheating(student)) {
            System.out.println("The student " + student.getName() + " is cheating");
         } else {
            System.out.println("The student " + student.getName() + " is not cheating");
         }
         System.out.println("*************************************************");
      });

   }

   @Test
   public void testDetermineCheatingStudentList(){

      exam.getThresholdBasedCheatingStudentList().forEach(System.out::println);
   }

   @Test
   public void testGenerateReports() {
      System.out.println(exam.getDetailedReport());
      exam.getDetailedReport().forEach((k, v) -> System.out.println(k + "\t\t" + v + "\n\n"));

   }

   @Test
   public void testPrintAggregatedReports () throws IOException{
      exam.getAggregatedReport().forEach((k, v) -> System.out.println(k + "  ---->  has " + v.getPercentageOfIdenticalAnswersWithSuspectedNeighbour() + "% of identical answers "
              + "with student with name " + v.getFromWhomThisStudentCopied()+ "\n"));

   }

   private Optional<Student> getByName(String name) {
      List<Student> studentList = exam.getStudentList();

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

}
