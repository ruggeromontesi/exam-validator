package com.exam.validator.entity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
         Assert.assertEquals(name,exam.getByName(name).get().getName() );
      }
   }

   @Test
   public void testGetNeighboursList(){
      String studentName = "Studentas12";
      List<Student> neighbours = Arrays.asList(
            exam.getByName("Studentas3").get(),
            exam.getByName("Studentas4").get(),
            exam.getByName("Studentas5").get(),
            exam.getByName("Studentas11").get(),
            exam.getByName("Studentas13").get()
      );
      Student studentA = exam.getByName(studentName).get();

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

      exam.getCheatingStudentList().forEach(System.out::println);
   }

   @Test
   public void testGenerateReports() {
      exam.generateReports();
      System.out.println(exam.getReports());
      exam.getReports().forEach((k,v) -> System.out.println(k + "\t\t" + v + "\n\n"));

   }

   @Test
   public void testPrintAggregatedReports () throws IOException{
      exam.generateReports();
      exam.generateAggregatedReport();

      exam.getAggregatedReports().forEach((k,v) -> System.out.println(k + "  ---->  has " + v.getPercentageOfIdenticalAnswersWithSuspectedNeighbour() + "% of identical answers "
              + "with student with name " + v.getFromWhomThisStudentCopied()+ "\n"));

   }


   @Test
   public void testGenerateAggregatedReport() {
      Exam exam = new Exam();
      try {
         exam.generateReports();
         exam.generateAggregatedReport();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

}
