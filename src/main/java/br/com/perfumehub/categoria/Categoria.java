package br.com.perfumehub.categoria;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, unique = true, length = 100)
    private String nome;

    @Column(name = "descricao", length = 2500)
    private String descricao;

    protected Categoria() {}

    public Categoria(String nome, String descricao) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório");
        }

        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return  nome;
    }

    public String getDescricao() {
        return descricao;
    }
}
