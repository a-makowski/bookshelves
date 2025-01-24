package com.makowski.bookshelves.service;

import com.makowski.bookshelves.entity.Book;
import com.makowski.bookshelves.entity.Rating;
import com.makowski.bookshelves.entity.User;
import com.makowski.bookshelves.exceptions.EntityNotFoundException;
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

    }

    @Test
    void createRating_ReturnsRatingAndDoesNotChangeBooksRate_WhenRatingWithoutScoreSuccessfullyCreated() {

    }

    @Test
    void createRating_ThrowsException_WhenRatingFromThisUserForThisBookAlreadyExists() {

    }

    @Test
    void createRating_ThrowsException_WhenRatingIsIncorrect() {

    }

    @Test
    void updateRating_ReturnsEditedRatingAndChangeBooksRate_WhenScoreWasUpdated() {

    }

    @Test
    void updateRating_ReturnsEditedRatingAndDoesNotChangeBooksRate_WhenScoreWasNotUpdated() {

    }

    @Test
    void updateRating_ThrowsException_WhenRatingDoesNotExist() {

    }

    @Test
    void updateRating_ThrowsException_WhenUserIsNotOwner() {

    }

    @Test
    void updateRating_ThrowsException_WhenRatingIsNotValid() {

    }

    @Test
    void deleteRating_DeletesRatingAndChangesBooksRate_WhenRatingContainsScore() {

    }

    @Test
    void deleteRating_DeletesRatingAndDoesNotChangeBookRate_WhenRatingDoesNotContainScore() {

    }

    @Test
    void deleteRating_ThrowsException_WhenRatingDoesNotExist() {

    }

    @Test
    void deleteRating_ThrowsException_WhenUserDoesNotOwnRating() {

    }

    @Test
    void changeRating_changesBooksRating_WhenScoreWasAdded() {

    }

    @Test
    void changeRating_changesBooksRating_WhenScoreWasDeletedAndThereIsNoMoreScores() {

    }

    @Test
    void changeRating_changesBooksRating_WhenScoreWasDeletedAndThereAreAnotherScores() {

    }

    @Test
    void changeRating_changesBooksRating_WhenScoreWasEdited() {

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