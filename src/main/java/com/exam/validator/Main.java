package com.exam.validator;

import java.util.HashMap;
import java.util.Map;

import com.exam.validator.entity.Exam;
import com.exam.validator.util.ReportPrinter;

/**
 * Students were taking an exam which was the a multichoice test. We know the location in auditorium where each student
 * was sitting and their answers. * Student's sitting location is in format x.y, where x is the row and the y is the
 * sitting place in the row. Rows are numbered from front to back. * Each student had a chance to cheat and write
 * answers down from:
 * a) his neighbours in the same row,
 * b) the 3 guys sitting in the row in front of him
 * e.g. auditorium could look like that
 *     (.....back........)
 *     (x,x,x,x,x,x,x,x,x)
 *     (x,x,x,x,x,y,s,y,x)
 *     (x,x,x,x,x,y,y,y,x)
 *     (....front........), where s is the student, and y are his neighbours
 * The task is to identify cheating students in the class.
 * If you find that you are missing requirements, take your own judgment.
 * Data could be found in results.csv file, but CSV parsing is already done for you and results are mapped to a
 * Student objects list.
 */
public class Main {

   private Map<Integer, String> correctAnswers = new HashMap<>();

   {
      correctAnswers.put(1, "a");
      correctAnswers.put(2, "bd");
      correctAnswers.put(3, "abef");
      correctAnswers.put(4, "f");
      correctAnswers.put(5, "f");
      correctAnswers.put(6, "d");
      correctAnswers.put(7, "abe");
      correctAnswers.put(8, "abcde");
      correctAnswers.put(9, "abe");
      correctAnswers.put(10, "abd");
      correctAnswers.put(11, "b");
      correctAnswers.put(12, "af");
      correctAnswers.put(13, "ce");
      correctAnswers.put(14, "be");
      correctAnswers.put(15, "bdf");
      correctAnswers.put(16, "a");
   }

   public static void main(String[] args) {
      Exam exam = new Exam(args);
      ReportPrinter.printReports(exam);
   }
}


