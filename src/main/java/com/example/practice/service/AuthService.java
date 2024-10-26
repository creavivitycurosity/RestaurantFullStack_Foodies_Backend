package com.example.practice.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.practice.dto.ReqRes;
import com.example.practice.entity.OurUsers;
import com.example.practice.repository.OurUserRepo;

import java.util.HashMap;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private OurUserRepo ourUserRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

//    public ReqRes signUp(ReqRes registrationRequest){
//        ReqRes resp = new ReqRes();
//        try {
//            OurUsers ourUsers = new OurUsers();
//            ourUsers.setEmail(registrationRequest.getEmail());
//            ourUsers.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
//            ourUsers.setRole("USER");
//            OurUsers ourUserResult = ourUserRepo.save(ourUsers);
//            if (ourUserResult != null && ourUserResult.getId()>0) {
//                resp.setOurUsers(ourUserResult);
//                resp.setMessage("User Saved Successfully");
//                resp.setStatusCode(200);
//            }
//        }catch (Exception e){
//            resp.setStatusCode(500);
//            resp.setError(e.getMessage());
//        }
//        return resp;
//    }
//
//    public ReqRes sellerSignUp(ReqRes registrationRequest){
//        ReqRes resp = new ReqRes();
//        try {
//            OurUsers ourUsers = new OurUsers();
//            ourUsers.setEmail(registrationRequest.getEmail());
//            ourUsers.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
//            ourUsers.setRole("SELLER");
//            OurUsers ourUserResult = ourUserRepo.save(ourUsers);
//            if (ourUserResult != null && ourUserResult.getId()>0) {
//                resp.setOurUsers(ourUserResult);
//                resp.setMessage("Seller Saved Successfully");
//                resp.setStatusCode(200);
//            }
//        }catch (Exception e){
//            resp.setStatusCode(500);
//            resp.setError(e.getMessage());
//        }
//        return resp;
//    }
//
//    public ReqRes signIn(ReqRes signinRequest){
//        ReqRes response = new ReqRes();
//
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
//            var user = ourUserRepo.findByEmail(signinRequest.getEmail()).orElseThrow();
//            System.out.println("USER IS: "+ user);
//            var jwt = jwtUtils.generateToken(user);
//            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
//            response.setStatusCode(200);
//            response.setToken(jwt);
//            response.setRefreshToken(refreshToken);
//            response.setExpirationTime("24Hr");
//            response.setMessage("Successfully Signed In");
//        } catch (Exception e) {
//            response.setStatusCode(500);
//            response.setError(e.getMessage());
//        }
//        return response;
//    }
//
//    public ReqRes sellerSignIn(ReqRes signinRequest){
//        ReqRes response = new ReqRes();
//
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
//            var user = ourUserRepo.findByEmail(signinRequest.getEmail()).orElseThrow();
//            if (!"SELLER".equals(user.getRole())) {
//                response.setStatusCode(403);
//                response.setError("Access denied");
//                return response;
//            }
//            System.out.println("SELLER IS: "+ user);
//            var jwt = jwtUtils.generateToken(user);
//            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
//            response.setStatusCode(200);
//            response.setToken(jwt);
//            response.setRefreshToken(refreshToken);
//            response.setExpirationTime("24Hr");
//            response.setMessage("Successfully Signed In");
//        } catch (Exception e) {
//            response.setStatusCode(500);
//            response.setError(e.getMessage());
//        }
//        return response;
//    }
    
//    public ReqRes signUp(ReqRes registrationRequest){
//        ReqRes resp = new ReqRes();
//        try {
//            OurUsers ourUsers = new OurUsers();
//            ourUsers.setEmail(registrationRequest.getEmail());
//            ourUsers.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
//            ourUsers.setRole("USER");
//            ourUsers.setEnabled(true); // Set enabled to true by default
//            ourUsers.setUsername(registrationRequest.getUsername());
//            ourUsers.setAddress(registrationRequest.getAddress()); // Add this line
//          
//            OurUsers ourUserResult = ourUserRepo.save(ourUsers);
//            if (ourUserResult != null && ourUserResult.getId() > 0) {
//                resp.setOurUsers(ourUserResult);
//                resp.setMessage("User Saved Successfully");
//                resp.setStatusCode(200);
//            }
//        } catch (Exception e) {
//            resp.setStatusCode(500);
//            resp.setError(e.getMessage());
//        }
//        return resp;
//    }
    
    
    

    public ReqRes signUp(ReqRes registrationRequest){
        ReqRes resp = new ReqRes();
        try {
            OurUsers ourUsers = new OurUsers();
            ourUsers.setEmail(registrationRequest.getEmail());
            ourUsers.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            ourUsers.setRole("USER");
            ourUsers.setEnabled(true); // Set enabled to true by default
            ourUsers.setName(registrationRequest.getName());
          
            OurUsers ourUserResult = ourUserRepo.save(ourUsers);
            if (ourUserResult != null && ourUserResult.getId() > 0) {
                resp.setOurUsers(ourUserResult);
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
//
//    public ReqRes sellerSignUp(String email, String password, String restaurantName, MultipartFile image) {
//                ReqRes resp = new ReqRes();
//        try {
//            OurUsers ourUsers = new OurUsers();
//            ourUsers.setEmail(email);
//            ourUsers.setPassword(passwordEncoder.encode(password));
//            ourUsers.setRole("SELLER");
//            ourUsers.setEnabled(true); // Set enabled to true by default
//            ourUsers.setRestaurantName(restaurantName);  // Set restaurantName
////            ourUsers.setImage(registrationRequest.getImage()); // Use the getter method
//            if (image != null && !image.isEmpty()) {
//                ourUsers.setImage(image.getBytes()); // Save the image bytes
//            }
//            OurUsers ourUserResult = ourUserRepo.save(ourUsers);
//                resp.setOurUsers(ourUserResult);
//                resp.setMessage("Seller Saved Successfully");
//                resp.setStatusCode(200);
//            
//        } catch (Exception e) {
//            resp.setStatusCode(500);
//            resp.setError(e.getMessage());
//        }
//        return resp;
//    }
    public ReqRes sellerSignUp(String email, String password, String restaurantName, MultipartFile image) {
        ReqRes resp = new ReqRes();
        try {
            logger.info("Attempting to sign up seller with email: {}", email);

            OurUsers ourUsers = new OurUsers();
            ourUsers.setEmail(email);
            ourUsers.setPassword(passwordEncoder.encode(password));
            ourUsers.setRole("SELLER");
            ourUsers.setEnabled(true); // Set enabled to true by default
            ourUsers.setRestaurantName(restaurantName);  // Set restaurantName

            if (image != null && !image.isEmpty()) {
                ourUsers.setImage(image.getBytes()); // Save the image bytes
                logger.info("Image uploaded successfully for email: {}", email);
            }

            OurUsers ourUserResult = ourUserRepo.save(ourUsers);
            if (ourUserResult != null && ourUserResult.getId() > 0) {
                logger.info("Seller saved successfully with ID: {}", ourUserResult.getId());
                resp.setOurUsers(ourUserResult);
                resp.setMessage("Seller Saved Successfully");
                resp.setStatusCode(200);
            } else {
                logger.error("Failed to save seller with email: {}", email);
                resp.setStatusCode(500);
                resp.setMessage("Failed to save seller");
            }
        } catch (Exception e) {
            logger.error("Error occurred during seller signup for email: {}. Error: {}", email, e.getMessage());
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
    public ReqRes signIn(ReqRes signinRequest){
        ReqRes response = new ReqRes();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
            var user = ourUserRepo.findByEmail(signinRequest.getEmail()).orElseThrow();
            if (!user.isEnabled()) {
                response.setStatusCode(403);
                response.setError("Account is disabled");
                return response;
            }
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Signed In");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public ReqRes sellerSignIn(ReqRes signinRequest){
        ReqRes response = new ReqRes();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
            var user = ourUserRepo.findByEmail(signinRequest.getEmail()).orElseThrow();
            if (!"SELLER".equals(user.getRole())) {
                response.setStatusCode(403);
                response.setError("Access denied");
                return response;
            }
            if (!user.isEnabled()) {
                response.setStatusCode(403);
                response.setError("Account is disabled");
                return response;
            }
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Signed In");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }


    public ReqRes refreshToken(ReqRes refreshTokenReqiest){
        ReqRes response = new ReqRes();
        String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
        OurUsers users = ourUserRepo.findByEmail(ourEmail).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenReqiest.getToken());
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Refreshed Token");
        }
        response.setStatusCode(500);
        return response;
    }
}
