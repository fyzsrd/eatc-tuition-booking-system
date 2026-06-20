package com.eatc.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

public class Lesson {
    public static final int MAX_STUDENTS = 4;

    private final int lessonId;
    private final TuitionSubject subject;
    private final LocalDate lessonDate;
    private final LessonDay lessonDay;
    private final TimeSlot timeSlot;

    public Lesson(int lessonId, TuitionSubject subject, LocalDate lessonDate, TimeSlot timeSlot) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be null");
        }

        if (lessonDate == null) {
            throw new IllegalArgumentException("Lesson date cannot be null");
        }

        if (timeSlot == null) {
            throw new IllegalArgumentException("Time slot cannot be null");
        }

        DayOfWeek dayOfWeek = lessonDate.getDayOfWeek();

        if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Lessons can only be on Saturday or Sunday");
        }

        this.lessonId = lessonId;
        this.subject = subject;
        this.lessonDate = lessonDate;
        this.lessonDay = dayOfWeek == DayOfWeek.SATURDAY
                ? LessonDay.SATURDAY
                : LessonDay.SUNDAY;
        this.timeSlot = timeSlot;
    }

    public int getLessonId() {
        return lessonId;
    }

    public TuitionSubject getSubject() {
        return subject;
    }

    public LocalDate getLessonDate() {
        return lessonDate;
    }

    public LessonDay getLessonDay() {
        return lessonDay;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public boolean clashesWith(Lesson other) {
        return other != null
                && lessonDate.equals(other.lessonDate)
                && timeSlot == other.timeSlot;
    }

    public String shortName() {
        return "#" + lessonId + " "
                + lessonDate + " "
                + lessonDay + " "
                + timeSlot + " "
                + subject.getSubjectName();
    }

    @Override
    public String toString() {
        return shortName() + " GBP " + subject.getPrice();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lesson lesson)) return false;
        return lessonId == lesson.lessonId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lessonId);
    }
}