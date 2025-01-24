package com.makowski.bookshelves.service;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.makowski.bookshelves.entity.Book;
import com.makowski.bookshelves.entity.Shelf;
import com.makowski.bookshelves.entity.User;
import com.makowski.bookshelves.exceptions.AccessDeniedException;
import com.makowski.bookshelves.exceptions.EntityNotFoundException;
import com.makowski.bookshelves.exceptions.ForbiddenNameException;
import com.makowski.bookshelves.exceptions.InvalidRequestException;
import com.makowski.bookshelves.exceptions.PermanentShelfException;
import com.makowski.bookshelves.repository.ShelfRepository;

import lombok.AllArgsConstructor;

@Service               
@AllArgsConstructor (onConstructor = @__(@Lazy))
public class ShelfService {                             
                                                        
    private ShelfRepository shelfRepository;
    private BookService bookService;
    private UserService userService;   

    public Shelf getShelf(Long id) {
        return shelfRepository.findById(id)     
            .orElseThrow(() -> new EntityNotFoundException(id, Shelf.class));
    } 

    public Shelf showShelf(Long id) {
        Shelf shelf = getShelf(id);
        if (shelf.getOwner().isPrivateProfile())
            if (isItWrongUser(id)) throw new AccessDeniedException();
        return shelf;
    }
    
    public Shelf createOwnShelf(String name) {
        if (name.isBlank()) throw new InvalidRequestException();
        if (isItProperName(name)) return createShelf(name, false, userService.getLoggedUser());
            else throw new ForbiddenNameException();                                
    }

    public Shelf createShelf(String name, boolean permanent, User user) {
        Shelf shelf = new Shelf();
        shelf.setName(name);
        shelf.setPermanent(permanent);
        shelf.setOwner(user);
        return saveShelf(shelf);
    }

    public Shelf saveShelf(Shelf shelf) {
        return shelfRepository.save(shelf);
    }

    public Shelf addToShelf(Long bookId, Long shelfId) {
        Shelf shelf = getShelf(shelfId);    
        if (isItWrongUser(shelfId)) throw new AccessDeniedException();
        List<Book> books = shelf.getBooks();
        Book book = bookService.getBook(bookId);
        if (books.contains(book)) throw new InvalidRequestException();
        books.add(book);
        shelf.setBooks(books);
        saveShelf(shelf);
        return shelf;

    }

    public void deleteShelf(Long shelfId) {
        if (!shelfRepository.existsById(shelfId)) throw new EntityNotFoundException(shelfId, Shelf.class);
        if (isItWrongUser(shelfId)) throw new AccessDeniedException();
        if (getShelf(shelfId).isPermanent()) throw new PermanentShelfException("delete");
        shelfRepository.deleteById(shelfId);
    }    

    public Shelf renameShelf(Long shelfId, String newName) {
        if (newName.isBlank()) throw new InvalidRequestException();
        if (isItWrongUser(shelfId)) throw new AccessDeniedException();
        Shelf shelf = getShelf(shelfId);
        if (shelf.isPermanent()) throw new PermanentShelfException("rename");
        if (!isItProperName(newName)) throw new ForbiddenNameException();
        shelf.setName(newName);
        return saveShelf(shelf);
    }

    public Shelf deleteBookFromShelf(Long bookId, Long shelfId) {
        if (isItWrongUser(shelfId)) throw new AccessDeniedException();
        Shelf shelf = getShelf(shelfId);
        List<Book> books = shelf.getBooks();
        Book book= bookService.getBook(bookId);
        if (!books.contains(book)) throw new EntityNotFoundException(bookId, shelfId);
        books.remove(book);
        shelf.setBooks(books);
        return saveShelf(shelf);
    }     

    public boolean isItProperName(String name) {
        for (Shelf shelf : userService.getLoggedUser().getShelves()) {
            if (shelf.getName().equalsIgnoreCase(name)) return false;
        }
        return true;
    } 
    
    public boolean isItWrongUser(Long shelfId) {
        return !(getShelf(shelfId).getOwner().equals(userService.getLoggedUser()));
    }
}