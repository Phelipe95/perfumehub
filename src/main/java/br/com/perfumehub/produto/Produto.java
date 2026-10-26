package br.com.perfumehub.produto;

import jakarta.persistence.*;
import java.math.BigDecimal;

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

    protected Produto() {}

    public Produto(String nome, BigDecimal preco, int quantidadeEstoque, Categoria categoria) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço inválido");
        }
        if (quantidadeEstoque < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("A categoria é obrigatória");
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

    public void alterarPreco(BigDecimal novoPreco) {
        if (novoPreco == null || novoPreco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo ou nulo");
        }

        this.preco = novoPreco;
    }
}