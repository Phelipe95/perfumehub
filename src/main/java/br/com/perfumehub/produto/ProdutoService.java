package br.com.perfumehub.produto;

import br.com.perfumehub.categoria.Categoria;
import br.com.perfumehub.categoria.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            CategoriaRepository categoriaRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id).orElseThrow();
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public void excluirPorId(Long id) {
        if(produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Produto não encontrado");
        }
    }

    public Produto cadastrar(ProdutoCadastroDTO dto) {
        Categoria categoria = categoriaRepository
                .findById(dto.categoriaId())
                .orElseThrow(() ->
                        new NoSuchElementException("Categoria não encontrada.")
        );

        Produto produto = new Produto(
                dto.nome(),
                dto.preco(),
                dto.quantidadeEstoque(),
                categoria
        );
        return produtoRepository.save(produto);
    }

}
