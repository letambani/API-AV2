package br.fmp.av2.controller;

import br.fmp.av2.dto.AlunoCreateDTO;
import br.fmp.av2.exception.AlunoNotFoundException;
import br.fmp.av2.model.Aluno;
import br.fmp.av2.repository.AlunoRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class HealthController {

    private final AlunoRepository alunoRepository;

    public HealthController(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "UP", "app", "av2-api");
    }

    // LISTAR todos
    @GetMapping("/alunos")
    public ResponseEntity<List<Aluno>> alunos() {
        return ResponseEntity.ok(alunoRepository.findAll());
    }

    // BUSCAR por id
    @GetMapping("/alunos/{id}")
    public ResponseEntity<Aluno> getById(@PathVariable Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoNotFoundException("Aluno " + id + " não encontrado"));
        return ResponseEntity.ok(aluno);
    }

    // CRIAR
    @PostMapping("/alunos")
    public ResponseEntity<Aluno> criarAluno(@Valid @RequestBody AlunoCreateDTO dto) {
        Aluno entity = new Aluno(dto.getNome(), dto.getIdade(), dto.getCpf());
        Aluno saved = alunoRepository.save(entity);
        return ResponseEntity.created(URI.create("/alunos/" + saved.getId())).body(saved);
    }

    // ATUALIZAR
    @PutMapping("/alunos/{id}")
    public ResponseEntity<Aluno> atualizar(@PathVariable Long id, @Valid @RequestBody AlunoCreateDTO dto) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoNotFoundException("Aluno " + id + " não encontrado"));
        aluno.setNome(dto.getNome());
        aluno.setIdade(dto.getIdade());
        aluno.setCpf(dto.getCpf());
        Aluno saved = alunoRepository.save(aluno);
        return ResponseEntity.ok(saved);
    }

    // DELETAR
    @DeleteMapping("/alunos/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoNotFoundException("Aluno " + id + " não encontrado"));
        alunoRepository.delete(aluno);
        return ResponseEntity.noContent().build();
    }
}
