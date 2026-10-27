package br.com.perfumehub.produto;

import br.com.perfumehub.categoria.Categoria;

import java.math.BigDecimal;

public record ProdutoRespostaDTO(
        Long id,
        String nome,
        BigDecimal preco,
        int quantidadeEstoque,
        Long categoriaId
) {

    public static ProdutoRespostaDTO fromEntity(Produto produto) {
        return new ProdutoRespostaDTO(
                produto.getId(),
                produto.getNome(),
                produto.getPreco(),
                produto.getQuantidadeEstoque(),
                produto.getCategoria().getId()
        );
    }
}
