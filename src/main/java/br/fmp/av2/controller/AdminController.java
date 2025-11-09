package br.fmp.av2.controller;

import br.fmp.av2.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserAccountRepository userRepo;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> listUsers() {
        List<Map<String, Object>> users = userRepo.findAll().stream()
                .map(u -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", u.getId());
                    userInfo.put("email", u.getEmail());
                    userInfo.put("role", u.getRole());
                    userInfo.put("senhaHash", u.getSenhaHash());
                    userInfo.put("senhaHashLength", u.getSenhaHash().length());
                    userInfo.put("isBCrypt", u.getSenhaHash().startsWith("$2a$") || u.getSenhaHash().startsWith("$2b$"));
                    return userInfo;
                })
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/generate-hash")
    public ResponseEntity<Map<String, String>> generateHash(@RequestParam String senha) {
        org.springframework.security.crypto.password.PasswordEncoder encoder = 
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String hash = encoder.encode(senha);
        
        Map<String, String> response = new HashMap<>();
        response.put("senhaOriginal", senha);
        response.put("senhaHash", hash);
        response.put("hashLength", String.valueOf(hash.length()));
        response.put("isBCrypt", String.valueOf(hash.startsWith("$2a$") || hash.startsWith("$2b$")));
        
        return ResponseEntity.ok(response);
    }
}

