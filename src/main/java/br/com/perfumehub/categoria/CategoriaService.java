package br.com.perfumehub.categoria;

import br.com.perfumehub.exception.ConflitoException;
import br.com.perfumehub.produto.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            ProdutoRepository produtoRepository
    ) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
    }

    public Categoria cadastrar(CategoriaCadastroDTO dto) {
        Categoria categoria = new Categoria(
                dto.nome(),
                dto.descricao()
        );

        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoria não encontrada."));
    }

    public Categoria atualizar(Long id, CategoriaCadastroDTO dto) {
        Categoria categoria = buscarPorId(id);

        if (categoriaRepository.existsByNomeAndIdNot(dto.nome(), id)) {
            throw new ConflitoException("Já existe uma categoria com esse nome.");
        }

        categoria.atualizar(
                dto.nome(),
                dto.descricao()
        );

        return categoriaRepository.save(categoria);
    }

    public  void excluirPorId(Long id) {
        Categoria categoria = buscarPorId(id);

        if (produtoRepository.existsByCategoria_Id(id)) {
            throw new ConflitoException("Não é possível excluir uma categoria com produtos vinculados.");
        }

        categoriaRepository.delete(categoria);

    }
}
