package br.com.gestao.financeira.aplicacao.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public class MembroAdicaoDto {

    @NotNull(message = "O ID do usuário é obrigatório")
    @Schema(description = "ID do usuário que será adicionado à família", example = "1")
    private Long usuarioId;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
