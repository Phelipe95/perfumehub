package br.com.perfumehub.estoque;

import br.com.perfumehub.categoria.Categoria;
import br.com.perfumehub.categoria.CategoriaRepository;
import br.com.perfumehub.produto.Produto;
import br.com.perfumehub.produto.ProdutoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class EntradaEstoqueRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    EntradaEstoqueRepository entradaEstoqueRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveSalvarEntradaEstoqueNoBanco() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Categoria categoriaSalva = categoriaRepository.saveAndFlush(categoria);

        Produto produto = new Produto(
                "Malbec",
                new BigDecimal("150.00"),
                0,
                categoriaSalva
        );

        Produto produtoSalvo = produtoRepository.saveAndFlush(produto);

        EntradaEstoque entradaEstoque = new EntradaEstoque(
                produtoSalvo,
                LocalDate.of(2026, 11, 8),
                10,
                new BigDecimal("100.00")
        );

        EntradaEstoque entradaSalva = entradaEstoqueRepository.saveAndFlush(entradaEstoque);

        Long entradaId = entradaSalva.getId();

        assertNotNull(entradaId);

        entityManager.clear();

        EntradaEstoque entradaEncontrada = entradaEstoqueRepository
                .findById(entradaId)
                .orElseThrow();

        assertEquals(produtoSalvo.getId(), entradaEncontrada.getProduto().getId());
        assertEquals(entradaId, entradaEncontrada.getId());
        assertEquals(LocalDate.of(2026, 11, 8), entradaEncontrada.getDataCompra());
        assertEquals(10, entradaEncontrada.getQuantidade());
        assertEquals(new BigDecimal("100.00"), entradaEncontrada.getCustoUnitario());
    }
}
