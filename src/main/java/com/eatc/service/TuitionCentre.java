package com.eatc.service;

import com.eatc.model.Booking;

import com.eatc.model.Gender;
import com.eatc.model.Lesson;
import com.eatc.model.Rating;
import com.eatc.model.Review;
import com.eatc.model.Student;
import com.eatc.model.TimeSlot;
import com.eatc.model.TuitionSubject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class TuitionCentre {
    private final Map<Integer, Student> students = new LinkedHashMap<>();
    private final Map<Integer, TuitionSubject> subjects = new LinkedHashMap<>();
    private final Map<Integer, Lesson> lessons = new LinkedHashMap<>();
    private final Map<Integer, Booking> bookings = new LinkedHashMap<>();

    private int nextStudentId = 1;
    private int nextSubjectId = 1;
    private int nextLessonId = 1;
    private int nextBookingId = 1;

    public Student addStudent(String name,
                              Gender gender,
                              LocalDate dob,
                              String address,
                              String emergencyPhone) {

        Student student = new Student(
                nextStudentId++,
                name,
                gender,
                dob,
                address,
                emergencyPhone
        );

        students.put(student.getStudentId(), student);
        return student;
    }

    public TuitionSubject addSubject(String subjectName, BigDecimal price) {
        TuitionSubject subject = new TuitionSubject(
                nextSubjectId++,
                subjectName,
                price
        );

        subjects.put(subject.getSubjectId(), subject);
        return subject;
    }

    public Lesson addLesson(int subjectId, LocalDate lessonDate, TimeSlot timeSlot) {
        TuitionSubject subject = getSubjectById(subjectId);

        ensureLessonSlotIsUnique(subject, lessonDate, timeSlot);

        Lesson lesson = new Lesson(
                nextLessonId++,
                subject,
                lessonDate,
                timeSlot
        );

        lessons.put(lesson.getLessonId(), lesson);
        return lesson;
    }

    public Booking bookLesson(int studentId, int lessonId) {
        Student student = getStudentById(studentId);
        Lesson lesson = getLessonById(lessonId);

        ensureLessonHasSpace(lesson, Optional.empty());
        ensureStudentHasNoTimeConflict(student, lesson, Optional.empty());

        Booking booking = new Booking(
                nextBookingId++,
                student,
                lesson
        );

        bookings.put(booking.getBookingId(), booking);
        return booking;
    }

    public Booking changeBooking(int bookingId, int newLessonId) {
        Booking booking = getBookingById(bookingId);
        Lesson newLesson = getLessonById(newLessonId);

        ensureLessonHasSpace(newLesson, Optional.of(booking));
        ensureStudentHasNoTimeConflict(
                booking.getStudent(),
                newLesson,
                Optional.of(booking)
        );

        booking.changeLesson(newLesson);
        return booking;
    }

    public Booking cancelBooking(int bookingId) {
        Booking booking = getBookingById(bookingId);
        booking.cancel();
        return booking;
    }

    public Booking checkInStudent(int bookingId) {
        Booking booking = getBookingById(bookingId);
        booking.markAttended();
        return booking;
    }

    public Booking addReview(int bookingId, int ratingValue, String comment) {
        Booking booking = getBookingById(bookingId);

        Review review = new Review(
                Rating.fromValue(ratingValue),
                comment
        );

        booking.addReview(review);
        return booking;
    }

    public int getBookedStudentCount(int lessonId) {
        Lesson lesson = getLessonById(lessonId);

        return (int) bookings.values()
                .stream()
                .filter(Booking::isActive)
                .filter(booking -> booking.getLesson().equals(lesson))
                .count();
    }

    public int getAvailableSpaces(int lessonId) {
        return Lesson.MAX_STUDENTS - getBookedStudentCount(lessonId);
    }

    public double getAverageRating(int lessonId) {
        Lesson lesson = getLessonById(lessonId);

        return bookings.values()
                .stream()
                .filter(booking -> booking.getLesson().equals(lesson))
                .map(Booking::getReview)
                .flatMap(Optional::stream)
                .mapToInt(review -> review.getRating().getValue())
                .average()
                .orElse(0.0);
    }

    public BigDecimal calculateIncomeForSubject(int subjectId) {
        TuitionSubject subject = getSubjectById(subjectId);

        return bookings.values()
                .stream()
                .filter(Booking::isActive)
                .filter(booking -> booking.getLesson().getSubject().equals(subject))
                .map(booking -> booking.getLesson().getSubject().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<TuitionSubject> findHighestIncomeSubject() {
        return subjects.values()
                .stream()
                .max(Comparator.comparing(
                        subject -> calculateIncomeForSubject(subject.getSubjectId())
                ));
    }

    public Student getStudentById(int studentId) {
        Student student = students.get(studentId);

        if (student == null) {
            throw new NoSuchElementException("Student not found: " + studentId);
        }

        return student;
    }

    public TuitionSubject getSubjectById(int subjectId) {
        TuitionSubject subject = subjects.get(subjectId);

        if (subject == null) {
            throw new NoSuchElementException("Subject not found: " + subjectId);
        }

        return subject;
    }

    public Lesson getLessonById(int lessonId) {
        Lesson lesson = lessons.get(lessonId);

        if (lesson == null) {
            throw new NoSuchElementException("Lesson not found: " + lessonId);
        }

        return lesson;
    }

    public Booking getBookingById(int bookingId) {
        Booking booking = bookings.get(bookingId);

        if (booking == null) {
            throw new NoSuchElementException("Booking not found: " + bookingId);
        }

        return booking;
    }

    public List<Student> getStudents() {
        return List.copyOf(students.values());
    }

    public List<TuitionSubject> getSubjects() {
        return List.copyOf(subjects.values());
    }

    public List<Lesson> getLessons() {
        return List.copyOf(lessons.values());
    }

    public List<Booking> getBookings() {
        return List.copyOf(bookings.values());
    }

    public List<Lesson> findLessonsBySubject(int subjectId) {
        TuitionSubject subject = getSubjectById(subjectId);

        return lessons.values()
                .stream()
                .filter(lesson -> lesson.getSubject().equals(subject))
                .sorted(
                        Comparator.comparing(Lesson::getLessonDate)
                                .thenComparing(Lesson::getTimeSlot)
                )
                .collect(Collectors.toList());
    }

    private void ensureLessonSlotIsUnique(TuitionSubject subject,
                                          LocalDate date,
                                          TimeSlot slot) {

        boolean duplicate = lessons.values()
                .stream()
                .anyMatch(existing -> existing.getSubject().equals(subject)
                        && existing.getLessonDate().equals(date)
                        && existing.getTimeSlot() == slot);

        if (duplicate) {
            throw new IllegalArgumentException(
                    "This subject already has a lesson at the selected date/time"
            );
        }
    }

    private void ensureLessonHasSpace(Lesson lesson,
                                      Optional<Booking> bookingToIgnore) {

        long activeBookings = bookings.values()
                .stream()
                .filter(Booking::isActive)
                .filter(booking -> booking.getLesson().equals(lesson))
                .filter(booking -> bookingToIgnore
                        .map(ignore -> !ignore.equals(booking))
                        .orElse(true))
                .count();

        if (activeBookings >= Lesson.MAX_STUDENTS) {
            throw new IllegalStateException(
                    "Lesson is full. Maximum students: " + Lesson.MAX_STUDENTS
            );
        }
    }

    private void ensureStudentHasNoTimeConflict(Student student,
                                                Lesson selectedLesson,
                                                Optional<Booking> bookingToIgnore) {

        boolean hasConflict = bookings.values()
                .stream()
                .filter(Booking::isActive)
                .filter(booking -> booking.getStudent().equals(student))
                .filter(booking -> bookingToIgnore
                        .map(ignore -> !ignore.equals(booking))
                        .orElse(true))
                .map(Booking::getLesson)
                .anyMatch(existingLesson -> existingLesson.clashesWith(selectedLesson));

        if (hasConflict) {
            throw new IllegalStateException(
                    "Student already has another lesson at this date/time"
            );
        }
    }
}