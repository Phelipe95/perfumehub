package br.com.perfumehub.produto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoCadastroDTO(

        @NotBlank(message = "O nome do produto é obrigatório")
        String nome,

        @NotNull(message = "O preço do produto é obrigatório")
        @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
        BigDecimal preco,

        @NotNull(message = "O estoque do produto é obrigatório")
        @Min(value = 0, message = "O estoque não pode ser negativo")
        Integer quantidadeEstoque,

        @NotNull(message = "A categoria do produto é obrigatória")
        Long categoriaId
) {}