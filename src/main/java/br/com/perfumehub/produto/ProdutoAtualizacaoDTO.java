package br.com.perfumehub.produto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProdutoAtualizacaoDTO(

        @NotBlank(message = "O nome do produto é obrigatório.")
        @Size(
                max = 150,
                message = "O nome do produto deve ter no máximo 150 caracteres."
        )
        String nome,

        @NotNull(message = "O preço do produto é obrigatório.")
        @DecimalMin(value = "0.0", message = "O valor não pode ser negativo.")
        @Digits(
                integer = 8,
                fraction = 2,
                message = "O preço do produto deve ter até 8 dígitos inteiros e 2 casas decimais."
        )
        BigDecimal preco,

        @NotNull(message = "A categoria do produto é obrigatória.")
        Long categoriaId
) {
}
