package com.makowski.bookshelves.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.makowski.bookshelves.dto.PasswordDto;
import com.makowski.bookshelves.dto.UserDto;
import com.makowski.bookshelves.entity.Rating;
import com.makowski.bookshelves.entity.Shelf;
import com.makowski.bookshelves.entity.User;
import com.makowski.bookshelves.exceptions.AccessDeniedException;
import com.makowski.bookshelves.exceptions.EntityNotFoundException;
import com.makowski.bookshelves.exceptions.ForbiddenNameException;
import com.makowski.bookshelves.exceptions.InvalidRequestException;
import com.makowski.bookshelves.exceptions.PasswordNotEqualsException;
import com.makowski.bookshelves.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    
    private BookService bookService;
    private ShelfService shelfService;     
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User createUser(User user) {
        if(userRepository.existsByUsernameIgnoreCase(user.getUsername())) throw new ForbiddenNameException();
        if(userRepository.existsByEmailIgnoreCase(user.getEmail())) throw new ForbiddenNameException(user.getEmail());
        String encode = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encode);      
        saveUser(user);
        shelfService.createShelf("Want read", true, user);
        shelfService.createShelf("Have read", true, user);
        return user;
    }

    public User saveUser(User user) {       
        return userRepository.save(user);
    }

    public User getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) return user.get();
            else throw new EntityNotFoundException(id, User.class);
    }

    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) return user.get();
            else throw new EntityNotFoundException(username);
    }

    public void changePassword(PasswordDto password) {      
        User user = getLoggedUser();
        if (bCryptPasswordEncoder.matches(password.getOldPassword(), user.getPassword())) {
            if (password.getNewPassword().equals(password.getRepeatNewPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(password.getNewPassword()));
                userRepository.save(user);
            } else throw new PasswordNotEqualsException();
        } else throw new AccessDeniedException();
    }

    public User getLoggedUser() {
        return findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public UserDto getUserDto(Long id) {
        User user = getUser(id);
        return new UserDto(id, user.getUsername());
    }

    public void deleteUser(Long id) {      
        if (userRepository.existsById(id)) {
            if (getLoggedUser().getId().equals(id)) {
                userRepository.deleteById(id);
            } else throw new AccessDeniedException(); 
        } else throw new EntityNotFoundException(id, User.class);
    }

    public User changePrivacyStatus() {
        User user = getLoggedUser();
        user.setPrivateProfile(!user.isPrivateProfile());
        return saveUser(user);
    }

    public List<Shelf> getUsersLibrary(Long id) {
        User user = getUser(id);
        if (user.isPrivateProfile())
            if (getLoggedUser() != user) throw new AccessDeniedException();
        return user.getShelves();
    }

    public List<Rating> getUsersRatings(Long id) {
        return getUser(id).getRatings();
    }
     
    public List<Rating> showUsersRatings(Long id) {
        User user = getUser(id);
        if (user.isPrivateProfile())
            if (getLoggedUser() != user) throw new AccessDeniedException();
        if (user.getRatings() == null) throw new EntityNotFoundException();               
        return user.getRatings();
    }

    public User deleteNowReadingStatus() {
        User user = getLoggedUser();
        user.setNowReading(null);          
        return saveUser(user);
    }

    public User setAsNowReading(Long bookId) {
        if (bookService.existsById(bookId)) {
            User user = getLoggedUser();
            user.setNowReading(bookId);
            return saveUser(user);
        } else throw new EntityNotFoundException ();
    }

    public List<UserDto> findUser(String phrase) {   
        if (phrase.isBlank()) throw new InvalidRequestException();
        List<UserDto> users = new ArrayList<>();    
        for (User user : userRepository.findAll()) {
            if (user.getUsername().toUpperCase().contains(phrase.toUpperCase())) {
                users.add(getUserDto(user.getId()));
            }
        }
        if (users.isEmpty()) throw new EntityNotFoundException();
            else return users;
    }
}