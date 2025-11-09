package br.fmp.av2.config;

import br.fmp.av2.model.Aluno;
import br.fmp.av2.model.UserAccount;
import br.fmp.av2.repository.AlunoRepository;
import br.fmp.av2.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserAccountRepository userRepo;
    private final AlunoRepository alunoRepo;
    private final PasswordEncoder encoder;

    public DataSeeder(UserAccountRepository userRepo, AlunoRepository alunoRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.alunoRepo = alunoRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        // Criar usuário admin
        if (userRepo.findByEmail("admin@fmp.br").isEmpty()) {
            UserAccount u = new UserAccount();
            u.setEmail("admin@fmp.br");
            u.setSenhaHash(encoder.encode("123456"));
            u.setRole("ROLE_ADMIN");
            userRepo.save(u);
            System.out.println("✅ Usuário admin@fmp.br criado com sucesso!");
        } else {
            System.out.println("ℹ️  Usuário admin@fmp.br já existe.");
        }

        // Criar alguns alunos de exemplo (opcional)
        if (alunoRepo.count() == 0) {
            alunoRepo.save(new Aluno("João Silva", 25, "12345678900"));
            alunoRepo.save(new Aluno("Maria Santos", 22, "98765432100"));
            System.out.println("✅ Alunos de exemplo criados.");
        }
    }
}
