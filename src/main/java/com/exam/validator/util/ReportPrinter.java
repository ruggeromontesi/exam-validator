package com.exam.validator.util;

import com.exam.validator.entity.Exam;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static com.exam.validator.constants.Constants.*;
import static com.exam.validator.constants.Constants.AGGREGATED_REPORTS_HEADER;

public class ReportPrinter {

    static {
        File directory = new File(REPORTS_DIRECTORY);
        if (!directory.exists()){
            directory.mkdirs();
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(REPORTS_DIRECTORY + "/" + DETAILED_REPORT_FILENAME));
            writer.write(DETAILED_REPORTS_HEADER);
            writer.close();
            writer = new BufferedWriter(new FileWriter(REPORTS_DIRECTORY + "/" + AGGREGATED_REPORT_FILENAME));
            writer.write(AGGREGATED_REPORTS_HEADER);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printDetailedReport(Exam exam) throws IOException {

        Map<String, Map<String,Integer>> detailedReports = exam.getDetailedReport();
        BufferedWriter writer = new BufferedWriter(new FileWriter(REPORTS_DIRECTORY + "/" + DETAILED_REPORT_FILENAME));
        writer.write(DETAILED_REPORTS_HEADER);
        detailedReports.forEach( (k,v) -> {
                    try {
                        writer.write(k + "\t\t" + v + "\n");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        writer.close();

    }

    public static void printAggregatedReport(Exam exam) throws IOException {
        Map<String, Exam.Detail> aggregatedReport = exam.getAggregatedReport();
        BufferedWriter writer = new BufferedWriter(new FileWriter(REPORTS_DIRECTORY + "/" + AGGREGATED_REPORT_FILENAME));
        writer.write(AGGREGATED_REPORTS_HEADER);
        aggregatedReport.forEach((k, v) -> {
            try {
                writer.write(k + "  ---->  has " + v.getPercentageOfIdenticalAnswersWithSuspectedNeighbour() + "% of identical answers "
                        + "with student with name " + v.getFromWhomThisStudentCopied() + "\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.close();
    }

}
