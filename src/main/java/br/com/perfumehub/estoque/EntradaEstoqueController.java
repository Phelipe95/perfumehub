package br.com.perfumehub.estoque;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/entradas-estoque")
public class EntradaEstoqueController {

    private final EntradaEstoqueService entradaEstoqueService;

    public EntradaEstoqueController(
            EntradaEstoqueService entradaEstoqueService
    ) {
        this.entradaEstoqueService = entradaEstoqueService;
    }

    @PostMapping
    public ResponseEntity<EntradaEstoqueRespostaDTO> registrar(
            @Valid @RequestBody EntradaEstoqueCadastroDTO dto
    ) {
        EntradaEstoque entradaEstoque = entradaEstoqueService.registrar(
                dto.produtoId(),
                dto.dataCompra(),
                dto.quantidade(),
                dto.custoUnitario()
        );

        EntradaEstoqueRespostaDTO entradaDTO = EntradaEstoqueRespostaDTO
                .fromEntity(entradaEstoque);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(entradaDTO);
    }
}
