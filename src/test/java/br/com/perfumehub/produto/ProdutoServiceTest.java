package br.com.perfumehub.produto;

import br.com.perfumehub.categoria.Categoria;
import br.com.perfumehub.categoria.CategoriaRepository;
import br.com.perfumehub.estoque.CompraInicialDTO;
import br.com.perfumehub.estoque.EntradaEstoqueService;
import br.com.perfumehub.exception.ConflitoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private EntradaEstoqueService entradaEstoqueService;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void deveListarTodosOsProdutos() {

        when(produtoRepository.findAll())
                .thenReturn(List.of());

        List<Produto> resultado = produtoService.listarTodos();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveListarProdutosCadastrados() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Produto produto1 = new Produto(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                categoria
        );

        Produto produto2 = new Produto(
                "Quasar",
                new BigDecimal("120.00"),
                15,
                categoria
        );

        when(produtoRepository.findAll())
                .thenReturn(List.of(produto1, produto2));

        List<Produto> resultado = produtoService.listarTodos();
        assertEquals(
                2,
                resultado.size()
        );
        assertEquals(
                "Malbec",
                resultado.get(0).getNome()
        );
        assertEquals(
                "Quasar",
                resultado.get(1).getNome()
        );

        verify(produtoRepository).findAll();
    }

    @Test
    void deveBuscarProdutoPorId() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Produto produto= new Produto(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                categoria
        );

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        Produto resultado = produtoService.buscarPorId(1L);

        assertEquals(
                "Malbec",
                resultado.getNome()
        );

        verify(produtoRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExiste() {
        when(produtoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> produtoService.buscarPorId(99L)
        );

        verify(produtoRepository).findById(99L);
    }

    @Test
    void deveCadastrarProdutoComCategoriaExistente() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        ProdutoCadastroDTO dto = new ProdutoCadastroDTO(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                1L,
                null
        );

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(produtoRepository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Produto produtoSalvo = produtoService.cadastrar(dto);

        assertEquals("Malbec", produtoSalvo.getNome());
        assertEquals(new BigDecimal("150.00"), produtoSalvo.getPreco());
        assertEquals(10, produtoSalvo.getQuantidadeEstoque());
        assertSame(categoria, produtoSalvo.getCategoria());

        verify(categoriaRepository).findById(1L);
        verify(produtoRepository).save(any(Produto.class));
        verifyNoInteractions(entradaEstoqueService);
    }

    @Test
    void deveExcluirProdutoPorId() {
        when(produtoRepository.existsById(1L))
                .thenReturn(true);

        produtoService.excluirPorId(1L);

        verify(produtoRepository).existsById(1L);
        verify(produtoRepository, times(1))
                .deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoFalharExclusao() {
        when(produtoRepository.existsById(1L))
                .thenReturn(true);

        doThrow(new RuntimeException("Erro ao excluir."))
                .when(produtoRepository)
                .deleteById(1L);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> produtoService.excluirPorId(1L)
        );

        assertEquals("Erro ao excluir.", exception.getMessage());

        verify(produtoRepository).existsById(1L);
        verify(produtoRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoExcluirProdutoInexistente() {
        when(produtoRepository.existsById(1L))
                .thenReturn(false);

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> produtoService.excluirPorId(1L)
        );

        assertEquals("Produto não encontrado.", exception.getMessage());

        verify(produtoRepository, never()).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoCadastrarComCategoriaInexistente() {
        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        ProdutoCadastroDTO dto = new ProdutoCadastroDTO(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                99L,
                null
        );

        assertThrows(
                NoSuchElementException.class,
                () -> produtoService.cadastrar(dto)
        );

        verify(categoriaRepository).findById(99L);
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void deveAtualizarProdutoComCategoriaExistente() {
        Categoria categoria1 = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Categoria categoria2 = new Categoria(
                "Feminino",
                "Perfumes femininos"
        );

        Produto produto = new Produto(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                categoria1
        );

        ProdutoCadastroDTO cadastroDTO = new ProdutoCadastroDTO(
                "Lily",
                new BigDecimal("180.00"),
                5,
                2L,
                null
        );

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        when(categoriaRepository.findById(2L))
                .thenReturn(Optional.of(categoria2));

        when(produtoRepository.save(produto))
                .thenReturn(produto);

        Produto resultado = produtoService.atualizar(1L, cadastroDTO);

        assertEquals("Lily", resultado.getNome());
        assertEquals(new BigDecimal("180.00"), resultado.getPreco());
        assertEquals(5, resultado.getQuantidadeEstoque());
        assertSame(categoria2, resultado.getCategoria());

        verify(produtoRepository).findById(1L);
        verify(categoriaRepository).findById(2L);
        verify(produtoRepository).save(produto);
    }

    @Test
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        ProdutoCadastroDTO dto = new ProdutoCadastroDTO(
                "Lily",
                new BigDecimal("180.00"),
                5,
                2L,
                null
        );

        when(produtoRepository.findById(99L))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> produtoService.atualizar(99L, dto)
        );

        assertEquals("Produto não encontrado.", exception.getMessage());

        verify(produtoRepository).findById(99L);
        verifyNoInteractions(categoriaRepository);
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarComCategoriaInexistente() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        Produto produto = new Produto(
                "Malbec",
                new BigDecimal("150.00"),
                10,
                categoria
        );

        ProdutoCadastroDTO dto = new ProdutoCadastroDTO(
                "Lily",
                new BigDecimal("180.00"),
                5,
                99L,
                null
        );

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> produtoService.atualizar(1L, dto)
        );

        assertEquals("Categoria não encontrada.", exception.getMessage());
        assertEquals("Malbec", produto.getNome());
        assertEquals(new BigDecimal("150.00"), produto.getPreco());
        assertEquals(10, produto.getQuantidadeEstoque());
        assertSame(categoria, produto.getCategoria());

        verify(produtoRepository).findById(1L);
        verify(categoriaRepository).findById(99L);
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void deveRejeitarCompraInicialComEstoqueInicialDiferenteDeZero() {
        CompraInicialDTO compraInicial = new CompraInicialDTO(
                LocalDate.of(2026,11,21),
                10,
                new BigDecimal("100.00")
        );

        ProdutoCadastroDTO dto = new ProdutoCadastroDTO(
                "Malbec",
                new BigDecimal("100.00"),
                5,
                1L,
                compraInicial
        );

        ConflitoException exception = assertThrows(
                ConflitoException.class,
                () -> produtoService.cadastrar(dto)
        );

        assertEquals(
                "Ao registrar uma compra inicial, o estoque inicial deve ser zero.",
                exception.getMessage()
        );

        verifyNoInteractions(
                categoriaRepository,
                produtoRepository,
                entradaEstoqueService
        );
    }

    @Test
    void deveCadastrarProdutoComCompraInicial() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        CompraInicialDTO compraInicial = new CompraInicialDTO(
                LocalDate.of(2026,11,21),
                10,
                new BigDecimal("100.00")
        );

        ProdutoCadastroDTO dto = new ProdutoCadastroDTO(
                "Malbec",
                new BigDecimal("100.00"),
                0,
                1L,
                compraInicial
        );

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(produtoRepository.save(any(Produto.class)))
                .thenAnswer(invocation -> {
                    Produto produto = invocation.getArgument(0);
                    ReflectionTestUtils.setField(produto, "id", 2L);
                    return produto;
                });

        Produto resultado = produtoService.cadastrar(dto);

        assertEquals(2L, resultado.getId());
        assertEquals("Malbec", resultado.getNome());
        assertEquals(new BigDecimal("100.00"), resultado.getPreco());
        assertEquals(categoria, resultado.getCategoria());

        verify(produtoRepository).save(any(Produto.class));

        verify(entradaEstoqueService).registrar(
                2L,
                compraInicial.dataCompra(),
                compraInicial.quantidade(),
                compraInicial.custoUnitario()
        );
    }

    @Test
    void deveRejeitarCompraInicialNaAtualizacaoDoProduto() {
        CompraInicialDTO compraInicial = new CompraInicialDTO(
                LocalDate.of(2026,11,21),
                10,
                new BigDecimal("100.00")
        );

        ProdutoCadastroDTO dto = new ProdutoCadastroDTO(
                "Malbec",
                new BigDecimal("100.00"),
                0,
                1L,
                compraInicial
        );

        ConflitoException exception = assertThrows(
                ConflitoException.class,
                () -> produtoService.atualizar(1L, dto)
        );

        assertEquals(
                "A compra inicial só pode ser informada no cadastro do produto.",
                exception.getMessage()
        );

        verifyNoInteractions(
                produtoRepository,
                categoriaRepository,
                entradaEstoqueService
        );
    }
}