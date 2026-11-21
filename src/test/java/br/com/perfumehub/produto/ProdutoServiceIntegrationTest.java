package br.com.perfumehub.produto;

import br.com.perfumehub.categoria.Categoria;
import br.com.perfumehub.categoria.CategoriaRepository;
import br.com.perfumehub.estoque.CompraInicialDTO;
import br.com.perfumehub.estoque.EntradaEstoque;
import br.com.perfumehub.estoque.EntradaEstoqueRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProdutoServiceIntegrationTest {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EntradaEstoqueRepository entradaEstoqueRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveCadastrarProdutoComCompraInicialNoBanco() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        categoriaRepository.saveAndFlush(categoria);

        CompraInicialDTO compraInicial = new CompraInicialDTO(
                LocalDate.of(2026,11,21),
                10,
                new BigDecimal("100.00")
        );

        ProdutoCadastroDTO dto = new ProdutoCadastroDTO(
                "Malbec",
                new BigDecimal("150.00"),
                0,
                categoria.getId(),
                compraInicial
        );

        Produto produtoSalvo = produtoService.cadastrar(dto);
        Long produtoId = produtoSalvo.getId();

        entityManager.flush();
        entityManager.clear();

        Produto produtoEncontrado = produtoRepository
                .findById(produtoId)
                .orElseThrow();

        assertEquals("Malbec", produtoEncontrado.getNome());
        assertEquals(new BigDecimal("150.00"), produtoEncontrado.getPreco());
        assertEquals(10, produtoEncontrado.getQuantidadeEstoque());
        assertEquals(new BigDecimal("100.000000"), produtoEncontrado.getCustoMedio());
        assertEquals(categoria.getId(), produtoEncontrado.getCategoria().getId());

        List<EntradaEstoque> entradas = entradaEstoqueRepository.findByProduto_Id(produtoId);

        assertEquals(1, entradas.size());

        EntradaEstoque entrada = entradas.get(0);

        assertEquals(produtoId, entrada.getProduto().getId());
        assertEquals(compraInicial.dataCompra(), entrada.getDataCompra());
        assertEquals(10, entrada.getQuantidade());
        assertEquals(new BigDecimal("100.00"), entrada.getCustoUnitario());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void deveDesfazerCadastroDoProdutoQuandoCompraInicialFalhar() {
        Categoria categoria = categoriaRepository.saveAndFlush(
                new Categoria(
                        "Categoria rollback " + UUID.randomUUID(),
                        "Categoria para teste de Rollback"
                )
        );

        CompraInicialDTO compraInicial = new CompraInicialDTO(
                LocalDate.of(2026,11,21),
                10,
                new BigDecimal("100000000.00")
        );

        ProdutoCadastroDTO dto = new ProdutoCadastroDTO(
                "Produto rollback",
                new BigDecimal("150.00"),
                0,
                categoria.getId(),
                compraInicial
        );

        try {
            long totalProdutosAntes = produtoRepository.count();
            long totalEntradasAntes = entradaEstoqueRepository.count();

            assertThrows(
                    DataIntegrityViolationException.class,
                    () -> produtoService.cadastrar(dto)
            );

            assertEquals(totalProdutosAntes, produtoRepository.count());
            assertEquals(totalEntradasAntes, entradaEstoqueRepository.count());
        } finally {
            categoriaRepository.deleteById(categoria.getId());
        }
    }
}
