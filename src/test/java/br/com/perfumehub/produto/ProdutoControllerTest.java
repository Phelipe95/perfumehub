package br.com.perfumehub.produto;

import br.com.perfumehub.categoria.Categoria;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;

    @Test
    void deveRetornar404QuandoProdutoNaoExiste() throws Exception {

        Long id = 9999L;

        when(produtoService.buscarPorId(id))
                .thenThrow(new NoSuchElementException("Produto não encontrado."));

        mockMvc.perform(get("/produtos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erros[0]")
                        .value("Produto não encontrado."));
    }

    @Test
    void deveRetornar400QuandoProdutoForInvalido() throws Exception {

        String json = """
                {
                    "nome": "",
                    "preco": -50,
                    "quantidadeEstoque": -3,
                    "categoriaId": null
                }
                """;

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.length()").value(4))
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("O nome do produto é obrigatório.")))
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("O valor não pode ser negativo.")))
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("O estoque não pode ser negativo.")))
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("A categoria do produto é obrigatória.")));
    }

    @Test
    void deveRetornar400QuandoJsonForMalFormado() throws Exception {

        String json = """
            {
                "nome": "Malbec",
                "preco": 150,
                "categoriaId: 1
            }
            """;

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0]")
                        .value("JSON inválido ou dados incompatíveis com o formato esperado."));
    }

    @Test
    void deveRetornar404AoAtualizarProdutoInexistente() throws Exception {

        String json = """
            {
                "nome": "Lily",
                "preco": 180.00,
                "quantidadeEstoque": 5,
                "categoriaId": 2
            }
            """;

        when(produtoService.atualizar(eq(99L), any(ProdutoCadastroDTO.class)))
                .thenThrow(new NoSuchElementException("Produto não encontrado."));

        mockMvc.perform(put("/produtos/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erros[0]")
                        .value("Produto não encontrado."));
    }

    @Test
    void deveRetornar400AoAtualizarProdutoComDadosInvalidos() throws Exception {
        String json = """
                {
                    "nome": "",
                    "preco": -50,
                    "quantidadeEstoque": -3,
                    "categoriaId": null
                }
                """;

        mockMvc.perform(put("/produtos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.length()").value(4))
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("O nome do produto é obrigatório.")))
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("O valor não pode ser negativo.")))
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("O estoque não pode ser negativo.")))
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("A categoria do produto é obrigatória.")));

        verifyNoInteractions(produtoService);
    }

    @Test
    void deveAtualizarProdutoERetornar200() throws Exception {
        String json = """
                {
                    "nome": "Lily",
                    "preco": 180.00,
                    "quantidadeEstoque": 5,
                    "categoriaId": 2
                }
                """;

        Categoria categoria = new Categoria(
                "Feminino",
                "Perfumes femininos"
        );

        ReflectionTestUtils.setField(categoria, "id", 2L);

        Produto produto = new Produto(
                "Lily",
                new BigDecimal("180.00"),
                5,
                categoria
        );

        ReflectionTestUtils.setField(produto, "id", 1L);

        when(produtoService.atualizar(eq(1L), any(ProdutoCadastroDTO.class)))
                .thenReturn(produto);

        mockMvc.perform(put("/produtos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Lily"))
                .andExpect(jsonPath("$.preco").value(180.00))
                .andExpect(jsonPath("$.quantidadeEstoque").value(5))
                .andExpect(jsonPath("$.categoriaId").value(2));

        ProdutoCadastroDTO dtoEsperado = new ProdutoCadastroDTO(
                "Lily",
                new BigDecimal("180.00"),
                5,
                2L
        );

        verify(produtoService).atualizar(1L, dtoEsperado);
    }

    @Test
    void deveRetornar404AoAtualizarComCategoriaInexistente() throws Exception {
        String json = """
            {
                "nome": "Lily",
                "preco": 180.00,
                "quantidadeEstoque": 5,
                "categoriaId": 99
            }
            """;

        when(produtoService.atualizar(eq(1L), any(ProdutoCadastroDTO.class)))
                .thenThrow(new NoSuchElementException("Categoria não encontrada."));

        mockMvc.perform(put("/produtos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erros[0]")
                        .value("Categoria não encontrada."));

    }

    @Test
    void deveRetornar400AoAtualizarComJsonMalFormado() throws Exception {
        String json = """
            {
                "nome": "Lily",
                "preco": 180.00,
                "quantidadeEstoque": 5,
                "categoriaId": 2
            """;

        mockMvc.perform(put("/produtos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0]")
                        .value("JSON inválido ou dados incompatíveis com o formato esperado."));

        verifyNoInteractions(produtoService);
    }
}
