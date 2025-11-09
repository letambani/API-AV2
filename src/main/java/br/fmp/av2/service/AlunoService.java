package br.fmp.av2.service;

import br.fmp.av2.dto.AlunoCreateDTO;
import br.fmp.av2.dto.AlunoDTO;
import br.fmp.av2.exception.AlunoNotFoundException;
import br.fmp.av2.exception.CpfDuplicadoException;
import br.fmp.av2.model.Aluno;
import br.fmp.av2.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository repository;

    @Transactional(readOnly = true)
    public List<AlunoDTO> findAll() {
        return repository.findAll().stream()
                .map(AlunoDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlunoDTO findById(Long id) {
        Aluno aluno = repository.findById(id)
                .orElseThrow(() -> new AlunoNotFoundException(id));
        return AlunoDTO.from(aluno);
    }

    @Transactional
    public AlunoDTO create(AlunoCreateDTO dto) {
        // Verificar se CPF já existe
        if (repository.existsByCpf(dto.getCpf())) {
            throw new CpfDuplicadoException(dto.getCpf(), "Já existe uma pessoa com cpf");
        }

        Aluno aluno = new Aluno(dto.getNome(), dto.getIdade(), dto.getCpf());
        aluno = repository.save(aluno);
        return AlunoDTO.from(aluno);
    }

    @Transactional
    public AlunoDTO update(Long id, AlunoCreateDTO dto) {
        Aluno aluno = repository.findById(id)
                .orElseThrow(() -> new AlunoNotFoundException(id));

        // Verificar se CPF já existe em outro aluno
        if (!aluno.getCpf().equals(dto.getCpf()) && repository.existsByCpf(dto.getCpf())) {
            throw new CpfDuplicadoException(dto.getCpf(), "Já existe uma pessoa com cpf");
        }

        aluno.setNome(dto.getNome());
        aluno.setIdade(dto.getIdade());
        aluno.setCpf(dto.getCpf());
        aluno = repository.save(aluno);
        return AlunoDTO.from(aluno);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new AlunoNotFoundException(id);
        }
        repository.deleteById(id);
    }
}

