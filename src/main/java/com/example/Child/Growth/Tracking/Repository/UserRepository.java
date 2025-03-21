package com.example.Child.Growth.Tracking.Repository;

import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.ulti.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    List<User> findAll();
    void deleteById(Long id);
    User save(User user);
    Optional<User> findByPhoneNumber(String phoneNumber); 
    Optional<User> findByEmail(String email); 
    User findByFullName(String fullName);
    User findByRole(String role);

    // Thêm các phương thức mới
    Optional<User> findByUsernameAndIdNot(String username, Long id);
    Optional<User> findByEmailAndIdNot(String email, Long id);
    Optional<User> findByPhoneNumberAndIdNot(String phoneNumber, Long id);
    
    List<User> findByRole(UserRole role);
}
