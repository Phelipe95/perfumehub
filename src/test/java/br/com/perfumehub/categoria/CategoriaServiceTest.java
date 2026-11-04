package br.com.perfumehub.categoria;

import br.com.perfumehub.exception.ConflitoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void deveAtualizarCategoriaExistente() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        CategoriaCadastroDTO cadastroDTO = new CategoriaCadastroDTO(
                "Feminino",
                "Perfumes femininos"
        );

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(categoriaRepository.save(categoria))
                .thenReturn(categoria);

        Categoria resultado = categoriaService.atualizar(1L, cadastroDTO);

        assertEquals("Feminino", resultado.getNome());
        assertEquals("Perfumes femininos", resultado.getDescricao());

        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void deveLancarExcecaoAoAtualizarCategoriaInexistente() {
        CategoriaCadastroDTO cadastroDTO = new CategoriaCadastroDTO(
                "Feminino",
                "Perfumes femininos"
        );

        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> categoriaService.atualizar(99L, cadastroDTO)
        );

        assertEquals("Categoria não encontrada.", exception.getMessage());

        verify(categoriaRepository).findById(99L);
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void deveRejeitarAtualizacaoComNomeJaUtilizado() {
        Categoria categoria = new Categoria(
                "Masculino",
                "Perfumes masculinos"
        );

        CategoriaCadastroDTO dto = new CategoriaCadastroDTO(
                "Feminino",
                "Perfumes femininos"
        );

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(categoriaRepository.existsByNomeAndIdNot(dto.nome(), 1L))
                .thenReturn(true);

        ConflitoException exception = assertThrows(ConflitoException.class,
                () -> categoriaService.atualizar(1L, dto));

        assertEquals("Já existe uma categoria com esse nome.", exception.getMessage());
        assertEquals("Masculino", categoria.getNome());
        assertEquals("Perfumes masculinos", categoria.getDescricao());

        verify(categoriaRepository, never()).save(any(Categoria.class));
    }
}
