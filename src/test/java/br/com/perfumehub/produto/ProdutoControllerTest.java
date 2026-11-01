package br.com.perfumehub.produto;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                        .value(hasItem("O nome do produto é obrigatório")))
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("O valor não pode ser negativo")))
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("O estoque não pode ser negativo")))
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("A categoria do produto é obrigatória")));
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
}
