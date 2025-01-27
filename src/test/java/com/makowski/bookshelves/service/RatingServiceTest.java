package com.makowski.bookshelves.service;

import com.makowski.bookshelves.entity.Book;
import com.makowski.bookshelves.entity.Rating;
import com.makowski.bookshelves.entity.User;
import com.makowski.bookshelves.exceptions.AccessDeniedException;
import com.makowski.bookshelves.exceptions.EntityNotFoundException;
import com.makowski.bookshelves.exceptions.InvalidRequestException;
import com.makowski.bookshelves.repository.RatingRepository;
import com.makowski.bookshelves.testutils.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @InjectMocks
    RatingService ratingService;
    @Mock
    UserService userService;
    @Mock
    BookService bookService;
    @Mock
    RatingRepository ratingRepository;

    @Test
    void getRating_ReturnsRating_WhenRatingExists() {
        Rating rating = TestDataFactory.createTestRating();

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        Rating result = ratingService.getRating(1L);

        assertEquals(rating, result);
    }

    @Test
    void getRating_ThrowsException_WhenRatingDoesNotExist() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ratingService.getRating(1L));
    }

    @Test
    void showRating_ReturnsRatingWithUsername_WhenAccountIsPublic() {
        Rating rating = TestDataFactory.createTestRating();
        User user = TestDataFactory.createTestUser();
        rating.setUser(user);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        Rating result = ratingService.showRating(1L);

        assertEquals(user, result.getUser());
    }

    @Test
    void showRating_ReturnsRatingWithUsername_WhenAccountIsPrivateAndUserIsOwner() {
        Rating rating = TestDataFactory.createTestRating();
        User user = TestDataFactory.createTestUser();
        user.setPrivateProfile(true);
        rating.setUser(user);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(userService.getLoggedUser()).thenReturn(user);

        Rating result = ratingService.showRating(1L);

        assertEquals(user, result.getUser());
    }

    @Test
    void showRating_ReturnsRatingWithoutUsername_WhenAccountIsPrivateAndUserIsNotOwner() {
        Rating rating = TestDataFactory.createTestRating();
        User loggedUser = TestDataFactory.createTestUser();
        User ratingOwner = TestDataFactory.createAnotherTestUser();
        ratingOwner.setPrivateProfile(true);
        rating.setUser(ratingOwner);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(userService.getLoggedUser()).thenReturn(loggedUser);

        Rating result = ratingService.showRating(1L);

        assertNull(result.getUser());
    }

    @Test
    void createRating_ReturnsRatingAndChangeBooksRate_WhenRatingWithScoreSuccessfullyCreated() {
        User user = TestDataFactory.createTestUser();
        Book book = TestDataFactory.createTestBook();
        book.setScoresSum(10);
        Rating rating = TestDataFactory.createTestRating();
        rating.setScore(2);

        when(userService.getLoggedUser()).thenReturn(user);
        when(bookService.getBook(1L)).thenReturn(book);
        when(ratingRepository.existsByUserAndBook(user, book)).thenReturn(false);
        when(ratingRepository.save(rating)).thenReturn(rating);

        Rating result = ratingService.createRating(rating, 1L);

        assertEquals(user, result.getUser());
        assertEquals(book, result.getBook());
        assertEquals(6.0F, book.getRating());
    }

    @Test
    void createRating_ReturnsRatingAndDoesNotChangeBooksRate_WhenRatingWithoutScoreSuccessfullyCreated() {
        User user = TestDataFactory.createTestUser();
        Book book = TestDataFactory.createTestBook();
        Rating rating = TestDataFactory.createTestRating();
        rating.setReview("test");

        when(userService.getLoggedUser()).thenReturn(user);
        when(bookService.getBook(1L)).thenReturn(book);
        when(ratingRepository.existsByUserAndBook(user, book)).thenReturn(false);
        when(ratingRepository.save(rating)).thenReturn(rating);

        Rating result = ratingService.createRating(rating, 1L);

        assertEquals(user, result.getUser());
        assertEquals(book, result.getBook());
        assertEquals(10.0F, book.getRating());
    }

    @Test
    void createRating_ThrowsException_WhenRatingFromThisUserForThisBookAlreadyExists() {
        User user = TestDataFactory.createTestUser();
        Book book = TestDataFactory.createTestBook();
        Rating rating = TestDataFactory.createTestRating();

        when(userService.getLoggedUser()).thenReturn(user);
        when(bookService.getBook(1L)).thenReturn(book);
        when(ratingRepository.existsByUserAndBook(user, book)).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> ratingService.createRating(rating, 1L));
    }

    @Test
    void createRating_ThrowsException_WhenRatingIsIncorrect() {
        User user = TestDataFactory.createTestUser();
        Book book = TestDataFactory.createTestBook();
        Rating rating = TestDataFactory.createTestRating();

        when(userService.getLoggedUser()).thenReturn(user);
        when(bookService.getBook(1L)).thenReturn(book);
        when(ratingRepository.existsByUserAndBook(user, book)).thenReturn(false);

        assertThrows(InvalidRequestException.class, () -> ratingService.createRating(rating, 1L));
    }

    @Test
    void updateRating_ReturnsEditedRatingAndChangeBooksRate_WhenScoreWasUpdated() {
        Book book = TestDataFactory.createTestBook();
        book.setScoresSum(10);
        User user = TestDataFactory.createTestUser();
        Rating rating = TestDataFactory.createTestRating();
        rating.setUser(user);
        rating.setBook(book);
        Rating newRating = new Rating();
        newRating.setScore(2);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(userService.getLoggedUser()).thenReturn(user);
        when(ratingRepository.save(rating)).thenReturn(rating);

        Rating result = ratingService.updateRating(1L, newRating);

        assertEquals(1L, result.getId());
        assertEquals(2, result.getScore());
        assertEquals(6.0F, book.getRating());
    }

    @Test
    void updateRating_ReturnsEditedRatingAndDoesNotChangeBooksRate_WhenScoreWasNotUpdated() {
        User user = TestDataFactory.createTestUser();
        Rating rating = TestDataFactory.createTestRating();
        rating.setUser(user);
        Rating newRating = new Rating();
        newRating.setReview("test");

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(userService.getLoggedUser()).thenReturn(user);
        when(ratingRepository.save(rating)).thenReturn(rating);

        Rating result = ratingService.updateRating(1L, newRating);

        assertEquals(1L, result.getId());
        assertEquals("test", result.getReview());
    }

    @Test
    void updateRating_ThrowsException_WhenUserIsNotOwner() {
        User loggedUser = TestDataFactory.createTestUser();
        User ratingOwner = TestDataFactory.createAnotherTestUser();
        Rating rating = TestDataFactory.createTestRating();
        rating.setUser(ratingOwner);
        Rating newRating = new Rating();

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(userService.getLoggedUser()).thenReturn(loggedUser);

        assertThrows(AccessDeniedException.class, () -> ratingService.updateRating(1L, newRating));
    }

    @Test
    void updateRating_ThrowsException_WhenRatingIsNotValid() {
        User user = TestDataFactory.createTestUser();
        Rating rating = TestDataFactory.createTestRating();
        rating.setUser(user);
        Rating newRating = new Rating();

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(userService.getLoggedUser()).thenReturn(user);

        assertThrows(InvalidRequestException.class, () -> ratingService.updateRating(1L, newRating));
    }

    @Test
    void deleteRating_DeletesRatingAndChangesBooksRate_WhenRatingContainsScore() {
        Book book = TestDataFactory.createTestBook();
        User user = TestDataFactory.createTestUser();
        Rating rating = TestDataFactory.createTestRating();
        rating.setUser(user);
        rating.setBook(book);
        rating.setScore(10);

        when(userService.getLoggedUser()).thenReturn(user);
        when(ratingRepository.existsById(1L)).thenReturn(true);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        ratingService.deleteRating(1L);

        verify(ratingRepository).deleteById(1L);
        assertEquals(0.0F, book.getRating());

    }

    @Test
    void deleteRating_DeletesRatingAndDoesNotChangeBookRate_WhenRatingDoesNotContainScore() {
        Book book = TestDataFactory.createTestBook();
        User user = TestDataFactory.createTestUser();
        Rating rating = TestDataFactory.createTestRating();
        rating.setUser(user);
        rating.setBook(book);

        when(userService.getLoggedUser()).thenReturn(user);
        when(ratingRepository.existsById(1L)).thenReturn(true);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        ratingService.deleteRating(1L);

        verify(ratingRepository).deleteById(1L);
        assertEquals(10.0F, book.getRating());
    }

    @Test
    void deleteRating_ThrowsException_WhenRatingDoesNotExist() {
        when(ratingRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> ratingService.deleteRating(1L));
    }

    @Test
    void deleteRating_ThrowsException_WhenUserDoesNotOwnRating() {
        User loggedUser = TestDataFactory.createTestUser();
        User ratingOwner = TestDataFactory.createAnotherTestUser();
        Rating rating = TestDataFactory.createTestRating();
        rating.setUser(ratingOwner);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(ratingRepository.existsById(1L)).thenReturn(true);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        assertThrows(AccessDeniedException.class, () -> ratingService.deleteRating(1L));
    }

    @Test
    void changeRating_changesBooksRating_WhenScoreWasAdded() {
        Book book = TestDataFactory.createTestBook();
        book.setScoresSum(10);

        ratingService.changeRating(book, 0, 2);

        assertEquals(2, book.getScoresNumber());
        assertEquals(12, book.getScoresSum());
        assertEquals(6.0F, book.getRating());
    }

    @Test
    void changeRating_changesBooksRating_WhenScoreWasDeletedAndThereIsNoMoreScores() {
        Book book = TestDataFactory.createTestBook();
        book.setScoresSum(10);

        ratingService.changeRating(book, 10, 0);

        assertEquals(0, book.getScoresNumber());
        assertEquals(0, book.getScoresSum());
        assertEquals(0, book.getRating());
    }

    @Test
    void changeRating_changesBooksRating_WhenScoreWasDeletedAndThereAreAnotherScores() {
        Book book = TestDataFactory.createTestBook();
        book.setScoresSum(12);
        book.setScoresNumber(2);

        ratingService.changeRating(book, 10, 0);

        assertEquals(1, book.getScoresNumber());
        assertEquals(2, book.getScoresSum());
        assertEquals(2.0F, book.getRating());
    }

    @Test
    void changeRating_changesBooksRating_WhenScoreWasEdited() {
        Book book = TestDataFactory.createTestBook();
        book.setScoresSum(12);
        book.setScoresNumber(2);

        ratingService.changeRating(book, 10, 6);

        assertEquals(2, book.getScoresNumber());
        assertEquals(8, book.getScoresSum());
        assertEquals(4.0F, book.getRating());
    }

    @Test
    void isItWrongUser_ReturnsFalse_WhenUserOwnRating() {
        User user = TestDataFactory.createTestUser();
        Rating rating = TestDataFactory.createTestRating();
        rating.setUser(user);

        when(userService.getLoggedUser()).thenReturn(user);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        boolean result = ratingService.isItWrongUser(1L);

        assertFalse(result);
    }

    @Test
    void isItWrongUser_ReturnsTrue_WhenUserDoesNotOwnRating() {
        User loggedUser = TestDataFactory.createTestUser();
        User ratingOwner = TestDataFactory.createAnotherTestUser();
        Rating rating = TestDataFactory.createTestRating();
        rating.setUser(ratingOwner);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        boolean result = ratingService.isItWrongUser(1L);

        assertTrue(result);
    }

    @Test
    void isItWrongRating_ReturnsFalse_WhenRatingHasReview() {
        Rating rating = TestDataFactory.createTestRating();
        rating.setReview("test");

        boolean result = ratingService.isItWrongRating(rating);

        assertFalse(result);
    }

    @Test
    void isItWrongRating_ReturnsFalse_WhenRatingHasScore() {
        Rating rating = TestDataFactory.createTestRating();
        rating.setScore(10);

        boolean result = ratingService.isItWrongRating(rating);

        assertFalse(result);
    }

    @Test
    void isItWrongRating_ReturnsFalse_WhenRatingHasReviewAndScore() {
        Rating rating = TestDataFactory.createTestRating();
        rating.setReview("test");
        rating.setScore(10);

        boolean result = ratingService.isItWrongRating(rating);

        assertFalse(result);
    }

    @Test
    void isItWrongRating_ReturnsTrue_WhenScoreIsMoreThan10() {
        Rating rating = TestDataFactory.createTestRating();
        rating.setScore(11);

        boolean result = ratingService.isItWrongRating(rating);

        assertTrue(result);
    }

    @Test
    void isItWrongRating_ReturnsTrue_WhenScoreIsLessThan0() {
        Rating rating = TestDataFactory.createTestRating();
        rating.setScore(-1);

        boolean result = ratingService.isItWrongRating(rating);

        assertTrue(result);
    }

    @Test
    void isItWrongRating_ReturnsTrue_WhenThereIsNoScoreAndNoReview() {
        Rating rating = TestDataFactory.createTestRating();

        boolean result = ratingService.isItWrongRating(rating);

        assertTrue(result);
    }

    @Test
    void canUserRateIt_ReturnsTrue_WhenUserDidNotAlreadyRateThisBook() {
        User user = TestDataFactory.createTestUser();
        Book book = TestDataFactory.createTestBook();

        when(ratingRepository.existsByUserAndBook(user, book)).thenReturn(false);

        boolean result = ratingService.canUserRateIt(user, book);

        assertTrue(result);
    }

    @Test
    void canUserRateIt_ReturnsFalse_WhenUserDidAlreadyRateThisBook() {
        User user = TestDataFactory.createTestUser();
        Book book = TestDataFactory.createTestBook();

        when(ratingRepository.existsByUserAndBook(user, book)).thenReturn(true);

        boolean result = ratingService.canUserRateIt(user, book);

        assertFalse(result);
    }
}