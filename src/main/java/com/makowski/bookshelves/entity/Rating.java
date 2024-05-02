package com.makowski.bookshelves.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "rating", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ownerr", "book"})
}) 
public class Rating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Min(0)
    @Max(10)
    @Column(name = "score", nullable = false)
    private int score; 

    @Column(name = "review")
    private String review;

    @Column(name = "date")
    private LocalDate date; 

    @ManyToOne(optional = false)
    @JoinColumn(name = "ownerr", referencedColumnName = "id")
    private User user;

    @ManyToOne(optional = false)           
    @JoinColumn(name = "book", referencedColumnName = "id")
    private Book book;
}
