package com.exam.validator.util;

import com.exam.validator.entity.Exam;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ReportPrinterTest {

    private Exam exam;

    @Before
    public void setUp() {
        exam = new Exam();
    }

    @Test
    public void testPrintDetailedReport() throws IOException {
        ReportPrinter.printDetailedReport(exam);
    }

    @Test
    public void testPrintAggregatedReport() throws IOException {
        ReportPrinter.printAggregatedReport(exam);
    }

    @Test
    public  void testPrintThresholdBasedCheatingStudentList() throws IOException {
        ReportPrinter.printThresholdBasedCheatingStudentList(exam);
    }
}
