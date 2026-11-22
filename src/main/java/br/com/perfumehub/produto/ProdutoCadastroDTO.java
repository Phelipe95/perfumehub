package br.com.perfumehub.produto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Digits;
import jakarta.validation.Valid;
import br.com.perfumehub.estoque.CompraInicialDTO;

import java.math.BigDecimal;

public record ProdutoCadastroDTO(

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

        @NotNull(message = "O estoque do produto é obrigatório.")
        @Min(value = 0, message = "O estoque não pode ser negativo.")
        Integer quantidadeEstoque,

        @NotNull(message = "A categoria do produto é obrigatória.")
        Long categoriaId,

        @Valid
        CompraInicialDTO compraInicial
) {}