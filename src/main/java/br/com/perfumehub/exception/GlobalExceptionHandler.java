package br.com.perfumehub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

}
