package com.shopzy.ecom.controller;

import com.shopzy.ecom.Utils.JWTUtil;
import com.shopzy.ecom.dto.AuthenticationRequest;
import com.shopzy.ecom.dto.SignupRequest;
import com.shopzy.ecom.dto.UserDTO;
import com.shopzy.ecom.entity.User;
import com.shopzy.ecom.repository.UserRepository;
import com.shopzy.ecom.services.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@AllArgsConstructor

public class AuthController {

    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer " ;
    private  final AuthenticationManager authenticationManager;
    private  final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final AuthService authService;

    @PostMapping("/authenticate")
    public void createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
                                          HttpServletResponse response) throws IOException, JSONException {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()));
        }
        catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect Username or Password");
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        Optional<User> optionalUser = userRepository.findFirstByEmail(userDetails.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        if(optionalUser.isPresent()){
            response.getWriter().write(new JSONObject()
                    .put("userId",optionalUser.get().getId())
                    .put("role",optionalUser.get().getRole())
                    .toString()
            );
        }

        response.addHeader("Access-Control-Expose-Headers","Authorization");
        response.addHeader("Access-Control-Allow-Headers","Authorization, X-PINGOTHER, Origin"+
                "X-Requested-With, Content-Type,Accept.X-Custom-header");
        response.addHeader(HEADER_STRING,TOKEN_PREFIX+jwt);

    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> createUser(@RequestBody SignupRequest signupRequest) {
        if(authService.hasUserWithEmail(signupRequest.getEmail())){
            return new ResponseEntity<>("User Already Exist with the given Email", HttpStatus.NOT_ACCEPTABLE);
        }
        UserDTO userDTO = authService.createUser(signupRequest);
        return new ResponseEntity<>(userDTO,HttpStatus.CREATED);
    }
}
