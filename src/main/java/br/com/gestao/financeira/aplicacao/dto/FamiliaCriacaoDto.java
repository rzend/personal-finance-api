package br.com.gestao.financeira.aplicacao.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class FamiliaCriacaoDto {

    @NotBlank(message = "O nome da família é obrigatório")
    @Schema(description = "Nome da família a ser criada", example = "Família Silva")
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
