package br.com.perfumehub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private String obterNomeRestricao(Throwable exception) {
        Throwable causa = exception;

        while (causa != null) {
            if (causa instanceof ConstraintViolationException violacao) {
                return violacao.getConstraintName();
            }

            causa = causa.getCause();
        }

        return null;
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErroRespostaDTO> tratarNaoEncontrado(
            NoSuchElementException exception
    ) {
        List<String> mensagens = List.of(exception.getMessage());

        ErroRespostaDTO respostaDTO = new ErroRespostaDTO(mensagens);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(respostaDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarValidacao(
            MethodArgumentNotValidException exception
    ) {
        List<FieldError> erros = exception.getBindingResult().getFieldErrors();

        List<String> mensagens = erros.stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        ErroRespostaDTO respostaDTO = new ErroRespostaDTO(mensagens);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(respostaDTO);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroRespostaDTO> tratarJsonInvalido(
            HttpMessageNotReadableException exception
    ) {
        List<String> mensagens = List.of(
                "JSON inválido ou dados incompatíveis com o formato esperado."
        );

        ErroRespostaDTO respostaDTO = new ErroRespostaDTO(mensagens);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(respostaDTO);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarConflito(
            ConflitoException exception
    ) {
        List<String> mensagens = List.of(exception.getMessage());

        ErroRespostaDTO resposta = new ErroRespostaDTO(mensagens);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(resposta);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErroRespostaDTO> tratarConcorrencia(
            OptimisticLockingFailureException exception
    ) {
        List<String> mensagens = List.of(
                "O produto foi alterado por outra operação. Atualize os dados e tente novamente."
        );

        ErroRespostaDTO resposta = new ErroRespostaDTO(mensagens);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(resposta);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroRespostaDTO> tratarIntegridade(
            DataIntegrityViolationException exception
    ) {
        String restricao = obterNomeRestricao(exception);

        if (restricao == null) {
            throw exception;
        }

        String mensagem = switch (restricao) {
            case "categoria_nome_key" ->
                    "Já existe uma categoria com esse nome.";

            case "fk_produto_categoria" ->
                    "A categoria não está disponível ou possui produtos vinculados. Atualize os dados e tente novamente.";

            case "fk_entrada_estoque_produto" ->
                    "O produto não está disponível ou possui compras registradas. Atualize os dados e tente novamente.";

            default -> throw exception;
        };

        ErroRespostaDTO resposta = new ErroRespostaDTO(
                List.of(mensagem)
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(resposta);
    }
}
