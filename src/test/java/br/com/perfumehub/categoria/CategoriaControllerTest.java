package br.com.perfumehub.categoria;

import br.com.perfumehub.exception.ConflitoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
