package com.makowski.bookshelves.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookDto {
    
    private Long id;
    private String title;
    private String author;
    private int year;
    private double rating;
    private int scoresNumber;
}
