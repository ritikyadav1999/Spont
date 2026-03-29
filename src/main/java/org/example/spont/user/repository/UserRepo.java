package org.example.spont.user.repository;

import org.example.spont.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    @Query(value = """
           SELECT * FROM users
           WHERE phone = :identifier 
           OR email = :identifier
""" ,nativeQuery = true)
    Optional<User> findByIdentifier(String identifier);





}
