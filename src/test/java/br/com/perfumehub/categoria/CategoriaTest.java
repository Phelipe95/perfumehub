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

    @Test
    void deveRejeitarNomeComMaisDe100Caracteres() {
        String nome = "a".repeat(101);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Categoria(nome, "Descrição válida")
        );

        assertEquals(
                "O nome da categoria deve ter no máximo 100 caracteres.",
                exception.getMessage()
        );
    }

    @Test
    void deveRejeitarDescricaoComMaisDe255Caracteres() {
        String descricao = "a".repeat(256);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Categoria("Masculino", descricao)
        );

        assertEquals(
                "A descrição da categoria deve ter no máximo 255 caracteres.",
                exception.getMessage()
        );
    }

    @Test
    void deveAceitarNomeEDescricaoNosLimites() {
        String nome = "a".repeat(100);
        String descricao = "b".repeat(255);

        Categoria categoria = new Categoria(nome, descricao);

        assertEquals(nome, categoria.getNome());
        assertEquals(descricao, categoria.getDescricao());
    }

    @Test
    void devePreservarDadosAoAtualizarComDescricaoAcimaDoLimite() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        String descricao = "a".repeat(256);

        assertThrows(
                IllegalArgumentException.class,
                () -> categoria.atualizar("Feminino", descricao)
        );

        assertEquals("Masculino", categoria.getNome());
        assertEquals("Perfumes masculinos", categoria.getDescricao());
    }
}
