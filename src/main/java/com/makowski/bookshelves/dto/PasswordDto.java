package com.makowski.bookshelves.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDto {
    
    private String oldPassword;
    private String newPassword;
    private String repeatNewPassword;
}
