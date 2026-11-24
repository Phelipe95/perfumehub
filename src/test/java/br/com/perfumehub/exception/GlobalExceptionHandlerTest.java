package br.com.perfumehub.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @Test
    void deveRelancarErroDeIntegridadeComRestricaoDesconhecida() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ConstraintViolationException causa = new ConstraintViolationException(
                "Restrição desconhecida.",
                new SQLException("Falha simulada.", "23514"),
                "restricao_desconhecida"
        );

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "Falha ao salvar.",
                        causa
                );

        DataIntegrityViolationException resultado = assertThrows(
                DataIntegrityViolationException.class,
                () -> handler.tratarIntegridade(exception)
        );

        assertSame(exception, resultado);
    }

    @Test
    void deveRetornar409ParaConflitoEntreProdutoECategoria() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ConstraintViolationException causa = new ConstraintViolationException(
                "Vínculo inválido.",
                new SQLException("Falha simulada.", "23503"),
                "fk_produto_categoria"
        );

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "Falha na operação.",
                        causa
                );

        ResponseEntity<ErroRespostaDTO> resposta =
                handler.tratarIntegridade(exception);

        assertEquals(HttpStatus.CONFLICT, resposta.getStatusCode());
        assertEquals(
                List.of(
                        "A categoria não está disponível ou possui produtos vinculados. Atualize os dados e tente novamente."
                ),
                resposta.getBody().erros()
        );
    }

    @Test
    void deveRetornar409ParaConflitoEntreEntradaEProduto() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ConstraintViolationException causa = new ConstraintViolationException(
                "Vínculo inválido.",
                new SQLException("Falha simulada.", "23503"),
                "fk_entrada_estoque_produto"
        );

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "Falha na operação.",
                        causa
                );

        ResponseEntity<ErroRespostaDTO> resposta =
                handler.tratarIntegridade(exception);

        assertEquals(HttpStatus.CONFLICT, resposta.getStatusCode());
        assertEquals(
                List.of(
                        "O produto não está disponível ou possui compras registradas. Atualize os dados e tente novamente."
                ),
                resposta.getBody().erros()
        );
    }

    @Test
    void deveRelancarErroDeIntegridadeSemRestricaoIdentificavel() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("Falha sem restrição.");

        DataIntegrityViolationException resultado = assertThrows(
                DataIntegrityViolationException.class,
                () -> handler.tratarIntegridade(exception)
        );

        assertSame(exception, resultado);
    }
}