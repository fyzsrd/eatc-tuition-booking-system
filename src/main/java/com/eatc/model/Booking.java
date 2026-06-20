package com.eatc.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Booking {
    private final int bookingId;
    private final Student student;
    private Lesson lesson;
    private BookingStatus status;
    private Review review;
    private final LocalDateTime createdAt;

    public Booking(int bookingId, Student student, Lesson lesson) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }

        if (lesson == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }

        this.bookingId = bookingId;
        this.student = student;
        this.lesson = lesson;
        this.status = BookingStatus.BOOKED;
        this.createdAt = LocalDateTime.now();
    }

    public int getBookingId() {
        return bookingId;
    }

    public Student getStudent() {
        return student;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Optional<Review> getReview() {
        return Optional.ofNullable(review);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return status == BookingStatus.BOOKED
                || status == BookingStatus.ATTENDED;
    }

    public void changeLesson(Lesson newLesson) {
        if (newLesson == null) {
            throw new IllegalArgumentException("New lesson cannot be null");
        }

        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change a cancelled booking");
        }

        if (!lesson.getSubject().equals(newLesson.getSubject())) {
            throw new IllegalArgumentException("Cannot change booking to a different subject");
        }

        this.lesson = newLesson;
    }

    public void cancel() {
        if (status == BookingStatus.ATTENDED) {
            throw new IllegalStateException("Cannot cancel a lesson that has already been attended");
        }

        status = BookingStatus.CANCELLED;
    }

    public void markAttended() {
        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot attend a cancelled booking");
        }

        status = BookingStatus.ATTENDED;
    }

    public void addReview(Review review) {
        if (status != BookingStatus.ATTENDED) {
            throw new IllegalStateException("Student can review only after attending the lesson");
        }

        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null");
        }

        this.review = review;
    }

    @Override
    public String toString() {
        return "Booking #" + bookingId
                + " | " + student.getName()
                + " | " + lesson.shortName()
                + " | " + status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking booking)) return false;
        return bookingId == booking.bookingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }
}