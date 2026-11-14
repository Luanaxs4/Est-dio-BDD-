package br.com.projeto.estudioBDD.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import br.com.projeto.estudioBDD.dto.ProjetoDTO;
import br.com.projeto.estudioBDD.dto.ResumoProjeto;
import br.com.projeto.estudioBDD.service.ProjetoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjetoDTO.Response criar(@Valid @RequestBody ProjetoDTO.Request req) {
        return service.criar(req);
    }

    @GetMapping
    public List<ProjetoDTO.Response> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ProjetoDTO.Response buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/{id}/resumo")
    public ResumoProjeto resumo(@PathVariable Long id) {
        return service.resumo(id);
    }

    @PutMapping("/{id}")
    public ProjetoDTO.Response atualizar(@PathVariable Long id, @Valid @RequestBody ProjetoDTO.Request req) {
        return service.atualizar(id, req);
    }

 
}
