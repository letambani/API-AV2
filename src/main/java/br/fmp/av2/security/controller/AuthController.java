package br.fmp.av2.security.controller;

import br.fmp.av2.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwt;

    public record LoginDTO(String email, String senha) {}

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO body) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.email(), body.senha())
        );
        String token = jwt.generateToken(auth.getName()); // subject = email
        long expiresIn = jwt.getExpirationSeconds();

        return ResponseEntity.ok(Map.of(
                "type", "Bearer",
                "token", token,
                "expiresIn", expiresIn
        ));
    }
}
