package br.com.perfumehub.produto;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import br.com.perfumehub.categoria.Categoria;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "quantidade_estoque", nullable = false)
    private int quantidadeEstoque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(name = "custo_medio", precision = 14, scale = 6)
    private BigDecimal custoMedio;

    protected Produto() {}

    public Produto(
            String nome,
            BigDecimal preco,
            int quantidadeEstoque,
            Categoria categoria
    ) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço inválido.");
        }
        if (quantidadeEstoque < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }

        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public String getNome(){
        return nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public int getQuantidadeEstoque(){
        return quantidadeEstoque;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public BigDecimal getCustoMedio() {
        return custoMedio;
    }

    public void alterarPreco(BigDecimal novoPreco) {
        if (novoPreco == null || novoPreco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo ou nulo.");
        }

        this.preco = novoPreco;
    }

    public void atualizar(
            String nome,
            BigDecimal preco,
            int quantidadeEstoque,
            Categoria categoria
    ) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }

        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço inválido.");
        }

        if (quantidadeEstoque < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        }

        if (categoria == null) {
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }

        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.categoria = categoria;
    }

    public void registrarEntrada(
            int quantidade,
            BigDecimal custoUnitario
    ) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero");
        }
        if (custoUnitario == null || custoUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Custo unitário inválido");
        }
        if (quantidadeEstoque > 0 && custoMedio == null) {
            throw new IllegalStateException("O estoque atual possui custo desconhecido.");
        }

        int novoSaldo = Math.addExact(quantidadeEstoque, quantidade);
        BigDecimal novoCustoMedio;

        if (quantidadeEstoque == 0) {
            novoCustoMedio = custoUnitario.setScale(6, RoundingMode.HALF_UP);
        } else {
            BigDecimal valorEstoqueAtual =
                    custoMedio.multiply(BigDecimal.valueOf(quantidadeEstoque));

            BigDecimal valorEntrada =
                    custoUnitario.multiply(BigDecimal.valueOf(quantidade));

            novoCustoMedio = valorEstoqueAtual.add(valorEntrada)
                    .divide(BigDecimal.valueOf(novoSaldo), 6, RoundingMode.HALF_UP);
        }

        this.quantidadeEstoque = novoSaldo;
        this.custoMedio = novoCustoMedio;
    }
}