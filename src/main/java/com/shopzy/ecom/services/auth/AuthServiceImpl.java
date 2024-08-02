package com.shopzy.ecom.services.auth;

import com.shopzy.ecom.dto.SignupRequest;
import com.shopzy.ecom.dto.UserDTO;
import com.shopzy.ecom.entity.User;
import com.shopzy.ecom.enums.UserRole;
import com.shopzy.ecom.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public BCryptPasswordEncoder bCryptPasswordEncoder;

    //This method is dedicated only to create Users with Customer role
    public UserDTO createUser(SignupRequest signupRequest) {
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setName(signupRequest.getName());
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        User createdUser = userRepository.save(user);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(createdUser.getId());
        return  userDTO;
    }

    @Override
    public boolean hasUserWithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }

    @PostConstruct
    public void createAdmin() {
        User admin = userRepository.findByRole(UserRole.ADMIN);
        if(null == admin){
            User user = new User();
            user.setEmail("test@admin.com");
            user.setName("admin");
            user.setPassword(new BCryptPasswordEncoder().encode("admin123"));
            user.setRole(UserRole.ADMIN);
            userRepository.save(user);
        }
    }
}
