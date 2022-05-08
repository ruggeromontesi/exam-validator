package com.exam.validator.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class Student {
   private String name;

   private String sittingLocation;

   private Map<Integer, String> answers = new HashMap<Integer, String>();

   public String getName() {
      return name;
   }

   public Student setName(String name) {
      this.name = name;
      return this;
   }

   public String getSittingLocation() {
      return sittingLocation;
   }

   public Student setSittingLocation(String sittingLocation) {
      this.sittingLocation = sittingLocation;
      return this;
   }

   public Map<Integer, String> getAnswers() {
      return answers;
   }

   public Student setAnswers(Map<Integer, String> answers) {
      this.answers = answers;
      return this;
   }

   @Override
   public String toString() {
      return new StringJoiner(", ", Student.class.getSimpleName() + "[", "]")
            .add("name='" + name + "'")
            .add("sittingLocation='" + sittingLocation + "'")
            .add("answers=" + answers)
            .toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      Student student = (Student) o;

      if (name != null ? !name.equals(student.name) : student.name != null) {
         return false;
      }
      if (sittingLocation != null ? !sittingLocation.equals(student.sittingLocation) : student.sittingLocation != null) {
         return false;
      }
      return answers != null ? answers.equals(student.answers) : student.answers == null;
   }

   @Override
   public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (sittingLocation != null ? sittingLocation.hashCode() : 0);
      result = 31 * result + (answers != null ? answers.hashCode() : 0);
      return result;
   }
}
