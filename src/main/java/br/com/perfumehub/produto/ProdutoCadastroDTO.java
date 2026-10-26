package br.com.perfumehub.produto;

import java.math.BigDecimal;

public record ProdutoCadastroDTO(
        String nome,
        BigDecimal preco,
        int quantidadeEstoque,
        Long categoriaId
) {}