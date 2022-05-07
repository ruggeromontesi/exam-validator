package com.exam.validator.entity;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ComparatorTest {

    private Exam exam;

    @Before
    public void setUp() {
        exam = new Exam();
    }

    @Test
    public void testComparator(){
        Student student1 = getByName("Studentas1").get();
        Student student6 = getByName("Studentas6").get();
        Map<Student, Map<Student,Integer>> newOutputReports = new TreeMap<>(exam.getStudentComparator());
        newOutputReports.put(student1,exam.getAnswersComparisonForThisStudent(student1) );
        newOutputReports.put(student6,exam.getAnswersComparisonForThisStudent(student6) );
        /**********/
        //newOutputReports.entrySet().stream().forEach(e -> System.out.println(e.getKey().getName()));
        newOutputReports.clear();
        exam.getStudentList().stream().forEach(student -> newOutputReports.put(student, exam.getAnswersComparisonForThisStudent(student)));
        newOutputReports.entrySet().stream().forEach(e -> System.out.println(e.getKey().getName()));




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

    @Test
    public void testGetSingleStudentReport(){
        Student student1 = getByName("Studentas1").get();
        //exam.getAnswersComparisonForThisStudent(student1).entrySet().forEach(e -> System.out.print( "  {" + e.getKey().getName() + "    " + e.getValue() + "}") );
        exam.getStudentList().stream().forEach(student -> {
            System.out.print("Comparison of identical answers for " +student.getName());
            exam.getAnswersComparisonForThisStudent(student).entrySet().forEach(e -> System.out.print( "  {" + e.getKey().getName() + "---" + e.getValue() + "}\t") );
            System.out.println();
        });


    }
}
