package br.com.perfumehub.estoque;

import br.com.perfumehub.produto.Produto;
import br.com.perfumehub.produto.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
public class EntradaEstoqueService {

    private final ProdutoRepository produtoRepository;
    private final EntradaEstoqueRepository entradaEstoqueRepository;

    public EntradaEstoqueService(
            ProdutoRepository produtoRepository,
            EntradaEstoqueRepository entradaEstoqueRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.entradaEstoqueRepository = entradaEstoqueRepository;
    }

    @Transactional
    public EntradaEstoque registrar(
            Long produtoId,
            LocalDate dataCompra,
            int quantidade,
            BigDecimal custoUnitario
    ) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new NoSuchElementException("Produto não encontrado.")
        );

        EntradaEstoque entrada = new EntradaEstoque(
                produto,
                dataCompra,
                quantidade,
                custoUnitario
        );

        produto.registrarEntrada(quantidade, custoUnitario);

        return entradaEstoqueRepository.save(entrada);
    }
}
