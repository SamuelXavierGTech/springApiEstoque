package com.example.apiestoque.repository;

import com.example.apiestoque.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  ProdutoRepository extends JpaRepository<Produto, Integer> {
    int deleteProdutoById(int id);
    List<Produto> findByNomeLikeIgnoreCase(String nome);

    int countByQuantidadeEstoqueIsLessThanEqual(int quant);

    void deleteByQuantidadeEstoqueIsLessThanEqual(int quant);


    List<Produto> findByNomeLikeIgnoreCaseAndPrecoGreaterThanEqual(String nome, double preco);

    List<Produto> findByNomeLikeIgnoreCaseAndQuantidadeEstoqueLessThanEqual(String nome, int quant);
}
