package com.makowski.bookshelves.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.makowski.bookshelves.entity.Book;
import com.makowski.bookshelves.entity.Rating;
import com.makowski.bookshelves.entity.User;
import com.makowski.bookshelves.exceptions.AccessDeniedException;
import com.makowski.bookshelves.exceptions.EntityNotFoundException;
import com.makowski.bookshelves.exceptions.InvalidRequestException;
import com.makowski.bookshelves.repository.RatingRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RatingService {

    private UserService userService;
    private BookService bookService;
    private RatingRepository ratingRepository;

    public Rating getRating(Long id) {
        return ratingRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(id, Rating.class));     
    } 

    public Rating showRating(Long id) {
        Rating rating = getRating(id);
        if (rating.getUser().getPrivateProfile()) 
            if (!isItProperUser(id)) rating.setUser(null);
        return rating;
    }

    public Rating createRating(Rating rating, Long bookId) {
        User user = userService.getLoggedUser();
        if (canUserRateIt(user.getId(), bookId)) {
            if (isItProperRating(rating)) {
                if (rating.getScore() != 0) changeRating(bookService.getBook(bookId), 0, rating.getScore());
                rating.setDate(LocalDate.now());
                rating.setUser(user);
                rating.setBook(bookService.getBook(bookId));
                return ratingRepository.save(rating);    
            } else throw new InvalidRequestException();
        } else throw new InvalidRequestException();   
    }

    public Rating updateRating(Long ratingId, Rating newRating) {
        Rating rating = getRating(ratingId);
        if (isItProperUser(ratingId)) {
            if (isItProperRating(newRating)) { 
                if (rating.getScore() != newRating.getScore()) {
                    changeRating(rating.getBook(), rating.getScore(), newRating.getScore());
                    rating.setScore(newRating.getScore());
                }
                rating.setReview(newRating.getReview());
                rating.setDate(LocalDate.now());
                return ratingRepository.save(rating);
            } else throw new InvalidRequestException();
        } else throw new AccessDeniedException();   
    }   

    public void deleteRating(Long ratingId) {
        if (ratingRepository.existsById(ratingId)) {
            if (isItProperUser(ratingId)) {
                Rating rating = getRating(ratingId);
                if (rating.getScore() != 0) changeRating(rating.getBook(), rating.getScore(), 0);
                ratingRepository.deleteById(ratingId);
            } else throw new AccessDeniedException();
        } else throw new EntityNotFoundException(ratingId, Rating.class);
    }

    public void changeRating(Book book, int oldScore, int newScore) {
        if (oldScore == 0) {
            book.setScoresNumber(book.getScoresNumber() + 1);
            book.setScoresSum(book.getScoresSum() + newScore);
        } else if (newScore == 0) {
            book.setScoresNumber(book.getScoresNumber() - 1);
            book.setScoresSum(book.getScoresSum() - oldScore);
        } else {
            book.setScoresSum(book.getScoresSum() + (newScore - oldScore));
        }
        if (book.getScoresNumber() == 0) book.setRating(0);      
            else book.setRating((float)Math.round(((float)book.getScoresSum() / book.getScoresNumber())* 10) / 10);
        bookService.saveBook(book);
    }

    public boolean isItProperUser(Long ratingId) {
        return getRating(ratingId).getUser() == userService.getLoggedUser();
    }

    public boolean isItProperRating(Rating rating) {
        if (rating.getReview() == null) rating.setReview("");
        return ((rating.getScore() != 0) || (!rating.getReview().isBlank())) && (rating.getScore() <= 10);
    }

    public boolean canUserRateIt(Long userId, Long bookId) {
        return !ratingRepository.existByUserIdAndBookId(userId, bookId);
    }
}