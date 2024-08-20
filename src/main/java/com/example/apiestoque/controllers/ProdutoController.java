package com.example.apiestoque.controllers;

import com.example.apiestoque.models.Produto;
import com.example.apiestoque.repository.ProdutoRepository;
import com.example.apiestoque.services.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;
    private final Validator validator;

    @Autowired
    public ProdutoController( ProdutoService produtoService, Validator validator) {
        this.produtoService = produtoService;
        this.validator = validator;
    }

    @GetMapping("/selecionar")
    @Operation (summary = "Lista todos os produtos", description = "Retorna uma lista de todos os produtos")
    @ApiResponses( value = {
            @ApiResponse (responseCode = "200", description = "Lista de produtos retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse (responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Exception.class)))
    })
    public List<Produto> buscarTodosProdutos() {
        return produtoService.buscarTodosProdutos();
    }

    @PostMapping("/inserir")
    @Operation (summary = "Inserir um novo produto", description = "Insere um novo  produto")
    @ApiResponses( value = {
            @ApiResponse (responseCode = "200", description = "Inserido com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse (responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Exception.class)))
    })
    public ResponseEntity<String> inserirProduto(@Valid @RequestBody Produto produto) {
        try {
            if(produto != null) {
                produtoService.salvarProduto(produto);
                return ResponseEntity.ok("Produto inserido com sucesso!");
            } else {
                throw new DataIntegrityViolationException("Valor nulo!");
            }
        } catch (DataIntegrityViolationException dive){
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/deletar/{id}")
    @Transactional
    @Operation (summary = "Exclui um produto", description = "Exclui um  produto passando o id")
    @ApiResponses( value = {
            @ApiResponse (responseCode = "200", description = "Excluido com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse (responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Exception.class)))
    })
    public ResponseEntity<String> excluirProduto(@Parameter(description = "ID do produto") @Valid @PathVariable int id) {
        Produto produtoExistente = produtoService.buscarProdutoPorId(id);
        if (produtoExistente != null) {
            produtoService.excluirProduto(id);
            return ResponseEntity.ok("Produto excluido com sucesso!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/atualizar/{id}")
    @Operation (summary = "Atualiza um produto", description = "Atualiza um  produto passando o id")
    @ApiResponses( value = {
            @ApiResponse (responseCode = "200", description = "Atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse (responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Exception.class)))
    })
    public ResponseEntity<String> atualizarProduto(@Valid @PathVariable int id, @RequestBody Produto produtoAtualizado) {
        Produto produtoExistente = produtoService.buscarProdutoPorId(id);
        if (produtoExistente != null) {
            Produto produto = produtoExistente;
            produto.setNome(produtoAtualizado.getNome());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            produtoService.salvarProduto(produto);
            return ResponseEntity.ok("Produto atualizado com sucesso!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/atualizarParcial/{id}")
    @Operation (summary = "Atualiza parcial um produto", description = "Atualiza parcial um  produto passando o id")
    @ApiResponses( value = {
            @ApiResponse (responseCode = "200", description = "Atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse (responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Exception.class)))
    })
    public ResponseEntity<?> atualizarParcial(@PathVariable int id, @RequestBody Map<String, Object> update) {
//        Optional<Produto> produtoExistente = produtoRepository.findById(id);

        try {
            Produto produto = produtoService.buscarProdutoPorId(id);
//            Produto produto = produtoExistente.get();

            // atualiza apenas aqueles que estão presentes no corpo da requisição
            if (update.containsKey("nome")) {
                produto.setNome((String) update.get("nome"));
            }
            if (update.containsKey("preco")) {
                String preco = update.get("preco").toString();
                produto.setPreco(Double.parseDouble(preco));
            }
            if (update.containsKey("descricao")) {
                produto.setDescricao((String) update.get("descricao"));
            }
            if (update.containsKey("quantidadeEstoque")) {
                produto.setQuantidadeEstoque((int) update.get("quantidadeEstoque"));
            }

            DataBinder binder = new DataBinder(produto);
            binder.setValidator(validator);
            binder.validate();
            BindingResult result = binder.getBindingResult();

            if (result.hasErrors()) {
                Map erros = validarProduto(result);
                return ResponseEntity.badRequest().body(erros);
            }
            Produto produtoSalvo = produtoService.salvarProduto(produto);
            return ResponseEntity.ok(produtoSalvo);
        } catch (RuntimeException r){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(r.getMessage());
        }
    }

    // encontrar pelo nome
    @PostMapping("/buscarPorNome")
    @Operation (summary = "Busca um produto por nome", description = "Buscar um  produto passando o nome como parametro")
    @ApiResponses( value = {
            @ApiResponse (responseCode = "200", description = "Buscado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse (responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Exception.class)))
    })
    public ResponseEntity<?> buscarProdutosPorNome(@RequestParam String nome) {
        List<Produto> recebido = produtoService.buscarPorNome(nome);
        if (!recebido.isEmpty()) {
            return new ResponseEntity<>(recebido, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado com esse nome!");
        }
    }

    @PostMapping("/contarPelaQuantidade")
    public ResponseEntity<?> contarPelaQuantidade(@RequestParam int quant) {
        int recebido = produtoService.excluirProdutosPorQuantidade(quant);
        if (recebido > 0) {
            return new ResponseEntity<>("Quantidade de produtos excluídos: " + recebido, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado com essa quantidade!");
        }
    }

    @GetMapping("/buscarPeloNomeQuantidade/{nome}/{quant}")
    public ResponseEntity<?> buscarPorNomeQuantidade(@PathVariable String nome, @PathVariable int quant) {
        List<Produto> produtos = produtoService.buscarProdutoPorNomeQuantidade(nome, quant);
        if (!produtos.isEmpty()) {
            return new ResponseEntity<>(produtos, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado");
        }
    }

    @GetMapping("/buscarPeloNomePreco")
    public ResponseEntity<?> buscarPorNomePreco(@RequestParam String nome, @RequestParam double preco) {
        List<Produto> produtos = produtoService.buscarProdutoPorNomePreco(nome, preco);
        if (!produtos.isEmpty()) {
            return new ResponseEntity<>(produtos, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado");
        }
    }

    @DeleteMapping("/excluirQuantidade/{quant}")
    @Transactional
    public ResponseEntity<String> excluirProdutoQuantidade(@PathVariable int quant) {

        int excluidos = produtoService.excluirProdutosPorQuantidade(quant);

        if (excluidos  > 0) {
            return ResponseEntity.status(HttpStatus.OK).body("Produto excluído com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado com a quantidade menor ou igual!");
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();
        result.getFieldErrors().forEach(fieldError ->
                errorMsg.append(fieldError.getDefaultMessage()).append("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg.toString());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<String> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    public Map<String, String> validarProduto(BindingResult resultado) {
        Map<String, String> erros = new HashMap<>();
        for (FieldError error : resultado.getFieldErrors()) {
            erros.put(error.getField(), error.getDefaultMessage());
        }
        return erros;
    }


}
