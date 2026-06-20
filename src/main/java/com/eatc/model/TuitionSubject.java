package com.eatc.model;

import java.math.BigDecimal;
import java.util.Objects;

public class TuitionSubject {
    private final int subjectId;
    private final String subjectName;
    private final BigDecimal price;

    public TuitionSubject(int subjectId, String subjectName, BigDecimal price) {
        if (subjectName == null || subjectName.isBlank()) {
            throw new IllegalArgumentException("Subject name cannot be blank");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Subject price must be positive");
        }

        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.price = price;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TuitionSubject that)) return false;
        return subjectId == that.subjectId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectId);
    }

    @Override
    public String toString() {
        return subjectName + " (GBP " + price + ")";
    }
}