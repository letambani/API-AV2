package br.fmp.av2.dto;

import br.fmp.av2.model.Aluno;

public class AlunoDTO {
    private Long id;
    private String nome;
    private Integer idade;
    private String cpf;

    public AlunoDTO() {
    }

    public AlunoDTO(Long id, String nome, Integer idade, String cpf) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.cpf = cpf;
    }

    public static AlunoDTO from(Aluno a) {
        if (a == null) return null;
        return new AlunoDTO(a.getId(), a.getNome(), a.getIdade(), a.getCpf());
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
