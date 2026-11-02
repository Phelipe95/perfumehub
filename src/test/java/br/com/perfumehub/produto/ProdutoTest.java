package br.com.perfumehub.produto;

import org.junit.jupiter.api.Test;
import br.com.perfumehub.categoria.Categoria;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProdutoTest {
    @Test
    void deveRejeitarPrecoNegativo() {
        Categoria categoria = new Categoria(
            "Masculino",
            "Perfumes masculinos"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            new Produto(
                "Malbec",
                new BigDecimal("-50.00"),
                10,
                categoria
            );
        });
    }

    @Test
    void deveRejeitarEstoqueNegativo() {
        Categoria categoria = new Categoria(
            "Feminino",
            "Perfumes femininos"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            new Produto(
                "Floratta Red",
                new BigDecimal("150.00"),
                -5,
                categoria
            );
        });
    }

    @Test
    void deveRejeitarNomeVazio() {
        Categoria categoria = new Categoria(
            "Feminino",
            "Perfumes femininos"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            new Produto(
                "  ",
                new BigDecimal("150.00"),
                0,
                categoria
            );
        });
    }

    @Test
    void deveRejeitarCategoriaNula() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Produto(
                "Egeo",
                new BigDecimal("150.00"),
                10,
                null
            );
        });
    }

    @Test
    void deveCriarProdutoValido() {
        Categoria categoria = new Categoria(
            "Masculino",
            "Perfumes masculinos"
        );

        Produto produto = new Produto(
            "Malbec",
            new BigDecimal("150.00"),
            10,
            categoria
        );

        assertEquals("Malbec", produto.getNome());
        assertEquals(new BigDecimal("150.00"), produto.getPreco());
        assertEquals(10, produto.getQuantidadeEstoque());
        assertEquals(categoria, produto.getCategoria());
    }

    @Test
    void deveRejeitarAlteracaoParaPrecoNegativo() {
        Categoria categoria = new Categoria(
            "Masculino",
            "Perfumes masculinos"
        );

        Produto produto = new Produto(
            "Malbec",
            new BigDecimal("150.00"),
            10,
            categoria
        );

        assertThrows(IllegalArgumentException.class,
            () -> produto.alterarPreco(new BigDecimal("-50.00")));

        assertEquals(new BigDecimal("150.00"), produto.getPreco());
    }

    @Test
    void deveRejeitarAlteracaoParaPrecoNulo() {
        Categoria categoria = new Categoria(
            "Masculino",
            "Perfumes masculinos"
        );

        Produto produto = new Produto(
            "Malbec",
            new BigDecimal("150.00"),
            10,
            categoria
        );

        assertThrows(IllegalArgumentException.class,
            () -> produto.alterarPreco(null));

        assertEquals(new BigDecimal("150.00"), produto.getPreco());
    }

    @Test
    void deveAtualizarProdutoValido() {
        Categoria categoria1 = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Categoria categoria2 = new Categoria(
                "Feminino",
                "Perfumes femininos"
        );

        Produto produto = new Produto(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                categoria1
        );

        produto.atualizar(
                "Lily",
                new BigDecimal("180.00"),
                5,
                categoria2
        );

        assertEquals("Lily", produto.getNome());
        assertEquals(new BigDecimal("180.00"), produto.getPreco());
        assertEquals(5, produto.getQuantidadeEstoque());
        assertEquals(categoria2, produto.getCategoria());
    }

    @Test
    void deveRejeitarAtualizacaoComCategoriaNula() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Produto produto = new Produto(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                categoria
        );

        assertThrows(IllegalArgumentException.class, () ->
                produto.atualizar(
                        "Lily",
                        new BigDecimal("180.00"),
                        5,
                        null
                ));

        assertEquals("Malbec", produto.getNome());
        assertEquals(new BigDecimal("150.00"), produto.getPreco());
        assertEquals(10, produto.getQuantidadeEstoque());
        assertEquals(categoria, produto.getCategoria());
    }

    @Test
    void deveRejeitarAtualizacaoComPrecoNegativo() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Produto produto = new Produto(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                categoria
        );

        assertThrows(IllegalArgumentException.class, () ->
                produto.atualizar(
                        "Lily",
                        new BigDecimal("-50.00"),
                        5,
                        categoria
                ));

        assertEquals("Malbec", produto.getNome());
        assertEquals(new BigDecimal("150.00"), produto.getPreco());
        assertEquals(10, produto.getQuantidadeEstoque());
        assertEquals(categoria, produto.getCategoria());
    }

    @Test
    void deveRejeitarAtualizacaoComEstoqueNegativo() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Produto produto = new Produto(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                categoria
        );

        assertThrows(IllegalArgumentException.class, () ->
                produto.atualizar(
                        "Lily",
                        new BigDecimal("180.00"),
                        -5,
                        categoria
                ));

        assertEquals("Malbec", produto.getNome());
        assertEquals(new BigDecimal("150.00"), produto.getPreco());
        assertEquals(10, produto.getQuantidadeEstoque());
        assertEquals(categoria, produto.getCategoria());
    }

    @Test
    void deveRejeitarAtualizacaoComNomeEmBranco() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Produto produto = new Produto(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                categoria
        );

        assertThrows(IllegalArgumentException.class, () ->
                produto.atualizar(
                        "   ",
                        new BigDecimal("180.00"),
                        5,
                        categoria
                ));

        assertEquals("Malbec", produto.getNome());
        assertEquals(new BigDecimal("150.00"), produto.getPreco());
        assertEquals(10, produto.getQuantidadeEstoque());
        assertEquals(categoria, produto.getCategoria());
    }

    @Test
    void deveRejeitarAtualizacaoComNomeNulo() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Produto produto = new Produto(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                categoria
        );

        assertThrows(IllegalArgumentException.class, () ->
                produto.atualizar(
                        null,
                        new BigDecimal("180.00"),
                        5,
                        categoria
                ));

        assertEquals("Malbec", produto.getNome());
        assertEquals(new BigDecimal("150.00"), produto.getPreco());
        assertEquals(10, produto.getQuantidadeEstoque());
        assertEquals(categoria, produto.getCategoria());
    }

    @Test
    void deveRejeitarAtualizacaoComPrecoNulo() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Produto produto = new Produto(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                categoria
        );

        assertThrows(IllegalArgumentException.class, () ->
                produto.atualizar(
                        "Lily",
                        null,
                        5,
                        categoria
                ));

        assertEquals("Malbec", produto.getNome());
        assertEquals(new BigDecimal("150.00"), produto.getPreco());
        assertEquals(10, produto.getQuantidadeEstoque());
        assertEquals(categoria, produto.getCategoria());
    }

    @Test
    void deveAtualizarProdutoComPrecoEEstoqueZero() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Produto produto = new Produto(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                categoria
        );

        produto.atualizar(
                "Lily",
                new BigDecimal("0.00"),
                0,
                categoria
        );

        assertEquals("Lily", produto.getNome());
        assertEquals(new BigDecimal("0.00"), produto.getPreco());
        assertEquals(0, produto.getQuantidadeEstoque());
        assertEquals(categoria, produto.getCategoria());
    }
}
