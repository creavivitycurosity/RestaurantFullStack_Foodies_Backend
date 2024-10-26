package com.example.practice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.practice.dto.ReqRes;
import com.example.practice.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ReqRes signUp(@RequestBody ReqRes reqRes) {
        return authService.signUp(reqRes);
    }

    @PostMapping("/signin")
    public ReqRes signIn(@RequestBody ReqRes reqRes) {
        return authService.signIn(reqRes);
    }

    @PostMapping("/seller/signup")
    public ReqRes sellerSignUp(@RequestParam("email") String email, 
            @RequestParam("password") String password, 
            @RequestParam("restaurantName") String restaurantName, 
            @RequestParam("image") MultipartFile image) {
        ReqRes reqRes = new ReqRes(); // Create an instance of ReqRes
        return authService.sellerSignUp(email, password, restaurantName, image);
    }
    @PostMapping("/seller/signin")
    public ReqRes sellerSignIn(@RequestBody ReqRes reqRes) {
        return authService.sellerSignIn(reqRes);
    }

    @PostMapping("/refresh-token")
    public ReqRes refreshToken(@RequestBody ReqRes reqRes) {
        return authService.refreshToken(reqRes);
    }
}
