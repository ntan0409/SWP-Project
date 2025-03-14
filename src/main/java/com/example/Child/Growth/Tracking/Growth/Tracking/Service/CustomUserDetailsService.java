package com.example.Child.Growth.Tracking.Service;

import com.example.Child.Growth.Tracking.Repository.UserRepository;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Child.Growth.Tracking.Model.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private User user;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // Đảm bảo inject PasswordEncoder

    public CustomUserDetailsService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;  // Khởi tạo PasswordEncoder
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Đang tìm user: " + username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                System.out.println("Không tìm thấy user: " + username);
                return new UsernameNotFoundException("User not found: " + username);
            });

        System.out.println("Tìm thấy user: " + user.getUsername());
        System.out.println("Pass: " + user.getPassword());

        String password = "a"; // Mật khẩu gốc
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("Encoded Password: " + encodedPassword);

        String rawPassword = "a";  // Mật khẩu gốc
        encodedPassword = passwordEncoder.encode(rawPassword);  // Mã hóa lại mật khẩu

        // Kiểm tra mật khẩu nhập vào với mật khẩu đã mã hóa
        boolean isPasswordMatch = passwordEncoder.matches(rawPassword, encodedPassword);
        if (isPasswordMatch) {
            System.out.println("Mật khẩu đúng");
        } else {
            System.out.println("Mật khẩu sai");
        }


        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))  // Phân quyền rõ ràng hơn
            .build();
    }

    public Long getId() {
        return user.getId();
    }

}
