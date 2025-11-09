package br.fmp.av2.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AlunoCreateDTO {

    @NotBlank(message = "nome é obrigatório")
    @Size(min = 2, max = 60, message = "nome deve ter entre 2 e 60 caracteres")
    private String nome;

    @NotNull(message = "idade é obrigatória")
    @Min(value = 1, message = "idade mínima é 1")
    private Integer idade;

    @NotBlank(message = "cpf é obrigatório")
    @Size(min = 11, max = 14, message = "cpf deve ter entre 11 e 14 caracteres")
    private String cpf;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
