package br.com.perfumehub.categoria;

import br.com.perfumehub.exception.ConflitoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.dao.DataIntegrityViolationException;
import org.hibernate.exception.ConstraintViolationException;

import java.util.NoSuchElementException;
import java.sql.SQLException;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @Test
    void deveAtualizarCategoriaERetornar200() throws Exception {
        String json = """
                {
                    "nome": "Feminino",
                    "descricao": "Perfumes femininos"
                }
                """;

        Categoria categoria = new Categoria(
                "Feminino",
                "Perfumes femininos"
        );

        ReflectionTestUtils.setField(categoria, "id", 1L);

        when(categoriaService.atualizar(eq(1L), any(CategoriaCadastroDTO.class)))
                .thenReturn(categoria);

        mockMvc.perform(put("/categorias/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Feminino"))
                .andExpect(jsonPath("$.descricao").value("Perfumes femininos"));

        CategoriaCadastroDTO dtoEsperado = new CategoriaCadastroDTO(
                "Feminino",
                "Perfumes femininos"
        );

        verify(categoriaService).atualizar(1L, dtoEsperado);
    }

    @Test
    void deveRetornar404AoAtualizarCategoriaInexistente() throws Exception {
        String json = """
            {
                "nome": "Feminino",
                "descricao": "Perfumes femininos"
            }
            """;

        when(categoriaService.atualizar(eq(99L), any(CategoriaCadastroDTO.class)))
                .thenThrow(new NoSuchElementException("Categoria não encontrada."));

        mockMvc.perform(put("/categorias/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erros[0]")
                        .value("Categoria não encontrada."));

        verify(categoriaService).atualizar(eq(99L), any(CategoriaCadastroDTO.class));
    }

    @Test
    void deveRetornar400AoAtualizarCategoriaComNomeInvalido() throws Exception {
        String json = """
            {
                "nome": "   ",
                "descricao": "Perfumes femininos"
            }
            """;

        mockMvc.perform(put("/categorias/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0]")
                        .value("O nome da categoria é obrigatório."));

        verifyNoInteractions(categoriaService);
    }

    @Test
    void deveRetornar400AoAtualizarCategoriaComJsonMalformado() throws Exception {
        String json = """
        {
            "nome": "Feminino",
            "descricao": "Perfumes femininos"
        """;

        mockMvc.perform(put("/categorias/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0]")
                        .value("JSON inválido ou dados incompatíveis com o formato esperado."));

        verifyNoInteractions(categoriaService);
    }

    @Test
    void deveRetornar409AoAtualizarCategoriaComNomeJaUtilizado() throws Exception {
        String json = """
            {
                "nome": "Feminino",
                "descricao": "Perfumes femininos"
            }
            """;

        when(categoriaService.atualizar(eq(1L), any(CategoriaCadastroDTO.class)))
                .thenThrow(new ConflitoException("Já existe uma categoria com esse nome."));

        mockMvc.perform(put("/categorias/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erros[0]")
                        .value("Já existe uma categoria com esse nome."));

        verify(categoriaService).atualizar(eq(1L), any(CategoriaCadastroDTO.class));
    }

    @Test
    void deveExcluirCategoriaERetornar204() throws Exception {
        mockMvc.perform(delete("/categorias/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(categoriaService).excluirPorId(1L);
    }

    @Test
    void deveRetornar404AoExcluirCategoriaInexistente() throws Exception {
        doThrow(new NoSuchElementException("Categoria não encontrada."))
                .when(categoriaService)
                .excluirPorId(99L);

        mockMvc.perform(delete("/categorias/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erros[0]")
                        .value("Categoria não encontrada."));

        verify(categoriaService).excluirPorId(99L);
    }

    @Test
    void deveRetornar409AoExcluirCategoriaComProdutosVinculados() throws Exception {
        doThrow(new ConflitoException(
                "Não é possível excluir uma categoria com produtos vinculados."))
                .when(categoriaService)
                .excluirPorId(1L);

        mockMvc.perform(delete("/categorias/{id}", 1L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erros[0]")
                        .value("Não é possível excluir uma categoria com produtos vinculados."));

        verify(categoriaService).excluirPorId(1L);
    }

    @Test
    void deveRetornar409AoCadastrarCategoriaComNomeJaUtilizado() throws Exception {
        String json = """
            {
                "nome": "Feminino",
                "descricao": "Perfumes femininos"
            }
            """;

        when(categoriaService.cadastrar(any(CategoriaCadastroDTO.class)))
                .thenThrow(new ConflitoException("Já existe uma categoria com esse nome."));

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erros[0]")
                        .value("Já existe uma categoria com esse nome."));
    }

    @Test
    void deveRetornar400AoCadastrarCategoriaComCamposAcimaDosLimites() throws Exception {
        String json = """
        {
            "nome": "%s",
            "descricao": "%s"
        }
        """.formatted(
                "a".repeat(101),
                "b".repeat(256)
        );

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.length()").value(2))
                .andExpect(jsonPath("$.erros").value(hasItem("O nome da categoria deve ter no máximo 100 caracteres.")))
                .andExpect(jsonPath("$.erros").value(hasItem("A descrição da categoria deve ter no máximo 255 caracteres.")));

        verifyNoInteractions(categoriaService);
    }

    @Test
    void deveRetornar409QuandoBancoRejeitarNomeDuplicado() throws Exception {
        ConstraintViolationException causa = new ConstraintViolationException(
                "Restrição de unicidade violada.",
                new SQLException("Nome duplicado.", "23505"),
                "categoria_nome_key"
        );

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "Não foi possível salvar a categoria.",
                        causa
                );

        when(categoriaService.cadastrar(any(CategoriaCadastroDTO.class)))
                .thenThrow(exception);

        String json = """
        {
            "nome": "Feminino",
            "descricao": "Perfumes femininos"
        }
        """;

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erros[0]")
                        .value("Já existe uma categoria com esse nome."));

        verify(categoriaService).cadastrar(any(CategoriaCadastroDTO.class));
    }
}
