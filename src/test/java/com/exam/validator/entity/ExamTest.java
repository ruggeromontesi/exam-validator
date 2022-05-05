package com.exam.validator.entity;

import org.junit.Assert;
import org.junit.Test;

public class ExamTest {

   private Exam exam = new Exam();

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

      Exam exam = new Exam();

      String studentName = "Studentas4";
      Student studentA = exam.getByName(studentName).get();
      exam.getNeighboursList(studentA).forEach(System.out::println);

      System.out.println("*************************************************");
      System.out.println("*************************************************");
      System.out.println("*************************************************");

      exam.getStudentList().forEach(student -> {
         System.out.println("*************************************************");
         System.out.println("printing neighbours for student " + student.getName());
         exam.getNeighboursList(student).forEach(student1 -> System.out.println(student1.getName()));
         System.out.println("*************************************************");

      });

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
      Exam exam = new Exam();
      exam.getCheatingStudentList().forEach(System.out::println);
   }

   @Test
   public void testGenerateReports() {
      Exam exam = new Exam();
      System.out.println(exam.getReports());
      exam.getReports().forEach((k,v) -> System.out.println(k + "\t\t" + v + "\n\n"));

   }

   @Test
   public void testPrintAggregateReport(){
      Exam exam = new Exam();
      exam.printAggregateReport();

   }

   @Test
   public void testGenerateAggregatedReport() {
      Exam exam = new Exam();
      exam.generateAggregatedReport();

   }

}
