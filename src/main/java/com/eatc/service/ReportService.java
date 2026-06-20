package com.eatc.service;

import com.eatc.model.Lesson;
import com.eatc.model.TuitionSubject;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {
    private final TuitionCentre centre;

    public ReportService(TuitionCentre centre) {
        if (centre == null) {
            throw new IllegalArgumentException("Tuition centre cannot be null");
        }

        this.centre = centre;
    }

    public String lessonAttendanceAndRatingReport() {
        StringBuilder report = new StringBuilder();

        report.append("\n=== Report 1: Students per Lesson and Average Rating ===\n");

        report.append(String.format(
                "%-5s %-12s %-10s %-10s %-24s %-10s %-10s%n",
                "ID",
                "Date",
                "Day",
                "Slot",
                "Subject",
                "Students",
                "AvgRating"
        ));

        List<Lesson> sortedLessons = centre.getLessons()
                .stream()
                .sorted(
                        Comparator.comparing(Lesson::getLessonDate)
                                .thenComparing(Lesson::getTimeSlot)
                                .thenComparing(lesson -> lesson.getSubject().getSubjectName())
                )
                .collect(Collectors.toList());

        for (Lesson lesson : sortedLessons) {
            report.append(String.format(
                    "%-5d %-12s %-10s %-10s %-24s %-10d %-10.2f%n",
                    lesson.getLessonId(),
                    lesson.getLessonDate(),
                    lesson.getLessonDay(),
                    lesson.getTimeSlot(),
                    lesson.getSubject().getSubjectName(),
                    centre.getBookedStudentCount(lesson.getLessonId()),
                    centre.getAverageRating(lesson.getLessonId())
            ));
        }

        return report.toString();
    }

    public String highestIncomeSubjectReport() {
        StringBuilder report = new StringBuilder();

        report.append("\n=== Report 2: Income by Subject ===\n");

        report.append(String.format(
                "%-5s %-24s %-10s%n",
                "ID",
                "Subject",
                "Income"
        ));

        for (TuitionSubject subject : centre.getSubjects()) {
            BigDecimal income = centre.calculateIncomeForSubject(subject.getSubjectId());

            report.append(String.format(
                    "%-5d %-24s GBP %-10.2f%n",
                    subject.getSubjectId(),
                    subject.getSubjectName(),
                    income
            ));
        }

        centre.findHighestIncomeSubject()
                .ifPresent(subject -> report.append("Highest income subject: ")
                        .append(subject.getSubjectName())
                        .append(" (GBP ")
                        .append(String.format(
                                "%.2f",
                                centre.calculateIncomeForSubject(subject.getSubjectId())
                        ))
                        .append(")\n"));

        return report.toString();
    }
}