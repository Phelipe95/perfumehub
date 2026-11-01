package br.com.perfumehub.exception;

import java.util.List;

public record ErroRespostaDTO(
        List<String> erros
) {
}
