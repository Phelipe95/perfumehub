package br.com.perfumehub.categoria;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<Categoria> cadastrar(@RequestBody CategoriaCadastroDTO dto) {

        Categoria categoriaSalva = categoriaService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
    }
}
