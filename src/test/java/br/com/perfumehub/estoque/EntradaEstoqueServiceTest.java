package br.com.perfumehub.estoque;

import br.com.perfumehub.categoria.Categoria;
import br.com.perfumehub.categoria.CategoriaRepository;
import br.com.perfumehub.produto.Produto;
import br.com.perfumehub.produto.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EntradaEstoqueServiceTest {

    @Autowired
    private EntradaEstoqueService entradaEstoqueService;

    @Autowired
    private EntradaEstoqueRepository entradaEstoqueRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    void deveRegistrarEntradaEAtualizarProdutoNoBanco() {
        Categoria categoria = new Categoria(
                "Categoria entrada " + UUID.randomUUID(),
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

        Long produtoId = produtoSalvo.getId();
        Long entradaId = null;

        try {
            EntradaEstoque entrada = entradaEstoqueService.registrar(
                    produtoId,
                    LocalDate.of(2026, 11, 8),
                    10,
                    new BigDecimal("100.00")
            );

            entradaId = entrada.getId();

            EntradaEstoque entradaGravada = entradaEstoqueRepository
                    .findById(entradaId)
                    .orElseThrow();

            Produto produtoAtualizado = produtoRepository
                    .findById(produtoId)
                    .orElseThrow();

            assertEquals(10, entradaGravada.getQuantidade());
            assertEquals(10, produtoAtualizado.getQuantidadeEstoque());
            assertEquals(new BigDecimal("100.000000"), produtoAtualizado.getCustoMedio());
        } finally {
            if (entradaId != null) {
                entradaEstoqueRepository.deleteById(entradaId);
            }

            produtoRepository.deleteById(produtoId);
            categoriaRepository.deleteById(categoriaSalva.getId());
        }
    }

    @Test
    void deveRejeitarEntradaParaProdutoInexistente() {
        Long produtoId = Long.MAX_VALUE;

        assertFalse(produtoRepository.existsById(produtoId));

        long totalAntes = entradaEstoqueRepository.count();

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> entradaEstoqueService.registrar(
                        produtoId,
                        LocalDate.of(2026,11,8),
                        10,
                        new BigDecimal("100.00")
                )
        );

        assertEquals("Produto não encontrado.", exception.getMessage());
        assertEquals(totalAntes, entradaEstoqueRepository.count());
    }

    @Test
    void deveDesfazerAtualizacaoDoEstoqueQuandoEntradaFalhar() {
        Categoria categoria = new Categoria(
                "Categoria entrada " + UUID.randomUUID(),
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
        Long produtoId = produtoSalvo.getId();

        try {
            assertThrows(
                    DataIntegrityViolationException.class,
                    () -> entradaEstoqueService.registrar(
                            produtoId,
                            LocalDate.of(2026,11,8),
                            10,
                            new BigDecimal("100000000.00")
                    )
            );

            Produto produtoEncontrado = produtoRepository
                    .findById(produtoId)
                    .orElseThrow();

            assertEquals(0, produtoEncontrado.getQuantidadeEstoque());
            assertNull(produtoEncontrado.getCustoMedio());
        } finally {
            produtoRepository.deleteById(produtoId);
            categoriaRepository.deleteById(categoriaSalva.getId());
        }
    }
}

