package br.fmp.av2.security;

import br.fmp.av2.model.UserAccount;
import br.fmp.av2.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UserAccountRepository repo;

    public DbUserDetailsService(UserAccountRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount u = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        // IMPORTANTe: a role já deve vir com prefixo ROLE_
        return User.withUsername(u.getEmail())
                .password(u.getSenhaHash())
                .roles(u.getRole().replace("ROLE_", ""))
                .build();
    }
}
