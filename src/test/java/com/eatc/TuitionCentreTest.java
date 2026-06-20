package com.eatc;

import com.eatc.model.Booking;
import com.eatc.model.BookingStatus;
import com.eatc.model.Gender;
import com.eatc.model.Lesson;
import com.eatc.model.Student;
import com.eatc.model.TimeSlot;
import com.eatc.model.TuitionSubject;
import com.eatc.service.TuitionCentre;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TuitionCentreTest {
    private TuitionCentre centre;
    private Student student1;
    private Student student2;
    private TuitionSubject english;
    private TuitionSubject math;
    private Lesson englishMorning;
    private Lesson englishNextWeekMorning;
    private Lesson mathSameTime;

    @BeforeEach
    void setUp() {
        centre = new TuitionCentre();

        student1 = centre.addStudent(
                "Student One",
                Gender.FEMALE,
                LocalDate.of(2013, 1, 1),
                "Address 1",
                "07000000001"
        );

        student2 = centre.addStudent(
                "Student Two",
                Gender.MALE,
                LocalDate.of(2012, 2, 2),
                "Address 2",
                "07000000002"
        );

        english = centre.addSubject("English", new BigDecimal("25.00"));
        math = centre.addSubject("Math", new BigDecimal("30.00"));

        englishMorning = centre.addLesson(
                english.getSubjectId(),
                LocalDate.of(2026, 6, 6),
                TimeSlot.MORNING
        );

        englishNextWeekMorning = centre.addLesson(
                english.getSubjectId(),
                LocalDate.of(2026, 6, 13),
                TimeSlot.MORNING
        );

        mathSameTime = centre.addLesson(
                math.getSubjectId(),
                LocalDate.of(2026, 6, 6),
                TimeSlot.MORNING
        );
    }

    @Test
    void bookLessonCreatesBookedStatusAndReducesSpaces() {
        Booking booking = centre.bookLesson(
                student1.getStudentId(),
                englishMorning.getLessonId()
        );

        assertEquals(BookingStatus.BOOKED, booking.getStatus());
        assertEquals(3, centre.getAvailableSpaces(englishMorning.getLessonId()));
    }

    @Test
    void lessonAllowsMaximumFourStudentsOnly() {
        centre.addStudent("Student 3", Gender.FEMALE, LocalDate.of(2013, 3, 3), "Address 3", "07000000003");
        centre.addStudent("Student 4", Gender.MALE, LocalDate.of(2013, 4, 4), "Address 4", "07000000004");
        centre.addStudent("Student 5", Gender.OTHER, LocalDate.of(2013, 5, 5), "Address 5", "07000000005");

        centre.bookLesson(1, englishMorning.getLessonId());
        centre.bookLesson(2, englishMorning.getLessonId());
        centre.bookLesson(3, englishMorning.getLessonId());
        centre.bookLesson(4, englishMorning.getLessonId());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> centre.bookLesson(5, englishMorning.getLessonId())
        );

        assertTrue(exception.getMessage().contains("Lesson is full"));
    }

    @Test
    void studentCannotBookTwoLessonsAtSameTime() {
        centre.bookLesson(
                student1.getStudentId(),
                englishMorning.getLessonId()
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> centre.bookLesson(student1.getStudentId(), mathSameTime.getLessonId())
        );

        assertTrue(exception.getMessage().contains("date/time"));
    }

    @Test
    void bookingCanChangeDateTimeForSameSubject() {
        Booking booking = centre.bookLesson(
                student1.getStudentId(),
                englishMorning.getLessonId()
        );

        centre.changeBooking(
                booking.getBookingId(),
                englishNextWeekMorning.getLessonId()
        );

        assertEquals(englishNextWeekMorning, booking.getLesson());
        assertEquals(4, centre.getAvailableSpaces(englishMorning.getLessonId()));
        assertEquals(3, centre.getAvailableSpaces(englishNextWeekMorning.getLessonId()));
    }

    @Test
    void bookingCannotChangeToDifferentSubject() {
        Booking booking = centre.bookLesson(
                student1.getStudentId(),
                englishMorning.getLessonId()
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> centre.changeBooking(booking.getBookingId(), mathSameTime.getLessonId())
        );

        assertTrue(exception.getMessage().contains("different subject"));
    }

    @Test
    void cancelledBookingDoesNotCountAgainstCapacity() {
        Booking booking = centre.bookLesson(
                student1.getStudentId(),
                englishMorning.getLessonId()
        );

        centre.cancelBooking(booking.getBookingId());

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(4, centre.getAvailableSpaces(englishMorning.getLessonId()));
    }

    @Test
    void attendedBookingCanHaveReviewAndAverageRating() {
        Booking b1 = centre.bookLesson(
                student1.getStudentId(),
                englishMorning.getLessonId()
        );

        Booking b2 = centre.bookLesson(
                student2.getStudentId(),
                englishMorning.getLessonId()
        );

        centre.checkInStudent(b1.getBookingId());
        centre.addReview(b1.getBookingId(), 5, "Great");

        centre.checkInStudent(b2.getBookingId());
        centre.addReview(b2.getBookingId(), 3, "Ok");

        assertEquals(BookingStatus.ATTENDED, b1.getStatus());
        assertTrue(b1.getReview().isPresent());
        assertEquals(4.0, centre.getAverageRating(englishMorning.getLessonId()));
    }

    @Test
    void bookedStudentCannotReviewBeforeCheckIn() {
        Booking booking = centre.bookLesson(
                student1.getStudentId(),
                englishMorning.getLessonId()
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> centre.addReview(booking.getBookingId(), 4, "Good")
        );

        assertTrue(exception.getMessage().contains("after attending"));
    }

    @Test
    void highestIncomeSubjectIsCalculatedFromActiveBookings() {
        centre.bookLesson(
                student1.getStudentId(),
                mathSameTime.getLessonId()
        );

        centre.bookLesson(
                student2.getStudentId(),
                englishMorning.getLessonId()
        );

        assertEquals(math, centre.findHighestIncomeSubject().orElseThrow());
        assertEquals(new BigDecimal("30.00"), centre.calculateIncomeForSubject(math.getSubjectId()));
    }
}