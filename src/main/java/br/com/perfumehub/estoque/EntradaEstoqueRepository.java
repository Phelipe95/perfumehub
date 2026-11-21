package br.com.perfumehub.estoque;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntradaEstoqueRepository extends JpaRepository<EntradaEstoque, Long> {
    List<EntradaEstoque> findByProduto_Id(Long produtoId);
}
