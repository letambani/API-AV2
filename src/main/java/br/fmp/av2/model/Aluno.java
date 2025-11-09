package br.fmp.av2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "aluno")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "nome é obrigatório")
    @Size(min = 2, max = 60, message = "nome deve ter entre 2 e 60 caracteres")
    @Column(nullable = false, length = 60)
    private String nome;

    @NotNull(message = "idade é obrigatória")
    @Min(value = 1, message = "idade mínima é 1")
    @Column(nullable = false)
    private Integer idade;

    @NotBlank(message = "cpf é obrigatório")
    @Size(min = 11, max = 14, message = "cpf deve ter entre 11 e 14 caracteres")
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    public Aluno() {}

    public Aluno(Long id, String nome, Integer idade, String cpf) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.cpf = cpf;
    }

    public Aluno(String nome, Integer idade, String cpf) {
        this.nome = nome;
        this.idade = idade;
        this.cpf = cpf;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Integer getIdade() { return idade; }
    public String getCpf() { return cpf; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setIdade(Integer idade) { this.idade = idade; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
