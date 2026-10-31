package br.com.perfumehub.produto;

import br.com.perfumehub.categoria.Categoria;
import br.com.perfumehub.categoria.CategoriaRepository;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveSalvarProdutoNoBanco() {
        Categoria categoria = new Categoria(
            "Masculino",
            "Perfumes masculinos"
        );

        Categoria categoriaSalva = categoriaRepository.saveAndFlush(categoria);

        assertNotNull(categoriaSalva.getId());

        Produto produto = new Produto(
            "Malbec",
            new BigDecimal("150.00"),
            10,
            categoriaSalva
        );

        Produto produtoSalvo = produtoRepository.saveAndFlush(produto);

        assertNotNull(produtoSalvo.getId());

        assertEquals("Malbec", produtoSalvo.getNome());

        assertEquals(new BigDecimal("150.00"), produtoSalvo.getPreco());

        assertEquals(10, produtoSalvo.getQuantidadeEstoque());

        assertEquals(categoriaSalva, produtoSalvo.getCategoria());
    }

    @Test
    void deveBuscarProdutoPorId() {
        Categoria categoria = new Categoria(
            "Masculino",
            "Perfumes masculinos"
        );

        Categoria categoriaSalva = categoriaRepository.saveAndFlush(categoria);

        assertNotNull(categoriaSalva.getId());

        Produto produto = new Produto(
            "Malbec",
            new BigDecimal("150.00"),
            10,
            categoriaSalva
        );

        Produto produtoSalvo = produtoRepository.saveAndFlush(produto);

        entityManager.flush();
        entityManager.clear();

        Optional<Produto> resultado = produtoRepository.findById(produtoSalvo.getId());

        assertTrue(resultado.isPresent());

        Produto produtoEncontrado = resultado.get();

        assertEquals("Malbec", produtoEncontrado.getNome());
        assertEquals(new BigDecimal("150.00"), produtoEncontrado.getPreco());
        assertEquals(10, produtoEncontrado.getQuantidadeEstoque());
        assertEquals("Masculino", produtoEncontrado.getCategoria().getNome());
    }

    @Test
    void deveAlterarPrecoDoProduto() {
        Categoria categoria = new Categoria(
            "Masculino",
            "Perfumes masculinos"
        );

        Categoria categoriaSalva = categoriaRepository.saveAndFlush(categoria);

        assertNotNull(categoriaSalva.getId());

        Produto produto = new Produto(
            "Malbec",
            new BigDecimal("150.00"),
            10,
            categoriaSalva
        );

        Produto produtoSalvo = produtoRepository.saveAndFlush(produto);

        Produto encontrarProduto = produtoRepository.findById(produtoSalvo.getId()).orElseThrow();

        encontrarProduto.alterarPreco(new BigDecimal("170.00"));

        entityManager.flush();
        entityManager.clear();

        Optional<Produto> resultado = produtoRepository.findById(produtoSalvo.getId());

        assertTrue(resultado.isPresent());

        Produto produtoEncontrado = resultado.get();

        assertEquals("Malbec", produtoEncontrado.getNome());
        assertEquals(new BigDecimal("170.00"), produtoEncontrado.getPreco());
        assertEquals(10, produtoEncontrado.getQuantidadeEstoque());
        assertEquals("Masculino", produtoEncontrado.getCategoria().getNome());
    }

    @Test
    void deveExcluirProdutoPorId() {
        Categoria categoria = new Categoria(
            "Masculino",
            "Perfumes masculinos"
        );

        Categoria categoriaSalva = categoriaRepository.saveAndFlush(categoria);

        Produto produto = new Produto(
            "Malbec",
            new BigDecimal("150.00"),
            10,
            categoria
        );

        Produto produtoSalvo = produtoRepository.saveAndFlush(produto);

        Produto produtoEncontrado = produtoRepository.findById(produtoSalvo.getId()).orElseThrow();

        produtoRepository.deleteById(produtoEncontrado.getId());

        entityManager.flush();
        entityManager.clear();

        assertFalse(produtoRepository.existsById(produtoEncontrado.getId()));
    }
}