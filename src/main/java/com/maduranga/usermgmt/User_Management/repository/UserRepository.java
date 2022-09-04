package com.maduranga.usermgmt.User_Management.repository;

import com.maduranga.usermgmt.User_Management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

}
