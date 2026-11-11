package br.com.perfumehub.estoque;

import br.com.perfumehub.categoria.Categoria;
import br.com.perfumehub.produto.Produto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EntradaEstoqueController.class)
class EntradaEstoqueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EntradaEstoqueService entradaEstoqueService;

    @Test
    void deveRegistrarEntradaERetornar201() throws Exception {
        String json = """
                {
                    "produtoId": 1,
                    "dataCompra": "2026-11-08",
                    "quantidade": 10,
                    "custoUnitario": 100.00
                }
                """;

        Categoria categoria = new Categoria(
                "Feminino",
                "Perfumes femininos"
        );

        Produto produto = new Produto(
                "Lily",
                new BigDecimal("180.00"),
                5,
                categoria
        );

        ReflectionTestUtils.setField(produto, "id", 1L);

        LocalDate dataCompra = LocalDate.of(2026, 11, 8);
        BigDecimal custoUnitario = new BigDecimal("100.00");

        EntradaEstoque entrada = new EntradaEstoque(
                produto,
                dataCompra,
                10,
                custoUnitario
        );

        ReflectionTestUtils.setField(entrada, "id", 2L);

        when(entradaEstoqueService.registrar(1L, dataCompra, 10, custoUnitario))
                .thenReturn(entrada);

        mockMvc.perform(post("/entradas-estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.produtoId").value(1))
                .andExpect(jsonPath("$.dataCompra").value("2026-11-08"))
                .andExpect(jsonPath("$.quantidade").value(10))
                .andExpect(jsonPath("$.custoUnitario").value(100.0));
    }

    @Test
    void deveRetornar404AoRegistrarEntradaParaProdutoInexistente() throws Exception {
        String json = """
                {
                    "produtoId": 99,
                    "dataCompra": "2026-11-08",
                    "quantidade": 10,
                    "custoUnitario": 100.00
                }
                """;

        LocalDate dataCompra = LocalDate.of(2026, 11, 8);
        BigDecimal custoUnitario = new BigDecimal("100.00");

        when(entradaEstoqueService.registrar(99L, dataCompra, 10, custoUnitario))
                .thenThrow(new NoSuchElementException("Produto não encontrado."));

        mockMvc.perform(post("/entradas-estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erros[0]").value("Produto não encontrado."));
    }

    @Test
    void deveRetornar400AoRegistrarEntradaSemCamposObrigatorios() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/entradas-estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.length()").value(4));

        verifyNoInteractions(entradaEstoqueService);
    }

    @Test
    void deveRetornar400AoRegistrarEntradaComQuantidadeZeroECustoNegativo() throws Exception {
        String json = """
        {
            "produtoId": 1,
            "dataCompra": "2026-11-08",
            "quantidade": 0,
            "custoUnitario": -0.01
        }
        """;

        mockMvc.perform(post("/entradas-estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("A quantidade deve ser maior que zero.")))
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("O custo unitário não pode ser negativo.")))
                .andExpect(jsonPath("$.erros.length()").value(2));

        verifyNoInteractions(entradaEstoqueService);
    }

    @Test
    void deveRetornar400AoRegistrarEntradaComCustoComMaisDeDuasCasasDecimais() throws Exception {
        String json = """
        {
            "produtoId": 1,
            "dataCompra": "2026-11-08",
            "quantidade": 10,
            "custoUnitario": 100.001
        }
        """;

        mockMvc.perform(post("/entradas-estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros")
                        .value(hasItem("O custo unitário deve ter até 8 dígitos inteiros e 2 casas decimais.")))
                .andExpect(jsonPath("$.erros.length()").value(1));

        verifyNoInteractions(entradaEstoqueService);
    }

    @Test
    void deveRetornar400AoRegistrarEntradaComJsonMalformado() throws Exception {
        String json = """
        {
            "produtoId": 1,
            "dataCompra": "2026-11-08",
            "quantidade": 10,
            "custoUnitario": 100.00
        """;

        mockMvc.perform(post("/entradas-estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0]")
                        .value("JSON inválido ou dados incompatíveis com o formato esperado."));

        verifyNoInteractions(entradaEstoqueService);
    }
}
