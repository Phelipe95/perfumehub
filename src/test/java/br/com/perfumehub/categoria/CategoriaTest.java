package br.com.perfumehub.categoria;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
