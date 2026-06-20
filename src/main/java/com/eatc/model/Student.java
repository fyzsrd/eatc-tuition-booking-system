package com.eatc.model;

import java.time.LocalDate;
import java.util.Objects;

public class Student {
    private final int studentId;
    private final String name;
    private final Gender gender;
    private final LocalDate dateOfBirth;
    private final String address;
    private final String emergencyContactPhone;

    public Student(int studentId,
                   String name,
                   Gender gender,
                   LocalDate dateOfBirth,
                   String address,
                   String emergencyContactPhone) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Student name cannot be blank");
        }

        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }

        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth must be valid");
        }

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address cannot be blank");
        }

        if (emergencyContactPhone == null || emergencyContactPhone.isBlank()) {
            throw new IllegalArgumentException("Emergency contact phone cannot be blank");
        }

        this.studentId = studentId;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return studentId == student.studentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return studentId + " - " + name;
    }
}