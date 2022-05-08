package com.exam.validator.util;

import com.exam.validator.entity.Exam;
import com.exam.validator.entity.Student;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.exam.validator.constants.Constants.*;

public class ReportPrinter {
    static {
        File directory = new File(REPORTS_DIRECTORY);
        if (!directory.exists()){
            directory.mkdirs();
        }
    }

    public static void printAggregatedReport(Exam exam) throws IOException {
        Map<Student, Map<Student,Integer>> report = new TreeMap<>(exam.getStudentComparator());
        exam.getStudentList().forEach(student -> report.put(student, exam.getAnswersComparisonForThisStudent(student)));

        BufferedWriter writer = new BufferedWriter(new FileWriter(REPORTS_DIRECTORY + "/" + AGGREGATED_REPORT_FILENAME));
        writer.write(AGGREGATED_REPORT_HEADER);
        StringBuilder reportLineSb = new StringBuilder("");
        exam.getStudentList().forEach(student -> report.put(student, exam.getAnswersComparisonForThisStudent(student)));
        report.forEach((key, value) -> {
            int maximumNumberOfIdenticalQuestions = exam.getAnswersComparisonForThisStudent(key)
                    .entrySet()
                    .stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue)).get().getValue();
            List<Map.Entry<Student, Integer>> maxEntryList = exam.getAnswersComparisonForThisStudent(key)
                    .entrySet()
                    .stream()
                    .filter(studentIntegerEntry -> studentIntegerEntry.getValue() == maximumNumberOfIdenticalQuestions)
                    .collect(Collectors.toList());
            reportLineSb.append(String.format("%-11.11s %5s",key.getName(), " has " ) + maximumNumberOfIdenticalQuestions + " answers identical to "
                    + maxEntryList.stream().map(studentIntegerEntry -> studentIntegerEntry.getKey().getName()).collect(
                    Collectors.joining(", ")
            ) + ". \n");
        });
        writer.write(reportLineSb.toString() );
        writer.close();
    }

    public static void printDetailedReport(Exam exam) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(REPORTS_DIRECTORY + "/" + DETAILED_REPORT_FILENAME));
        writer.write(DETAILED_REPORT_HEADER);
        StringBuilder reportLineSb = new StringBuilder("");
        exam.getStudentList().forEach(student -> {
            reportLineSb.append("Comparison of identical answers for " + String.format("%-11.11s", student.getName()));
            exam.getAnswersComparisonForThisStudent(student).forEach((key, value) -> {
                reportLineSb.append("  {" + key.getName() + " : " + value + "}\t");
            });
            reportLineSb.append("\n");
        });
        writer.write(reportLineSb.toString());
        writer.close();
    }


    public static void printReports(Exam exam)  {
        try {
            printThresholdBasedCheatingStudentList(exam);
            printDetailedReport(exam);
            printAggregatedReport(exam);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void printThresholdBasedCheatingStudentList(Exam exam) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(REPORTS_DIRECTORY + "/" + THRESHOLD_BASED_REPORT_FILENAME));
        writer.write(THRESHOLD_BASED_REPORT_HEADER);
        writer.write("The predefined threshold is equal to " + exam.getCheatingThreshold() + "\n\n\n");
        StringBuilder reportLineSb = new StringBuilder("");
        exam.getStudentList().stream().filter(exam::isThisStudentCheating).collect(Collectors.toList()).forEach(student -> {
            reportLineSb.append(student.toString() + "\n");
        });
        writer.write(reportLineSb.toString());
        writer.close();
     }
}
