package com.makowski.bookshelves.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@NoArgsConstructor
@Table(name = "books")
public class Book {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotBlank(message = "Please enter book title")
    @Column(name = "title", nullable = false)
    private String title;

    @NonNull
    @NotBlank(message = "Please enter author's name")
    @Column(name = "author", nullable = false)
    private String author;

    @NonNull
    @NotBlank(message = "Please enter publisher's name")
    @Column(name = "publisher", nullable = false)
    private String publisher;

    @NonNull           
    @NotBlank(message = "Please enter genre")
    @Column(name = "genre", nullable = false)
    private String genre;

    @Min(value = 0, message = "Please enter number of pages")     
    @Column(name = "pages", nullable = false)
    private int pages;

    @Min(value = 0, message = "Please enter year of publication")
    @Column(name = "publication_year", nullable = false)
    private int year;

    @Column(name = "rating", nullable = false)
    private float rating;    

    @Column(name = "scores_number", nullable = false)
    private int scoresNumber;      

    @JsonIgnore
    @Column(name = "scores_sum", nullable = false)
    private int scoresSum;
    
    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Rating> ratings;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "books_on_shelves",
        joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "shelf_id", referencedColumnName = "id")
        )
    private List<Shelf> shelves;
}
