package com.sctrcd.qzr.rules;

import static org.junit.Assert.*;

import org.joda.time.LocalDate;
import org.junit.Test;

public class QuizTest {

    @Test
    public void shouldDeriveLocalDate() {
        LocalDate date = LocalDate.parse("1986-06-06");
        System.out.println(date);
    }

}
