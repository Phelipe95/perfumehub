package br.com.perfumehub.produto;

import br.com.perfumehub.categoria.Categoria;
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
    void deveSalvarProduto() {
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

        when(produtoRepository.save(produto))
                .thenReturn(produto);

        Produto produtoSalvo = produtoService.salvar(produto);

        assertEquals("Malbec", produtoSalvo.getNome());
        assertEquals(new BigDecimal("150.00"), produtoSalvo.getPreco());

        verify(produtoRepository).save(produto);
    }
}