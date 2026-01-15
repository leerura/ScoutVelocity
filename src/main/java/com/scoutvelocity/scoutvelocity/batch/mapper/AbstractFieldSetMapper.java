package com.scoutvelocity.scoutvelocity.batch.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class AbstractFieldSetMapper<T> implements FieldSetMapper<T> {
    
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    protected String readString(FieldSet fieldSet, String name) {
        String value = fieldSet.readString(name);
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }

    protected Long readLong(FieldSet fieldSet, String name) {
        try {
            String value = fieldSet.readString(name);
            return (value == null || value.trim().isEmpty()) ? null : Long.parseLong(value.trim());
        } catch (NumberFormatException e) { return null; }
    }

    protected Integer readInteger(FieldSet fieldSet, String name) {
        try {
            String value = fieldSet.readString(name);
            if (value == null || value.trim().isEmpty()) return null;
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) { return null; }
    }

    protected Integer readIntegerFromDouble(FieldSet fieldSet, String name) {
        try {
            String value = fieldSet.readString(name);
            if (value == null || value.trim().isEmpty()) return null;
            Double d = Double.parseDouble(value.trim());
            return Integer.valueOf(d.intValue());
        } catch (NumberFormatException e) { return null; }
    }

    protected Double readDouble(FieldSet fieldSet, String name) {
        try {
            String value = fieldSet.readString(name);
            return (value == null || value.trim().isEmpty()) ? null : Double.parseDouble(value.trim());
        } catch (NumberFormatException e) { return null; }
    }

    protected LocalDate readLocalDate(FieldSet fieldSet, String name) {
        try {
            String value = fieldSet.readString(name);
            if (value == null || value.trim().isEmpty()) return null;
            return LocalDate.parse(value.trim(), DATE_FORMATTER);
        } catch (Exception e) { return null; }
    }
}
