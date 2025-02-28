package com.example.Child.Growth.Tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.Child.Growth.Tracking.Model.User;
import com.example.Child.Growth.Tracking.Repository.UserRepository;
import com.example.Child.Growth.Tracking.ulti.UserRole;

@SpringBootApplication
@EntityScan("com.example.Child.Growth.Tracking")
@EnableJpaRepositories("com.example.Child.Growth.Tracking")
@EnableScheduling
public class ChildGrowthTrackingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChildGrowthTrackingApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setRole(UserRole.ADMIN);
				admin.setEmail("admin@example.com");
				admin.setFullName("Admin");
				admin.setPhoneNumber("0909090909");
				admin.setAddress("Hà Nội");
				userRepository.save(admin);
				System.out.println("Đã tạo tài khoản admin mặc định");
			}
		};
	}
}
