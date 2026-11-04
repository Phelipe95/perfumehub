package br.com.perfumehub.categoria;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaTest {
    @Test
    void deveRejeitarNomeNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Categoria(
                null,
                "Sem categoria"
            );
        });
    }

    @Test
    void deveRejeitarNomeVazio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Categoria(
                "",
                "Sem nome"
            );
        });
    }

    @Test
    void deveRejeitarNomeComEspacos() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Categoria(
                "   ",
                "Sem nome"
            );
        });
    }

    @Test
    void deveCriarCategoriaValida() {
        Categoria categoria = new Categoria(
            "Masculino",
            "Perfumes masculinos"
        );

        assertEquals("Masculino", categoria.getNome());
        assertEquals("Perfumes masculinos", categoria.getDescricao());
    }

    @Test
    void deveAtualizarCategoriaValida() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        categoria.atualizar(
                "Feminino",
                "Perfumes femininos"
        );

        assertEquals("Feminino", categoria.getNome());
        assertEquals("Perfumes femininos", categoria.getDescricao());
    }

    @Test
    void deveRejeitarAtualizacaoComNomeNulo() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        assertThrows(IllegalArgumentException.class,
                () -> categoria.atualizar(null, "Nova descrição"));

        assertEquals("Masculino", categoria.getNome());
        assertEquals("Perfumes masculinos", categoria.getDescricao());
    }

    @Test
    void deveRejeitarAtualizacaoComNomeEmBranco() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        assertThrows(IllegalArgumentException.class,
                () -> categoria.atualizar("   ", "Nova descrição"));

        assertEquals("Masculino", categoria.getNome());
        assertEquals("Perfumes masculinos", categoria.getDescricao());
    }

    @Test
    void deveAtualizarCategoriaComDescricaoNula() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        categoria.atualizar("Feminino", null);

        assertEquals("Feminino", categoria.getNome());
        assertNull(categoria.getDescricao());
    }
}
