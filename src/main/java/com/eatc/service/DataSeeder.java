package com.eatc.service;

import com.eatc.model.Booking;
import com.eatc.model.Gender;
import com.eatc.model.Lesson;
import com.eatc.model.TimeSlot;
import com.eatc.model.TuitionSubject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DataSeeder {
    private DataSeeder() {
    }

    public static TuitionCentre createSampleCentre() {
        TuitionCentre centre = new TuitionCentre();

        TuitionSubject english = centre.addSubject("English", new BigDecimal("25.00"));
        TuitionSubject math = centre.addSubject("Math", new BigDecimal("30.00"));
        TuitionSubject verbal = centre.addSubject("Verbal Reasoning", new BigDecimal("28.00"));
        TuitionSubject nonVerbal = centre.addSubject("Non-verbal Reasoning", new BigDecimal("28.00"));

        centre.addStudent("Aisha Khan", Gender.FEMALE, LocalDate.of(2013, 2, 15), "12 Oak Road", "07111 111111");
        centre.addStudent("Ben Smith", Gender.MALE, LocalDate.of(2012, 7, 6), "44 Pine Street", "07111 111112");
        centre.addStudent("Chloe Brown", Gender.FEMALE, LocalDate.of(2014, 9, 1), "8 Maple Close", "07111 111113");
        centre.addStudent("Daan Patel", Gender.MALE, LocalDate.of(2013, 11, 18), "90 Cedar Ave", "07111 111114");
        centre.addStudent("Eva Wilson", Gender.FEMALE, LocalDate.of(2012, 5, 21), "17 King Street", "07111 111115");
        centre.addStudent("Farhan Ali", Gender.MALE, LocalDate.of(2014, 4, 9), "29 Queen Road", "07111 111116");
        centre.addStudent("Grace Taylor", Gender.FEMALE, LocalDate.of(2013, 1, 30), "31 Hill Lane", "07111 111117");
        centre.addStudent("Hassan Ahmed", Gender.MALE, LocalDate.of(2012, 12, 12), "6 Park View", "07111 111118");
        centre.addStudent("Isla Johnson", Gender.FEMALE, LocalDate.of(2014, 3, 3), "18 River Way", "07111 111119");
        centre.addStudent("Jack Evans", Gender.MALE, LocalDate.of(2013, 8, 24), "23 Station Road", "07111 111120");

        LocalDate firstSaturday = LocalDate.of(2026, 6, 6);

        for (int week = 0; week < 8; week++) {
            LocalDate saturday = firstSaturday.plusWeeks(week);
            LocalDate sunday = saturday.plusDays(1);

            centre.addLesson(english.getSubjectId(), saturday, TimeSlot.MORNING);
            centre.addLesson(math.getSubjectId(), saturday, TimeSlot.AFTERNOON);
            centre.addLesson(verbal.getSubjectId(), sunday, TimeSlot.MORNING);
            centre.addLesson(nonVerbal.getSubjectId(), sunday, TimeSlot.AFTERNOON);
        }

        addSampleBookings(centre);

        return centre;
    }

    private static void addSampleBookings(TuitionCentre centre) {
        List<Lesson> lessons = centre.getLessons();

        Booking b1 = centre.bookLesson(1, lessons.get(0).getLessonId());
        Booking b2 = centre.bookLesson(2, lessons.get(0).getLessonId());
        Booking b3 = centre.bookLesson(3, lessons.get(1).getLessonId());
        Booking b4 = centre.bookLesson(4, lessons.get(1).getLessonId());
        Booking b5 = centre.bookLesson(5, lessons.get(2).getLessonId());
        Booking b6 = centre.bookLesson(6, lessons.get(2).getLessonId());
        Booking b7 = centre.bookLesson(7, lessons.get(3).getLessonId());
        Booking b8 = centre.bookLesson(8, lessons.get(3).getLessonId());

        centre.bookLesson(9, lessons.get(4).getLessonId());
        centre.bookLesson(10, lessons.get(5).getLessonId());

        centre.checkInStudent(b1.getBookingId());
        centre.addReview(b1.getBookingId(), 5, "Excellent lesson and clear explanations.");

        centre.checkInStudent(b2.getBookingId());
        centre.addReview(b2.getBookingId(), 4, "Good pace and helpful exercises.");

        centre.checkInStudent(b3.getBookingId());
        centre.addReview(b3.getBookingId(), 5, "Math examples were useful.");

        centre.checkInStudent(b4.getBookingId());
        centre.addReview(b4.getBookingId(), 3, "Ok, but I need more practice questions.");

        centre.checkInStudent(b5.getBookingId());
        centre.addReview(b5.getBookingId(), 4, "Good verbal reasoning strategies.");

        centre.checkInStudent(b6.getBookingId());
        centre.addReview(b6.getBookingId(), 5, "Very satisfied.");

        centre.checkInStudent(b7.getBookingId());
        centre.addReview(b7.getBookingId(), 4, "Helpful non-verbal examples.");

        centre.checkInStudent(b8.getBookingId());
        centre.addReview(b8.getBookingId(), 5, "Great lesson.");
    }
}