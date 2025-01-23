package com.makowski.bookshelves.service;

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
        assertFalse(result.getPermanent());
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

    }

    @Test
    void addToShelf_ReturnsShelf_WhenBookSuccessfullyAddedToShelf() {

    }

    @Test
    void addToShelf_ThrowsException_WhenUserDoesNotOwnShelf() {

    }

    @Test
    void addToShelf_ThrowsException_WhenThisBookAlreadyExistOnThisShelf() {

    }

    @Test
    void deleteShelf_DeletesShelf_WhenPermitted() {

    }

    @Test
    void deleteShelf_ThrowsException_WhenShelfDoesNotExist() {

    }

    @Test
    void deleteShelf_ThrowsException_WhenUserDoesNotOwnShelf() {

    }

    @Test
    void deleteShelf_ThrowsException_WhenShelfIsPermanent() {

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
    void deleteBookFromShelf_ReturnsShelf_WhenSuccessfullyUpdated() {

    }

    @Test
    void deleteBookFromShelf_ThrowsException_WhenUserDoesNotOwnShelf() {

    }

    @Test
    void deleteBookFromShelf_ThrowsException_WhenBookDoesNotExistOnThisShelf() {

    }

    @Test
    void isItProperName_ReturnsTrue_WhenUserDoesNotHaveShelfWithThisName() {

    }

    @Test
    void isItProperName_ReturnsFalse_WhenUserAlreadyHaveShelfWithThisName() {

    }

    @Test
    void isItProperUser_ReturnsTrue_WhenUserOwnShelf() {

    }

    @Test
    void isItProperUser_returnsFalse_WhenUserDoesNotOwnShelf() {

    }
}