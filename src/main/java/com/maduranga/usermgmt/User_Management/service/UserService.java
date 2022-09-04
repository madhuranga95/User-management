package com.maduranga.usermgmt.User_Management.service;

import com.maduranga.usermgmt.User_Management.config.MessageStrings;
import com.maduranga.usermgmt.User_Management.dto.SignInDto;
import com.maduranga.usermgmt.User_Management.dto.SignInResponseDto;
import com.maduranga.usermgmt.User_Management.dto.SignUpResponseDto;
import com.maduranga.usermgmt.User_Management.dto.SignupDto;
import com.maduranga.usermgmt.User_Management.exceptions.AuthenticationFailException;
import com.maduranga.usermgmt.User_Management.exceptions.CustomException;
import com.maduranga.usermgmt.User_Management.model.AuthenticationToken;
import com.maduranga.usermgmt.User_Management.model.User;
import com.maduranga.usermgmt.User_Management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Service
public class UserService {

    final UserRepository userRepository;

    final AuthenticationService authenticationService;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public ResponseEntity<SignUpResponseDto> signup(SignupDto signupDto) throws CustomException {

        if (Objects.nonNull(userRepository.findByEmail(signupDto.getEmail()))) {
            throw new CustomException("User already exists");
        }

        //encrypt pwd
        String encryptedPassword = signupDto.getPassword();

        try {
            encryptedPassword = this.hashPassword(encryptedPassword);
        } catch (NoSuchAlgorithmException exception) {

            exception.printStackTrace();
            logger.error("Hashing password failed: {}", exception.getMessage());
        }

        User user = new User(signupDto.getFirstName(), signupDto.getLastName(), signupDto.getEmail(),
                encryptedPassword);

        try {
            // save user
            userRepository.save(user);

            //if success generate token for user
            final AuthenticationToken authenticationToken = new AuthenticationToken(user);

            //save token in db
            authenticationService.saveConfirmationToken(authenticationToken);

            SignUpResponseDto signUpResponseDto = new SignUpResponseDto("Success",
                    "User created successfully !");
            return new ResponseEntity<>(signUpResponseDto, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage());
        }

    }

    public ResponseEntity<SignInResponseDto> signIn(SignInDto dto) throws AuthenticationFailException, CustomException {

        //find user by email
        User user = userRepository.findByEmail(dto.getEmail());

        if (!Objects.nonNull(user)) {
            throw new AuthenticationFailException("USER NOT PRESENT");
        }

        try {
            // check pwd
            if (!user.getPassword().equals(hashPassword(dto.getPassword()))) {
                throw new AuthenticationFailException(MessageStrings.WRONG_PASSWORD);
            }
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
            logger.error("Hashing password failed {}", exception.getMessage());
        }

        AuthenticationToken token = authenticationService.getToken(user);

        if (!Objects.nonNull(token)) {
            throw new CustomException(MessageStrings.AUTH_TOEKN_NOT_PRESENT);
        }

        SignInResponseDto responseDto = new SignInResponseDto("Login Success", token.getToken());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter
                .printHexBinary(digest).toUpperCase();
    }
}
