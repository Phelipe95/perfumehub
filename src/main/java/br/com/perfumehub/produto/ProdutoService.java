package br.com.perfumehub.produto;

import br.com.perfumehub.categoria.Categoria;
import br.com.perfumehub.categoria.CategoriaRepository;
import br.com.perfumehub.estoque.CompraInicialDTO;
import br.com.perfumehub.estoque.EntradaEstoqueService;
import br.com.perfumehub.exception.ConflitoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EntradaEstoqueService entradaEstoqueService;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            CategoriaRepository categoriaRepository,
            EntradaEstoqueService entradaEstoqueService
    ) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.entradaEstoqueService = entradaEstoqueService;
    }

    public Produto buscarPorId(Long id) {

        return produtoRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Produto não encontrado."));
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public void excluirPorId(Long id) {
        if(produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Produto não encontrado.");
        }
    }

    @Transactional
    public Produto cadastrar(ProdutoCadastroDTO dto) {
        if (dto.compraInicial() != null && dto.quantidadeEstoque() != 0) {
            throw new ConflitoException(
                    "Ao registrar uma compra inicial, o estoque inicial deve ser zero."
            );
        }

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

        Produto produtoSalvo = produtoRepository.save(produto);

        CompraInicialDTO compraInicial = dto.compraInicial();

        if (compraInicial != null) {
            entradaEstoqueService.registrar(
                    produtoSalvo.getId(),
                    compraInicial.dataCompra(),
                    compraInicial.quantidade(),
                    compraInicial.custoUnitario()
            );
        }

        return produtoSalvo;
    }

    public Produto atualizar(Long id, ProdutoCadastroDTO dto) {
        if (dto.compraInicial() != null) {
            throw new ConflitoException(
                    "A compra inicial só pode ser informada no cadastro do produto."
            );
        }

        Produto produto = buscarPorId(id);

        Categoria categoria = categoriaRepository
                .findById(dto.categoriaId())
                .orElseThrow(() ->
                        new NoSuchElementException("Categoria não encontrada.")
                );

        produto.atualizar(
                dto.nome(),
                dto.preco(),
                dto.quantidadeEstoque(),
                categoria
        );

        return produtoRepository.save(produto);
    }
}