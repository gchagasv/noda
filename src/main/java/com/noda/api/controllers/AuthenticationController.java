package com.noda.api.controllers;


import com.noda.api.dtos.AuthenticationResponseDTO;
import com.noda.api.dtos.LoginRequestDTO;
import com.noda.api.dtos.ResendCodeRequestDTO;
import com.noda.api.dtos.VerifyRequestDTO;
import com.noda.api.services.AuthenticationService;
import com.noda.api.services.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authService;
    private final JwtService jwtService;

    public AuthenticationController(AuthenticationService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

   @PostMapping("/login")
   public ResponseEntity<AuthenticationResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        String code = authService.authenticateUserAndGenerateOtp(loginRequest.email(), loginRequest.password());
        System.out.println("Sent fresh OTP code [" + code + "] to email: " + loginRequest.email());
        return ResponseEntity.ok(new AuthenticationResponseDTO("Password verified! A verification code has been sent to your email."));
   }


    @PostMapping("/verify")
    public ResponseEntity<AuthenticationResponseDTO> verifyLogin(@Valid @RequestBody VerifyRequestDTO verifyRequest) {
        boolean isValid = authService.validateOtp(verifyRequest.email(), verifyRequest.code());

        if(isValid) {
            String token = jwtService.generateToken(verifyRequest.email());
            return ResponseEntity.ok(new AuthenticationResponseDTO("Authentication Successful!.", token));
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthenticationResponseDTO("Authentication Failed: Invalid or Expired Code"));
    }

    @PostMapping("/resend")
    public ResponseEntity<AuthenticationResponseDTO> resendCoder (@RequestBody ResendCodeRequestDTO resendCodeRequestD) {
        String code = authService.generateAndSaveOtp(resendCodeRequestD.email());
        System.out.println("Resent fresh OTP code [" + code + "] to email: " + resendCodeRequestD.email());
        return ResponseEntity.ok(new AuthenticationResponseDTO("A new verification code has been sent to your email."));
    }
}
