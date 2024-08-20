package com.example.apiestoque.services;

import com.example.apiestoque.models.Produto;
import com.example.apiestoque.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {
    private  final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> buscarTodosProdutos() {
        return produtoRepository.findAll();
    }

    @Transactional
    public Produto salvarProduto (Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto buscarProdutoPorId(int id) {
        return produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    @Transactional
    public Produto excluirProduto(int id) {
        Produto produto = buscarProdutoPorId(id);
        produtoRepository.deleteProdutoById(id);
        return produto;
    }

    public List <Produto>buscarPorNome(String nome) {
        return produtoRepository.findByNomeLikeIgnoreCase(nome);
    }

    public int excluirProdutosPorQuantidade(int quantidade) {
        int quantExcluidos = produtoRepository.countByQuantidadeEstoqueIsLessThanEqual(quantidade);
        if(quantExcluidos > 0) {
            produtoRepository.deleteByQuantidadeEstoqueIsLessThanEqual(quantidade);
        }
        return quantExcluidos;
    }

    public List<Produto> buscarProdutoPorNomeQuantidade(String nome, int quant) {
        return produtoRepository.findByNomeLikeIgnoreCaseAndQuantidadeEstoqueLessThanEqual(nome, quant);
    }

    public List<Produto> buscarProdutoPorNomePreco(String nome, double preco) {
        return produtoRepository.findByNomeLikeIgnoreCaseAndPrecoGreaterThanEqual(nome, preco);
    }
}
