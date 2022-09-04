package com.maduranga.usermgmt.User_Management.controller;

import com.maduranga.usermgmt.User_Management.dto.SignInDto;
import com.maduranga.usermgmt.User_Management.dto.SignInResponseDto;
import com.maduranga.usermgmt.User_Management.dto.SignUpResponseDto;
import com.maduranga.usermgmt.User_Management.dto.SignupDto;
import com.maduranga.usermgmt.User_Management.exceptions.AuthenticationFailException;
import com.maduranga.usermgmt.User_Management.exceptions.CustomException;
import com.maduranga.usermgmt.User_Management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;


    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signup(@RequestBody SignupDto signupDto) throws CustomException {
        return userService.signup(signupDto);
    }

    @PostMapping("/signIn")
    public ResponseEntity<SignInResponseDto> login(@RequestBody SignInDto signInDto) throws CustomException, AuthenticationFailException {
        return userService.signIn(signInDto);
    }

}
