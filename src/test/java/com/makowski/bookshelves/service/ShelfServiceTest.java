package com.makowski.bookshelves.service;

import com.makowski.bookshelves.entity.Book;
import com.makowski.bookshelves.entity.Shelf;
import com.makowski.bookshelves.entity.User;
import com.makowski.bookshelves.exceptions.*;
import com.makowski.bookshelves.repository.ShelfRepository;
import com.makowski.bookshelves.testutils.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @InjectMocks
    ShelfService shelfService;
    @Mock
    ShelfRepository shelfRepository;
    @Mock
    BookService bookService;
    @Mock
    UserService userService;

    @Test
    void getShelf_ReturnsShelf_WhenShelfExists() {
        Shelf shelf = TestDataFactory.createTestShelf();

        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));

        Shelf result = shelfService.getShelf(3L);

        assertEquals(shelf, result);
    }

    @Test
    void getShelf_ThrowsException_WhenShelfDoesNotExist() {
        when(shelfRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> shelfService.getShelf(1L));
    }

    @Test
    void showShelf_ReturnsShelf_WhenOwnerIsPublic() {
        User shelfOwner = TestDataFactory.createAnotherTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        shelf.setOwner(shelfOwner);

        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));

        Shelf result = shelfService.showShelf(3L);

        assertEquals(shelf, result);
    }

    @Test
    void showShelf_ReturnsShelf_WhenOwnerIsPrivateAndUserIsOwner() {
        User shelfOwner = TestDataFactory.createTestUser();
        shelfOwner.setPrivateProfile(true);
        Shelf shelf = TestDataFactory.createTestShelf();
        shelf.setOwner(shelfOwner);

        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));
        when(userService.getLoggedUser()).thenReturn(shelfOwner);

        Shelf result = shelfService.showShelf(3L);

        assertEquals(shelf, result);
    }

    @Test
    void showShelf_ThrowsException_WhenOwnerIsPrivateAndUserIsNotOwner() {
        User loggedUser = TestDataFactory.createTestUser();
        User shelfOwner = TestDataFactory.createAnotherTestUser();
        shelfOwner.setPrivateProfile(true);
        Shelf shelf = TestDataFactory.createTestShelf();
        shelf.setOwner(shelfOwner);

        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));
        when(userService.getLoggedUser()).thenReturn(loggedUser);

        assertThrows(AccessDeniedException.class, () -> shelfService.showShelf(3L));
    }

    @Test
    void createOwnShelf_ReturnsShelf_WhenSuccessfullyCreated() {
        User user = TestDataFactory.createTestUser();
        user.setShelves(TestDataFactory.createTestDefaultLibrary());
        Shelf shelf = new Shelf();
        shelf.setName("New Shelf");
        shelf.setPermanent(false);
        shelf.setOwner(user);

        when(userService.getLoggedUser()).thenReturn(user);
        when(shelfRepository.save(shelf)).thenReturn(shelf);

        Shelf result = shelfService.createOwnShelf("New Shelf");

        assertEquals("New Shelf", result.getName());
        assertEquals(user, result.getOwner());
        assertFalse(result.isPermanent());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void createOwnShelf_ThrowsException_WhenNameIsBlank() {
        assertThrows(InvalidRequestException.class, () -> shelfService.createOwnShelf(""));
    }

    @Test
    void createOwnShelf_ThrowsException_WhenShelfAlreadyExistInLibrary() {
        User user = TestDataFactory.createTestUser();
        user.setShelves(TestDataFactory.createTestDefaultLibrary());

        when(userService.getLoggedUser()).thenReturn(user);

        assertThrows(ForbiddenNameException.class, () -> shelfService.createOwnShelf("HAVE READ"));
    }

    @Test
    void createShelf_ReturnsShelf_WhenShelfSuccessfullyCreated() {
        Shelf shelf = new Shelf();
        User user = TestDataFactory.createTestUser();
        shelf.setName("Test shelf");
        shelf.setPermanent(false);
        shelf.setOwner(user);

        when(shelfRepository.save(shelf)).thenReturn(shelf);

        Shelf result = shelfService.createShelf(shelf.getName(), shelf.isPermanent(), shelf.getOwner());

        assertEquals(user, result.getOwner());
        assertEquals(shelf.getName(), result.getName());
        assertEquals(shelf.isPermanent(), result.isPermanent());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void addToShelf_ReturnsUpdatedShelf_WhenBookSuccessfullyAddedToShelf() {
        User user = TestDataFactory.createTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        Book book = TestDataFactory.createTestBook();
        shelf.setOwner(user);

        when(userService.getLoggedUser()).thenReturn(user);
        when(bookService.getBook(1L)).thenReturn(book);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));
        when(shelfRepository.save(shelf)).thenReturn(shelf);

        Shelf result = shelfService.addToShelf(1L, 3L);

        assertEquals(shelf.getName(), result.getName());
        assertEquals(user, result.getOwner());
        assertEquals(book, result.getBooks().get(0));
        verify(shelfRepository).save(shelf);
    }

    @Test
    void addToShelf_ThrowsException_WhenUserDoesNotOwnShelf() {
        User loggedUser = TestDataFactory.createTestUser();
        User shelfOwner = TestDataFactory.createAnotherTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        shelf.setOwner(shelfOwner);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));

        assertThrows(AccessDeniedException.class, () -> shelfService.addToShelf(1L, 3L));

    }

    @Test
    void addToShelf_ThrowsException_WhenThisBookAlreadyExistOnThisShelf() {
        Book book = TestDataFactory.createTestBook();
        User user = TestDataFactory.createTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();

        shelf.getBooks().add(book);
        shelf.setOwner(user);

        when(userService.getLoggedUser()).thenReturn(user);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));
        when(bookService.getBook(1L)).thenReturn(book);

        assertThrows(InvalidRequestException.class, () -> shelfService.addToShelf(1L, 3L));
    }

    @Test
    void deleteShelf_DeletesShelf_WhenPermitted() {
        User user = TestDataFactory.createTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        shelf.setOwner(user);

        when(shelfRepository.existsById(3L)).thenReturn(true);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));
        when(userService.getLoggedUser()).thenReturn(user);

        shelfService.deleteShelf(3L);
        verify(shelfRepository).deleteById(3L);
    }

    @Test
    void deleteShelf_ThrowsException_WhenShelfDoesNotExist() {
        when(shelfRepository.existsById(3L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> shelfService.deleteShelf(3L));
    }

    @Test
    void deleteShelf_ThrowsException_WhenUserDoesNotOwnShelf() {
        User loggedUser = TestDataFactory.createTestUser();
        User shelfOwner = TestDataFactory.createAnotherTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        shelf.setOwner(shelfOwner);

        when(shelfRepository.existsById(3L)).thenReturn(true);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));
        when(userService.getLoggedUser()).thenReturn(loggedUser);

        assertThrows(AccessDeniedException.class, () -> shelfService.deleteShelf(3L));
    }

    @Test
    void deleteShelf_ThrowsException_WhenShelfIsPermanent() {
        User user = TestDataFactory.createTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        shelf.setOwner(user);
        shelf.setPermanent(true);

        when(shelfRepository.existsById(3L)).thenReturn(true);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));
        when(userService.getLoggedUser()).thenReturn(user);

        assertThrows(PermanentShelfException.class, () -> shelfService.deleteShelf(3L));
    }

    @Test
    void renameShelf_ReturnsShelf_WhenSuccessfullyRenamed() {
        User user = TestDataFactory.createTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        user.getShelves().add(shelf);
        shelf.setOwner(user);

        when(userService.getLoggedUser()).thenReturn(user);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));
        when(shelfRepository.save(shelf)).thenReturn(shelf);

        Shelf result = shelfService.renameShelf(3L, "New name");

        assertEquals(shelf.getId(), result.getId());
        assertEquals("New name", result.getName());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void renameShelf_ThrowsException_WhenShelfNameIsBlank() {
        assertThrows(InvalidRequestException.class, () -> shelfService.renameShelf(3L,"  "));
    }

    @Test
    void renameShelf_ThrowsException_WhenUserDoesNotOwnShelf() {
        User loggedUser = TestDataFactory.createTestUser();
        User owner = TestDataFactory.createAnotherTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        shelf.setOwner(owner);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));

        assertThrows(AccessDeniedException.class, () -> shelfService.renameShelf(3L, "New name"));
    }

    @Test
    void renameShelf_ThrowsException_WhenShelfIsPermanent() {
        User user = TestDataFactory.createTestUser();
        user.setShelves(TestDataFactory.createTestDefaultLibrary());
        user.getShelves().get(0).setOwner(user);

        when(userService.getLoggedUser()).thenReturn(user);
        when(shelfRepository.findById(1L)).thenReturn(Optional.of(user.getShelves().get(0)));

        assertThrows(PermanentShelfException.class, () -> shelfService.renameShelf(1L, "New name"));
    }

    @Test
    void renameShelf_ThrowsException_WhenShelfAlreadyExistInLibrary() {
        User user = TestDataFactory.createTestUser();
        List<Shelf> library = TestDataFactory.createTestDefaultLibrary();
        library.add(TestDataFactory.createTestShelf());
        library.get(2).setOwner(user);
        user.setShelves(library);

        when(userService.getLoggedUser()).thenReturn(user);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(user.getShelves().get(2)));

        assertThrows(ForbiddenNameException.class, () -> shelfService.renameShelf(3L, "HAVE READ"));
    }

    @Test
    void deleteBookFromShelf_ReturnsUpdatedShelf_WhenSuccessfullyUpdated() {
        Book book = TestDataFactory.createTestBook();
        User user = TestDataFactory.createTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        shelf.setOwner(user);
        shelf.getBooks().add(book);

        when(userService.getLoggedUser()).thenReturn(user);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));
        when(bookService.getBook(1L)).thenReturn(book);
        when(shelfRepository.save(shelf)).thenReturn(shelf);

        Shelf result = shelfService.deleteBookFromShelf(1L, 3L);

        assertEquals(0, result.getBooks().size());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void deleteBookFromShelf_ThrowsException_WhenUserDoesNotOwnShelf() {
        User loggedUser = TestDataFactory.createTestUser();
        User shelfOwner = TestDataFactory.createAnotherTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        shelf.setOwner(shelfOwner);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));

        assertThrows(AccessDeniedException.class, () -> shelfService.deleteBookFromShelf(1L, 3L));
    }

    @Test
    void deleteBookFromShelf_ThrowsException_WhenBookDoesNotExistOnThisShelf() {
        Book book = TestDataFactory.createTestBook();
        User user = TestDataFactory.createTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        shelf.setOwner(user);

        when(userService.getLoggedUser()).thenReturn(user);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));
        when(bookService.getBook(1L)).thenReturn(book);

        assertThrows(EntityNotFoundException.class, () -> shelfService.deleteBookFromShelf(1L, 3L));
    }

    @Test
    void isItProperName_ReturnsTrue_WhenUserDoesNotHaveShelfWithThisName() {
        User user = TestDataFactory.createTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        user.getShelves().add(shelf);

        when(userService.getLoggedUser()).thenReturn(user);

        boolean result = shelfService.isItProperName("NEW SHELF");

        assertTrue(result);
    }

    @Test
    void isItProperName_ReturnsFalse_WhenUserAlreadyHaveShelfWithThisName() {
        User user = TestDataFactory.createTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        user.getShelves().add(shelf);

        when(userService.getLoggedUser()).thenReturn(user);

        boolean result = shelfService.isItProperName("TEST SHELF");

        assertFalse(result);
    }

    @Test
    void isItWrongUser_ReturnsFalse_WhenUserOwnShelf() {
        User user = TestDataFactory.createTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        shelf.setOwner(user);

        when(userService.getLoggedUser()).thenReturn(user);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));

        boolean result = shelfService.isItWrongUser(3L);

        assertFalse(result);
    }

    @Test
    void isItWrongUser_returnsTrue_WhenUserDoesNotOwnShelf() {
        User loggedUser = TestDataFactory.createTestUser();
        User shelfOwner = TestDataFactory.createAnotherTestUser();
        Shelf shelf = TestDataFactory.createTestShelf();
        shelf.setOwner(shelfOwner);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(shelfRepository.findById(3L)).thenReturn(Optional.of(shelf));

        boolean result = shelfService.isItWrongUser(3L);

        assertTrue(result);
    }
}