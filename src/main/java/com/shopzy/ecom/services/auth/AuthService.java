package com.shopzy.ecom.services.auth;

import com.shopzy.ecom.dto.SignupRequest;
import com.shopzy.ecom.dto.UserDTO;

public interface AuthService {
    UserDTO createUser(SignupRequest signupRequest);

    boolean hasUserWithEmail(String email);
}
