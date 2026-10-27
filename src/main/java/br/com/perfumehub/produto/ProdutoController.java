package br.com.perfumehub.produto;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<ProdutoRespostaDTO> listarTodos() {
        List<ProdutoRespostaDTO> produtos = produtoService.listarTodos()
                .stream()
                .map(ProdutoRespostaDTO::fromEntity)
                .toList();

        return produtos;
    }

    @GetMapping("/{id}")
    public ProdutoRespostaDTO buscarPorId(@PathVariable Long id) {
        Produto buscarProduto = produtoService.buscarPorId(id);

        return ProdutoRespostaDTO.fromEntity(buscarProduto);
    }

    @PostMapping
    public ResponseEntity<ProdutoRespostaDTO> cadastrar(@RequestBody ProdutoCadastroDTO dto) {
        Produto produtoSalvo = produtoService.cadastrar(dto);

        ProdutoRespostaDTO resposta = ProdutoRespostaDTO.fromEntity(produtoSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
