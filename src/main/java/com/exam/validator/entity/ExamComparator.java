package com.exam.validator.entity;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.ToIntBiFunction;

public class ExamComparator {

    public static ToIntBiFunction<Student,Exam> toIntStudentExamFunction = (student, exam) ->{
        Map<Student,Integer> singleStudentReport = new HashMap<>();

        exam.getNeighboursList(student).forEach(
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




    };


    //Comparator<Student> studentComparator = Comparator.comparingInt(toIntStudentExamFunction).reversed().thenComparing(Student::getName);
}
