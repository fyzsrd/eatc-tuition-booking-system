package com.eatc.app;

import com.eatc.model.Booking;
import com.eatc.model.Lesson;
import com.eatc.service.DataSeeder;
import com.eatc.service.ReportService;
import com.eatc.service.TuitionCentre;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleApp {
    private final TuitionCentre centre;
    private final ReportService reportService;
    private final Scanner scanner;

    public ConsoleApp(TuitionCentre centre) {
        this.centre = centre;
        this.reportService = new ReportService(centre);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        TuitionCentre centre = DataSeeder.createSampleCentre();
        new ConsoleApp(centre).run();
    }

    public void run() {
        System.out.println("Welcome to Excel Academy Tuition Centre Booking System");

        boolean running = true;

        while (running) {
            printMenu();

            int choice = readInt("Choose option: ");

            try {
                switch (choice) {
                    case 1 -> listStudents();
                    case 2 -> listSubjects();
                    case 3 -> listLessons();
                    case 4 -> createBooking();
                    case 5 -> changeBooking();
                    case 6 -> cancelBooking();
                    case 7 -> checkInStudent();
                    case 8 -> addReview();
                    case 9 -> listBookings();
                    case 10 -> printReports();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid option.");
                }
            } catch (RuntimeException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }

        System.out.println("Goodbye.");
    }

    private void printMenu() {
        System.out.println("\n---------------- MENU ----------------");
        System.out.println("1. List students");
        System.out.println("2. List subjects");
        System.out.println("3. List lessons and spaces");
        System.out.println("4. Book lesson");
        System.out.println("5. Change booking date/time");
        System.out.println("6. Cancel booking");
        System.out.println("7. Check in student");
        System.out.println("8. Add review and rating");
        System.out.println("9. List bookings");
        System.out.println("10. Print reports");
        System.out.println("0. Exit");
    }

    private void listStudents() {
        System.out.println("\nStudents:");

        centre.getStudents()
                .forEach(student -> System.out.println(
                        student.getStudentId()
                                + ". " + student.getName()
                                + " | " + student.getGender()
                                + " | DOB: " + student.getDateOfBirth()
                                + " | Emergency: " + student.getEmergencyContactPhone()
                ));
    }

    private void listSubjects() {
        System.out.println("\nSubjects:");

        centre.getSubjects()
                .forEach(subject -> System.out.println(
                        subject.getSubjectId()
                                + ". " + subject.getSubjectName()
                                + " | Price: GBP " + subject.getPrice()
                ));
    }

    private void listLessons() {
        System.out.println("\nLessons:");

        centre.getLessons()
                .forEach(lesson -> System.out.println(
                        lesson.getLessonId()
                                + ". " + lesson.getLessonDate()
                                + " " + lesson.getLessonDay()
                                + " " + lesson.getTimeSlot()
                                + " | " + lesson.getSubject().getSubjectName()
                                + " | Spaces: "
                                + centre.getAvailableSpaces(lesson.getLessonId())
                                + "/" + Lesson.MAX_STUDENTS
                ));
    }

    private void listBookings() {
        System.out.println("\nBookings:");
        centre.getBookings().forEach(System.out::println);
    }

    private void createBooking() {
        listStudents();
        int studentId = readInt("Student id: ");

        listLessons();
        int lessonId = readInt("Lesson id: ");

        Booking booking = centre.bookLesson(studentId, lessonId);

        System.out.println("Booking created: " + booking);
    }

    private void changeBooking() {
        listBookings();
        int bookingId = readInt("Booking id to change: ");

        listLessons();
        int lessonId = readInt("New lesson id: ");

        Booking booking = centre.changeBooking(bookingId, lessonId);

        System.out.println("Booking updated: " + booking);
    }

    private void cancelBooking() {
        listBookings();
        int bookingId = readInt("Booking id to cancel: ");

        Booking booking = centre.cancelBooking(bookingId);

        System.out.println("Booking cancelled: " + booking);
    }

    private void checkInStudent() {
        listBookings();
        int bookingId = readInt("Booking id to check in: ");

        Booking booking = centre.checkInStudent(bookingId);

        System.out.println("Student checked in: " + booking);
    }

    private void addReview() {
        listBookings();

        int bookingId = readInt("Booking id to review: ");
        int rating = readInt("Rating 1-5: ");

        scanner.nextLine();

        System.out.print("Review comment: ");
        String comment = scanner.nextLine();

        Booking booking = centre.addReview(bookingId, rating, comment);

        System.out.println("Review saved for booking: " + booking.getBookingId());
    }

    private void printReports() {
        System.out.println(reportService.lessonAttendanceAndRatingReport());
        System.out.println(reportService.highestIncomeSubjectReport());
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Please enter a valid number.");
            }
        }
    }
}