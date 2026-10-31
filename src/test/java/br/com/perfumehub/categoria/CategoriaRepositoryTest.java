package br.com.perfumehub.categoria;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

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
}
