package br.com.perfumehub.produto;

import br.com.perfumehub.categoria.Categoria;
import br.com.perfumehub.categoria.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
                1L
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

        doThrow(new RuntimeException("Erro ao excluir"))
                .when(produtoRepository)
                .deleteById(1L);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> produtoService.excluirPorId(1L)
        );

        assertEquals("Erro ao excluir", exception.getMessage());

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

        assertEquals("Produto não encontrado", exception.getMessage());

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
                99L
        );

        assertThrows(
                NoSuchElementException.class,
                () -> produtoService.cadastrar(dto)
        );

        verify(categoriaRepository).findById(99L);
        verify(produtoRepository, never()).save(any(Produto.class));
    }
}