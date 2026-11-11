package br.com.perfumehub.estoque;

import br.com.perfumehub.produto.Produto;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "entrada_estoque")
public class EntradaEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "data_compra", nullable = false)
    private LocalDate dataCompra;

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Column(name = "custo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal custoUnitario;

    protected EntradaEstoque() {}

    public EntradaEstoque(
            Produto produto,
            LocalDate dataCompra,
            int quantidade,
            BigDecimal custoUnitario
    ) {
        if (produto == null) {
            throw new IllegalArgumentException("O produto é obrigatório.");
        }
        if (dataCompra == null) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (custoUnitario == null || custoUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Custo unitário inválido.");
        }

        this.produto = produto;
        this.dataCompra = dataCompra;
        this.quantidade = quantidade;
        this.custoUnitario = custoUnitario;
    }

    public Long getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getCustoUnitario() {
        return custoUnitario;
    }
}
