package com.example.apiestoque.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Entity
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do produto", example = "1234")
    private long id;

    @NotNull(message = "O nome não pode ser nulo")
    @Size(min = 2, message = "O nome deve ter pelo menos 2 caracteres")
    @Schema(description = "Nome do produto", example = "Camiseta")
    private String nome;

    @Schema(description = "Descricão do produto", example = "Camiseta branca")
    private String descricao;

    @NotNull(message = "O preço não pode ser nulo")
    @Min(value = 0, message = "O preço deve ser pelo menos 0")
    @Schema(description = "Preço do produto", example = "10.00")
    private double preco;
    @Column(name = "quantidadeestoque")
    @NotNull(message = "O preço não pode ser nulo")
    @Min(value = 0, message = "O preço deve ser pelo menos 0")
    @Schema(description = "Quantidade de estoque do produto", example = "10")
    private int quantidadeEstoque;

    public Produto(long id, String nome, String descricao, double preco, int quantidadeEstoque) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Produto() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco=" + preco +
                ", quantidadeEstoque=" + quantidadeEstoque +
                '}';
    }
}


