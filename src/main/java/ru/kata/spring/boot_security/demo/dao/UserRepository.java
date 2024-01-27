package ru.kata.spring.boot_security.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    void deleteUserByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    User findByUsername(String username);
}