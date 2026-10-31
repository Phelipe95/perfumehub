package br.com.perfumehub.categoria;

import jakarta.validation.constraints.NotBlank;

public record CategoriaCadastroDTO(

        @NotBlank(message = "O nome da categoria é obrigatório")
        String nome,

        String descricao
) {
}
