package br.com.perfumehub.produto;

import br.com.perfumehub.categoria.Categoria;
import br.com.perfumehub.categoria.CategoriaRepository;
import br.com.perfumehub.estoque.CompraInicialDTO;
import br.com.perfumehub.estoque.EntradaEstoque;
import br.com.perfumehub.estoque.EntradaEstoqueRepository;
import br.com.perfumehub.estoque.EntradaEstoqueService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

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
    private EntradaEstoqueService entradaEstoqueService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

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

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void deveRejeitarAtualizacaoDeProdutoComVersaoDesatualizada() {
        Categoria categoria = categoriaRepository.saveAndFlush(
                new Categoria(
                        "Categoria concorrência " + UUID.randomUUID(),
                        "Categoria para teste de versão"
                )
        );

        Produto produtoSalvo = produtoRepository.saveAndFlush(
                new Produto(
                        "Malbec",
                        new BigDecimal("150.00"),
                        0,
                        categoria
                )
        );

        Long produtoId = produtoSalvo.getId();

        Produto produtoDesatualizado = produtoRepository
                .findById(produtoId)
                .orElseThrow();

        try {
            entradaEstoqueService.registrar(
                    produtoId,
                    LocalDate.of(2026, 11, 22),
                    10,
                    new BigDecimal("100.00")
            );

            produtoDesatualizado.registrarEntrada(
                    5,
                    new BigDecimal("80.00")
            );

            assertThrows(
                    OptimisticLockingFailureException.class,
                    () -> produtoRepository.saveAndFlush(produtoDesatualizado)
            );

            Produto produtoAtual = produtoRepository
                    .findById(produtoId)
                    .orElseThrow();

            assertEquals(10, produtoAtual.getQuantidadeEstoque());
            assertEquals(
                    new BigDecimal("100.000000"),
                    produtoAtual.getCustoMedio()
            );

            assertEquals(
                    1,
                    entradaEstoqueRepository.findByProduto_Id(produtoId).size()
            );
        } finally {
            entradaEstoqueRepository.deleteAll(
                    entradaEstoqueRepository.findByProduto_Id(produtoId)
            );
            produtoRepository.deleteById(produtoId);
            categoriaRepository.deleteById(categoria.getId());
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void deveDesfazerEntradaQuandoHouverConflitoEntreCompras() throws Exception {
        Categoria categoria = categoriaRepository.saveAndFlush(
                new Categoria(
                        "Categoria teste " + UUID.randomUUID(),
                        "Categoria para teste"
                )
        );

        Produto produtoSalvo = produtoRepository.saveAndFlush(
                new Produto(
                        "Malbec",
                        new BigDecimal("150.00"),
                        0,
                        categoria
                )
        );

        Long produtoId = produtoSalvo.getId();

        CountDownLatch produtoLido = new CountDownLatch(1);
        CountDownLatch liberarCompra = new CountDownLatch(1);

        TransactionTemplate transactionTemplate =
                new TransactionTemplate(transactionManager);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<?> compraPendente = executor.submit(() ->
                transactionTemplate.executeWithoutResult(status -> {
                    produtoRepository.findById(produtoId).orElseThrow();

                    produtoLido.countDown();

                    try {
                        if (!liberarCompra.await(10, TimeUnit.SECONDS)) {
                            throw new IllegalStateException(
                                    "Tempo esgotado aguardando a liberação da compra."
                            );
                        }
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();

                        throw new IllegalStateException(
                                "A operação de teste foi interrompida.",
                                exception
                        );
                    }

                    entradaEstoqueService.registrar(
                            produtoId,
                            LocalDate.of(2026, 11, 22),
                            5,
                            new BigDecimal("80.00")
                    );
                })
        );

        try {
            assertTrue(
                    produtoLido.await(10, TimeUnit.SECONDS),
                    "A operação pendente não carregou o produto a tempo."
            );

            entradaEstoqueService.registrar(
                    produtoId,
                    LocalDate.of(2026, 11, 22),
                    10,
                    new BigDecimal("100.00")
            );

            liberarCompra.countDown();

            ExecutionException exception = assertThrows(
                    ExecutionException.class,
                    () -> compraPendente.get(10, TimeUnit.SECONDS)
            );

            assertInstanceOf(
                    OptimisticLockingFailureException.class,
                    exception.getCause()
            );

            Produto produtoAtual = produtoRepository
                    .findById(produtoId)
                    .orElseThrow();

            assertEquals(10, produtoAtual.getQuantidadeEstoque());
            assertEquals(
                    new BigDecimal("100.000000"),
                    produtoAtual.getCustoMedio()
            );

            List<EntradaEstoque> entradas =
                    entradaEstoqueRepository.findByProduto_Id(produtoId);

            assertEquals(1, entradas.size());
            assertEquals(10, entradas.get(0).getQuantidade());
            assertEquals(
                    new BigDecimal("100.00"),
                    entradas.get(0).getCustoUnitario()
            );
        } finally {
            liberarCompra.countDown();
            executor.shutdownNow();

            assertTrue(
                    executor.awaitTermination(10, TimeUnit.SECONDS),
                    "A operação pendente não encerrou a tempo."
            );

            entradaEstoqueRepository.deleteAll(
                    entradaEstoqueRepository.findByProduto_Id(produtoId)
            );
            produtoRepository.deleteById(produtoId);
            categoriaRepository.deleteById(categoria.getId());
        }
    }
}
