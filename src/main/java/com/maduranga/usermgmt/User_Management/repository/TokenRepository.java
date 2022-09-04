package com.maduranga.usermgmt.User_Management.repository;

import com.maduranga.usermgmt.User_Management.model.AuthenticationToken;
import com.maduranga.usermgmt.User_Management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<AuthenticationToken, Integer> {

    AuthenticationToken findTokenByUser(User user);

    AuthenticationToken findTokenByToken(String token);

}
