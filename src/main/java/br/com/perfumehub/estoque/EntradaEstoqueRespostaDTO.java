package br.com.perfumehub.estoque;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EntradaEstoqueRespostaDTO(
        Long id,
        Long produtoId,
        LocalDate dataCompra,
        int quantidade,
        BigDecimal custoUnitario
) {
    public static EntradaEstoqueRespostaDTO fromEntity(EntradaEstoque entrada) {
        return new EntradaEstoqueRespostaDTO(
                entrada.getId(),
                entrada.getProduto().getId(),
                entrada.getDataCompra(),
                entrada.getQuantidade(),
                entrada.getCustoUnitario()
        );
    }
}
