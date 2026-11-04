package br.com.perfumehub.categoria;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveSalvarCategoriaNoBanco() {

        Categoria categoria = new Categoria(
            "Teste Masculino",
            "Categoria criada pelo teste"
        );

        Categoria categoriaSalva = categoriaRepository.saveAndFlush(categoria);

        assertNotNull(categoriaSalva.getId());
        assertEquals("Teste Masculino",categoriaSalva.getNome());
    }

    @Test
    void deveAtualizarCategoriaNoBanco() {

        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Categoria categoriaSalva = categoriaRepository.saveAndFlush(categoria);
        Long idOriginal = categoriaSalva.getId();

        categoriaSalva.atualizar(
                "Feminino",
                "Perfumes femininos"
        );

        entityManager.flush();
        entityManager.clear();

        Categoria categoriaEncontrada = categoriaRepository
                .findById(idOriginal)
                        .orElseThrow();

        assertEquals(idOriginal, categoriaEncontrada.getId());
        assertEquals("Feminino",categoriaEncontrada.getNome());
        assertEquals("Perfumes femininos",categoriaEncontrada.getDescricao());
    }

    @Test
    void deveVerificarNomeUtilizadoPorOutraCategoria() {
        Categoria masculino = categoriaRepository.saveAndFlush(
                new Categoria("Masculino", "Perfumes masculinos")
        );

        categoriaRepository.saveAndFlush(
                new Categoria("Feminino", "Perfumes femininos")
        );

        assertFalse(
                categoriaRepository.existsByNomeAndIdNot(
                        "Masculino", masculino.getId()
                )
        );

        assertTrue(
                categoriaRepository.existsByNomeAndIdNot(
                        "Feminino", masculino.getId()
                )
        );
    }
}
