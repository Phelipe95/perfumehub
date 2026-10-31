package br.com.perfumehub.categoria;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaRespostaDTO> cadastrar(
            @Valid @RequestBody CategoriaCadastroDTO dto) {

        Categoria categoriaSalva = categoriaService.cadastrar(dto);

        CategoriaRespostaDTO resposta = CategoriaRespostaDTO.fromEntity(categoriaSalva);

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    public List<CategoriaRespostaDTO> listarTodas() {
        List<CategoriaRespostaDTO> categorias = categoriaService.listarTodas()
                .stream()
                .map(CategoriaRespostaDTO::fromEntity)
                .toList();

        return categorias;
    }

    @GetMapping("/{id}")
    public CategoriaRespostaDTO buscarPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.buscarPorId(id);

        return CategoriaRespostaDTO.fromEntity(categoria);
    }


}
