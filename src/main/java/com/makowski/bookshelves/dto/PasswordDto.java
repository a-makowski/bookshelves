package com.makowski.bookshelves.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PasswordDto {
    
    private String oldPassword;
    private String newPassword;
    private String repeatNewPassword;
}
