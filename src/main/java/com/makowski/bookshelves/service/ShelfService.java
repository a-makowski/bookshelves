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
        if (shelf.getOwner().getPrivateProfile()) 
            if (!isItProperUser(id)) throw new AccessDeniedException();
        return shelf;
    }
    
    public Shelf createOwnShelf(String name) {
        if (name.isBlank()) throw new InvalidRequestException();
        if (isItProperName(name)) return createShelf(name, false, userService.getLoggedUser());
            else throw new ForbiddenNameException();                                
    }

    public Shelf createShelf(String name, Boolean permanent, User user) {
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
        if (isItProperUser(shelfId)) {   
            List<Book> books = shelf.getBooks();
            Book book = bookService.getBook(bookId);
            if (!books.contains(book)) {
                books.add(book);
                shelf.setBooks(books);
                saveShelf(shelf);
                return shelf;
            } else throw new InvalidRequestException();
        } else throw new AccessDeniedException();
    }

    public void deleteShelf(Long shelfId) {
        if (shelfRepository.existsById(shelfId)) {
            if (isItProperUser(shelfId)) {
                if (!getShelf(shelfId).getPermanent()) {
                     shelfRepository.deleteById(shelfId);
                } else throw new PermanentShelfException("delete"); 
            } else throw new AccessDeniedException();
        } else throw new EntityNotFoundException(shelfId, Shelf.class);
    }    

    public Shelf renameShelf(Long shelfId, String newName) {
        Shelf shelf = getShelf(shelfId);
        if (isItProperUser(shelfId)) {
            if (!shelf.getPermanent()) {
                if (isItProperName(newName)) {
                    shelf.setName(newName);
                    return saveShelf(shelf);
                } else throw new ForbiddenNameException();   
            } else throw new PermanentShelfException("rename"); 
        } else throw new AccessDeniedException();   
    }

    public Shelf deleteBookFromShelf(Long bookId, Long shelfId) {
        if (isItProperUser(shelfId)) {
            Shelf shelf = getShelf(shelfId);
            List<Book> books = shelf.getBooks();
            Book book= bookService.getBook(bookId);
            if (books.contains(book)) {
                books.remove(book);
                shelf.setBooks(books);
                return saveShelf(shelf);
            } else throw new EntityNotFoundException(bookId, shelfId);       
        } else throw new AccessDeniedException();                           
    }     

    public Boolean isItProperName(String name) {
        for (Shelf shelf : userService.getLoggedUser().getShelves()) {
            if (shelf.getName().equalsIgnoreCase(name)) return false;
        }
        return true;
    } 
    
    public boolean isItProperUser(Long shelfId) {
        return getShelf(shelfId).getOwner() == userService.getLoggedUser();
    }
}