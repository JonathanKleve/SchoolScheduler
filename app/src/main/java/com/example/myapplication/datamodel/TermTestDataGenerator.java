package com.example.myapplication.datamodel;

import com.example.myapplication.datamodel.Term;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TermTestDataGenerator {

    private static final String[] TERM_TITLES = {
            "Fall 2024", "Spring 2025", "Summer 2025",
            "Term A", "Term B", "Term C",
            "Year 1 - Semester 1", "Year 1 - Semester 2",
            "Winter Term", "Autumn Semester"
    };

    public static List<Term> generateTermTestData(int numberOfTerms) {
        List<Term> terms = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numberOfTerms; i++) {
            String title = TERM_TITLES[random.nextInt(TERM_TITLES.length)] + " - Test " + (i + 1);

            // Generate random start and end dates within a reasonable range
            LocalDate start = LocalDate.now().plusDays(random.nextInt(30) - 15); // +/- 15 days from now
            LocalDate end = start.plusDays(random.nextInt(120) + 30); // Start + 30 to 150 days

            Term term = new Term(0, title, start, end);
            terms.add(term);
        }

        return terms;
    }

    public static void main(String[] args) {
        // Example usage: Generate 15 test terms and print them
        List<Term> testTerms = generateTermTestData(15);
        for (Term term : testTerms) {
            System.out.println("Title: " + term.getTitle() + ", Start: " + term.getStart() + ", End: " + term.getEnd());
        }
    }
}