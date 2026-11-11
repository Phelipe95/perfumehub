package br.com.perfumehub.estoque;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EntradaEstoqueCadastroDTO(

        @NotNull(message = "O ID do produto é obrigatório.")
        Long produtoId,

        @NotNull(message = "A data da compra é obrigatória.")
        LocalDate dataCompra,

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidade,

        @NotNull(message = "O custo unitário é obrigatório.")
        @PositiveOrZero(message = "O custo unitário não pode ser negativo.")
        @Digits(
                integer = 8,
                fraction = 2,
                message = "O custo unitário deve ter até 8 dígitos inteiros e 2 casas decimais."
        )
        BigDecimal custoUnitario
) {
}
