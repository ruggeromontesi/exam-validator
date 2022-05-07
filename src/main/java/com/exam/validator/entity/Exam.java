package com.exam.validator.entity;

import com.exam.validator.util.CSVReader;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static com.exam.validator.constants.Constants.AUDITORIUM_NUMBER_OF_COLUMNS;
import static com.exam.validator.constants.Constants.CHEATING_THRESHOLD;

public class Exam {
   private int cheatingThreshold;
   private final List<Student> studentList = CSVReader.parse();
   private List<Student> thresholdBasedCheatingStudentList;
   private Map<String, Map<String,Integer>> detailedReport;
   private Map<Student, Map<Student,Integer>> newDetailedReport;
   private  Map<String,Detail> aggregatedReport;


    private  ToIntFunction<Student> studentToIntFunction = student ->{
        Map<Student,Integer> singleStudentReport = new HashMap<>();

        getNeighboursList(student).forEach(
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


    } ;

   private Comparator<Student> studentComparator = Comparator.comparingInt(studentToIntFunction).reversed().thenComparing(Student::getName);

    public ToIntFunction<Student> getStudentToIntFunction() {
        return studentToIntFunction;
    }

    public Comparator<Student> getStudentComparator() {
        return studentComparator;
    }

    /**
     * The constructor accepts a vararg parameter of type String.
     * If no String is passed, or if it cannot be parsed to extract an integer , or if the String can be parsed but the
     * integer is either lower than 0 or higher than 16, a predefined threshold is used.
     * @param args A String containing an integer number representing the threshold for determining if the student
     * has cheated. If the students has a number of answers identical to a neighbour he is considered to have cheated.
     */
   public Exam(String... args) {
       if(args.length == 0) {
           cheatingThreshold = CHEATING_THRESHOLD;
       } else {
           try {
               int userThreshold = Integer.parseInt(args[0]);
               cheatingThreshold = (userThreshold > 0 && userThreshold < 17) ? userThreshold : CHEATING_THRESHOLD;
           } catch(NumberFormatException ex) {
               cheatingThreshold = CHEATING_THRESHOLD;
           }
       }
       generateDetailedReports();
       generateAggregatedReport();
       generateCheatingStudentList();
   }

   public List<Student> getStudentList() {
      return studentList;
   }

   public List<Student> getThresholdBasedCheatingStudentList() {
      return thresholdBasedCheatingStudentList;
   }

   public Map<String, Map<String, Integer>> getDetailedReport() {
      return detailedReport;
   }

    public Map<String, Detail> getAggregatedReport() {
        return aggregatedReport;
    }

    public int getCheatingThreshold() {
        return cheatingThreshold;
    }

    /**
     * Given a coordinate this method determines the Student sitting at this specific coordinate and wraps it within
     * an Optional.
     * If no student with the given position is present an empty optional is returned.
     * @param coordinate the coordinate from which is required to get the relevant Student instance.
     * @return the Student with sitting location equal to the given coordinate.
     */
   Optional<Student> getByCoordinate(String coordinate) {
      return studentList.stream().filter(
            student -> student.getSittingLocation().equals(coordinate)
      ).collect(
            Collectors.collectingAndThen(
                  Collectors.toList(),
                  list -> {
                     if (list.size() > 1) {
                        throw new RuntimeException("More than a student with with same sitting location!");
                     }
                     return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
                  }
            ));
   }

    /**
     * This method determines, given a Student, the list of coordinates of neighbouring positions, i.e.: the two
     * neighbouring positions in the same row and the three positions in front of the considered student.
     * @param thisStudent The students for which is required to determine the neighbouring positions.
     * @return the list of neighbouring positions.
     */
   public List<String> getCoordinatesOfNeighbouringSittingLocations(Student thisStudent) {
      int rowIndex = Integer.parseInt(thisStudent.getSittingLocation().substring(0,1));
      int colIndex = Integer.parseInt(thisStudent.getSittingLocation().substring(2,3));
      List<String> possibleCoordinateOfNeighbours = new ArrayList<>();
      //EAST neighbour
      if(colIndex > 1) {
         possibleCoordinateOfNeighbours.add(rowIndex + "." + (colIndex - 1));
      }
      //WEST neighbour
      if (colIndex < AUDITORIUM_NUMBER_OF_COLUMNS) {
         possibleCoordinateOfNeighbours.add(rowIndex + "." + (colIndex+1));
      }
      //SOUTH-WEST neighbour
      if(rowIndex > 1 && colIndex > 1) {
         possibleCoordinateOfNeighbours.add((rowIndex - 1) + "." + (colIndex - 1));
      }
      //SOUTH neighbour
      if(rowIndex > 1){
         possibleCoordinateOfNeighbours.add((rowIndex - 1) + "." + colIndex);
      }
      //SOUTH-EAST neighbour
      if(rowIndex > 1 && colIndex < AUDITORIUM_NUMBER_OF_COLUMNS) {
         possibleCoordinateOfNeighbours.add((rowIndex - 1) + "." + (colIndex + 1));
      }
      return possibleCoordinateOfNeighbours;
   }

    /**
     * This method, given a student, determines the list of other students from which he could have copied during
     * the exam. In this list are only included the two neighbouring students in the same row and the three students
     * in front of the considered student.
     * The method accounts for situation where no students are seated in the candidate positions and for the border
     * positions where less than five neighbouring students are present.
     * @param thisStudent the student for which is determined the list of neighbours.
     * @return the list of neighbours.
     */
    List<Student> getNeighboursList(Student thisStudent) {
      List<String> possibleCoordinateOfNeighbours = getCoordinatesOfNeighbouringSittingLocations(thisStudent);
      return possibleCoordinateOfNeighbours
            .stream()
            .map(this::getByCoordinate)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
   }

    /**
     * This method determines if the student has cheated based on the amount of answers identical to his neighbours.
     * The method, given a student, determines his neighbours and check for each of them the amount of identical answers.
     * If the count is higher than a predefined threshold the student is assumed to have cheated during the exam.
     * @param thisStudent the student for which is required to determine if he has cheated or not.
     * @return if this student has cheated or not.
     */
   public boolean isThisStudentCheating(Student thisStudent) {
      List<Student> neighboursList = getNeighboursList(thisStudent);
      return neighboursList
            .stream().anyMatch(student -> student
                  .getAnswers()
                  .entrySet()
                  .stream()
                  .filter(e -> e.getValue().equals(thisStudent.getAnswers().get(e.getKey()))).count() > cheatingThreshold);
   }

    /**
     * Given the list of students this method gathers in a list all students assumed to have cheated as determined
     * in the method  {@link #isThisStudentCheating(Student thisStudent) isThisStudentCheating}
     */
   public void generateCheatingStudentList(){
       thresholdBasedCheatingStudentList = studentList.stream().filter(this::isThisStudentCheating).collect(Collectors.toList());
   }

    /**
     * This method iterates over the list of student and, for each of them, creates a Map where keys are the names of
     * neighbouring students and values are the numbers of identical answers.
     * The map is sorted according the integer at the end of the String representing student name as per the given input.
     */
   public void generateDetailedReports()  {
       Comparator<String> stringComparator = Comparator.comparingInt( s -> Integer.parseInt(s.substring(s.indexOf('s') + 1)));
       /*Note: if more flexibility is required the commented comparator
        If more flexibility is required is also given, commented, implementation of a comparator that first attempt
        * to sort students in the same way as in the current implementation and then, if this approach is not succesful
        * simply implements lexicographical comparator.

       Comparator<String> stringComparator = (s1, s2) -> {
           try {
               int id1 = Integer.parseInt(s1.substring(s1.indexOf('s') + 1));
               int id2 = Integer.parseInt(s2.substring(s1.indexOf('s') + 1));
               return id1 - id2;
           } catch (NumberFormatException ex) {
               return s1.compareTo(s2);
           }
       };*/
      Map<String, Map<String,Integer>> outputReports = new TreeMap<>(stringComparator);
      studentList.forEach(
           student -> {
              List<Student> neighboursList = getNeighboursList(student);
              Map<String,Integer> report = new TreeMap<>(stringComparator);

              neighboursList.forEach(
                    neighbourStudent -> {
                       int amountOfIdenticalAnswers = (int) neighbourStudent
                             .getAnswers()
                             .entrySet()
                             .stream()
                             .filter(e -> e.getValue().equals(student.getAnswers().get(e.getKey()))
                       ).count();
                       report.put(neighbourStudent.getName(),amountOfIdenticalAnswers);
                    }
              );
              outputReports.put(student.getName(),report);
           }
      );
      detailedReport = outputReports;
   }

   public void printNewDetailedReports(){
       ToIntFunction<Student> toInt = student ->{
           Map<Student,Integer> singleStudentReport = new HashMap<>();

           getNeighboursList(student).forEach(
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


       } ;

       Comparator<Student> studentComparator = Comparator.comparingInt(toInt).thenComparing(Student::getName);
       Map<Student, Map<Student,Integer>> newOutputReports = new TreeMap<>(studentComparator);
       studentList.forEach(student -> {
           Map<Student,Integer> singleStudentReport = new HashMap<>();
           getNeighboursList(student).forEach(
                   neighbourStudent -> {
                       int amountOfIdenticalAnswers = (int) neighbourStudent
                               .getAnswers()
                               .entrySet()
                               .stream()
                               .filter(e -> e.getValue().equals(student.getAnswers().get(e.getKey()))).count();
                       singleStudentReport.put(neighbourStudent,amountOfIdenticalAnswers);
                   }
           );

           newOutputReports.put(student,singleStudentReport);

       });

      // newOutputReports.entrySet().stream().forEach(           e -> System.out.println(e.getKey().getName() +"  " + e.getValue()));

       int a = 2;

       for(Map.Entry<Student,Map<Student,Integer>> entry : newOutputReports.entrySet()) {
           System.out.print("\n [" + entry.getKey().getName());
           Map<Student,Integer> innerMap = entry.getValue();
           for(Map.Entry<Student,Integer> innerMapEntry : innerMap.entrySet()){
               System.out.print( "  {" + innerMapEntry.getKey().getName()  +"---"+ innerMapEntry.getValue() + "}");
           }
           System.out.print("]\n");
       }

   }

    /**
     * This method creates the aggregated report. Information used in the detailed report are retrieved to produce a new
     * Map where keys are student names and values are Detail entity, which contains information on the maximum amount
     * of answers identical to answers of neighbours.
     * The map is then rearranged and sorted by values, in decreasing order, according to the maximum amount of answers
     * identical to  answers of neighbours.
     */
   public void generateAggregatedReport() {
      Map<String,Detail> tempAggregatedReports = new HashMap<>();
      detailedReport.forEach(
            (k,v) -> {
               double maxAmountOfIdenticalAnswers =
                     v.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getValue();
               String fromWhomTheStudentCopied = v.entrySet()
                       .stream()
                       .max(Comparator.comparingInt(Map.Entry::getValue))
                       .get()
                       .getKey();
               int ratio = (int) ((100*maxAmountOfIdenticalAnswers)/16);
               tempAggregatedReports.put(k,new Detail(fromWhomTheStudentCopied,ratio));
            }
      );

      aggregatedReport =
            tempAggregatedReports.entrySet().stream()
                  .sorted(Collections.reverseOrder(
                          Comparator.comparingDouble(e -> e.getValue().percentageOfIdenticalAnswersWithSuspectedNeighbour)))
                  .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)
                  );
   }

   public class Detail {
      private final String fromWhomThisStudentCopied;
      private final int percentageOfIdenticalAnswersWithSuspectedNeighbour;

       public String getFromWhomThisStudentCopied() {
           return fromWhomThisStudentCopied;
       }

       public int getPercentageOfIdenticalAnswersWithSuspectedNeighbour() {
           return percentageOfIdenticalAnswersWithSuspectedNeighbour;
       }

       public Detail(String fromWhomThisStudentCopied, int percentageOfIdenticalAnswersWithSuspectedNeighbour) {
         this.fromWhomThisStudentCopied = fromWhomThisStudentCopied;
         this.percentageOfIdenticalAnswersWithSuspectedNeighbour = percentageOfIdenticalAnswersWithSuspectedNeighbour;
      }
   }


   public Map<Student,Integer> getAnswersComparisonForThisStudent(Student thisStudent) {
       Map<Student,Integer> singleStudentReport = new HashMap<>();
       getNeighboursList(thisStudent).forEach(
               neighbourStudent -> {
                   int amountOfIdenticalAnswers = (int) neighbourStudent
                           .getAnswers()
                           .entrySet()
                           .stream()
                           .filter(e -> e.getValue().equals(thisStudent.getAnswers().get(e.getKey()))).count();
                   singleStudentReport.put(neighbourStudent,amountOfIdenticalAnswers);
               }
       );
       return singleStudentReport;
   }
}