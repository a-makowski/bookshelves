package com.makowski.bookshelves.service;

import com.makowski.bookshelves.dto.PasswordDto;
import com.makowski.bookshelves.dto.UserDto;
import com.makowski.bookshelves.entity.User;
import com.makowski.bookshelves.exceptions.AccessDeniedException;
import com.makowski.bookshelves.exceptions.EntityNotFoundException;
import com.makowski.bookshelves.exceptions.ForbiddenNameException;
import com.makowski.bookshelves.exceptions.PasswordNotEqualsException;
import com.makowski.bookshelves.repository.UserRepository;
import com.makowski.bookshelves.testutils.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;
    @Mock
    BookService bookService;
    @Mock
    ShelfService shelfService;
    @Mock
    UserRepository userRepository;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void createUser_ReturnsUser_WhenUserSuccessfullyCreated() {
        User user = TestDataFactory.createTestUser();
        user.setEmail("email");
        user.setPassword("password");

        when(userRepository.existsByUsernameIgnoreCase("username1")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("email")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("password")).thenReturn("encodedPassword");

        User result = userService.createUser(user);

        verify(userRepository).save(user);
        verify(shelfService).createShelf("Want read", true, user);
        assertEquals("encodedPassword", result.getPassword());
    }

    @Test
    void createUser_ThrowsException_WhenUsernameAlreadyTaken() {
        User user = TestDataFactory.createTestUser();

        when(userRepository.existsByUsernameIgnoreCase("username1")).thenReturn(true);

        assertThrows(ForbiddenNameException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_ThrowsException_WhenEmailIsAlreadyInDatabase() {
        User user = TestDataFactory.createTestUser();
        user.setEmail("email");

        when(userRepository.existsByUsernameIgnoreCase("username1")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("email")).thenReturn(true);

        assertThrows(ForbiddenNameException.class, () -> userService.createUser(user));
    }

    @Test
    void getUser_ReturnsUser_WhenUserExists() {
        User user = TestDataFactory.createTestUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUser(1L);

        assertEquals(user, result);
    }

    @Test
    void getUser_ThrowsException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(1L));
    }

    @Test
    void findByUsername_ReturnsUser_WhenUserExists() {
        User user = new User();
        user.setUsername("TestUser");

        when(userRepository.findByUsername("TestUser")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("TestUser");

        assertEquals(user, result);
    }

    @Test
    void findByUsername_ThrowsException_WhenUserDoesNotExist() {
        when(userRepository.findByUsername("TestUser")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findByUsername("TestUser"));
    }

    @Test
    void changePassword_ChangesPassword_WhenPasswordsAreCorrect() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken("username1", null);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        User user = TestDataFactory.createTestUser();
        user.setPassword("oldPassword");
        PasswordDto passwordDto = new PasswordDto("oldPassword", "newPassword", "newPassword");

        when(userRepository.findByUsername("username1")).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())).thenReturn(true);
        when(bCryptPasswordEncoder.encode(passwordDto.getNewPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        userService.changePassword(passwordDto);

        verify(userRepository).save(user);
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void changePassword_ThrowsException_WhenOldPasswordDoesNotMatch() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken("username1", null);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        User user = TestDataFactory.createTestUser();
        user.setPassword("password");
        PasswordDto passwordDto = new PasswordDto("oldPassword", "newPassword", "newPassword");

        when(userRepository.findByUsername("username1")).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> userService.changePassword(passwordDto));
    }

    @Test
    void changePassword_ThrowsException_WhenNewPasswordsDoNotMatch() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken("username1", null);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        User user = TestDataFactory.createTestUser();
        user.setPassword("oldPassword");
        PasswordDto passwordDto = new PasswordDto("oldPassword", "newPassword", "test");

        when(userRepository.findByUsername("username1")).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())).thenReturn(true);

        assertThrows(PasswordNotEqualsException.class, () -> userService.changePassword(passwordDto));
    }

    @Test
    void getUserDto_ReturnsUserDto_WhenUserExists() {
        User user = TestDataFactory.createTestUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserDto(1L);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    void deleteUser_DeletesUser_WhenUserExists() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken("username1", null);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        User user = TestDataFactory.createTestUser();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findByUsername("username1")).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ThrowsException_WhenUserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void deleteUser_ThrowsException_WhenUserHasNoPermission() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken("username2", null);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        User loggedUser = TestDataFactory.createAnotherTestUser();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findByUsername("username2")).thenReturn(Optional.of(loggedUser));

        assertThrows(AccessDeniedException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void changePrivacyStatus_ReturnsEditedUser_WhenUserExists() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken("username1", null);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        User user = TestDataFactory.createTestUser();

        when(userRepository.findByUsername("username1")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.changePrivacyStatus();

        assertTrue(result.isPrivateProfile());
    }

    @Test
    void getUsersLibrary_ReturnsShelves_WhenProfileIsPrivateAndUserIsOwner() {

    }

    @Test
    void getUsersLibrary_ReturnsShelves_WhenProfileIsPublicAndUserIsOwner() {

    }

    @Test
    void getUsersLibrary_ReturnsShelves_WhenProfileIsPublicAndUserIsNotOwner() {

    }

    @Test
    void getUsersLibrary_ThrowsException_WhenProfileIsPrivateAndUserIsNotOwner () {

    }

    @Test
    void getUsersRatings_ReturnsRatings_WhenUserExists() {

    }

    @Test
    void showUsersRatings_ReturnRatings_WhenProfileIsPrivateAndUserIsOwner() {

    }

    @Test
    void showUsersRatings_ReturnRatings_WhenProfileIsPublicAndUserIsOwner() {

    }

    @Test
    void showUsersRatings_ReturnRatings_WhenProfileIsPublicAndUserIsNotOwner() {

    }

    @Test
    void showUsersRatings_ThrowsException_WhenProfileIsPrivateAndUserIsNotOwner() {

    }

    @Test
    void deleteNowReadingStatus_SetsNowReadingStatusAsNull_WhenUserIsLoggedIn() {

    }

    @Test
    void findUser_ReturnsListOfUserDto_WhenUsersAreFound() {

    }

    @Test
    void findUser_ThrowsException_WhenSearchPhraseIsBlank() {

    }

    @Test
    void findUser_ThrowsException_WhenSearchPhraseIsEmpty() {

    }

    @Test
    void findUser_ThrowsException_WhenNothingFound() {

    }

    @Test
    void setAsNowReading_SetsNewStatus_WhenBookExists() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken("username1", null);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        User user = TestDataFactory.createTestUser();

        when(bookService.existsById(1L)).thenReturn(true);
        when(userRepository.findByUsername("username1")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.setAsNowReading(1L);

        verify(userRepository).save(user);
        assertEquals(1L, result.getNowReading());
    }

    @Test
    void setAsNowReading_ThrowsException_WhenBookDoesNotExists() {
        when(bookService.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.setAsNowReading(1L));
    }
}