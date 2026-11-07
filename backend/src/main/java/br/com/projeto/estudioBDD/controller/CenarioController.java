package br.com.projeto.estudioBDD.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import br.com.projeto.estudioBDD.dto.CenarioDto;
import br.com.projeto.estudioBDD.service.CenarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CenarioController {

    private final CenarioService service;

    @GetMapping("/historias/{historiaId}/cenarios")
    public List<CenarioDto.Response> listar(@PathVariable Long historiaId) {
        return service.listar(historiaId);
    }

    @PostMapping("/historias/{historiaId}/cenarios")
    @ResponseStatus(HttpStatus.CREATED)
    public CenarioDto.Response criar(@PathVariable Long historiaId,
                                     @RequestHeader("X-Usuario-Id") Long usuarioId,
                                     @Valid @RequestBody CenarioDto.Request req) {
        return service.criar(historiaId, usuarioId, req);
    }

    @PutMapping("/cenarios/{id}")
    public CenarioDto.Response atualizar(@PathVariable Long id,
                                         @RequestHeader("X-Usuario-Id") Long usuarioId,
                                         @Valid @RequestBody CenarioDto.Request req) {
        return service.atualizar(id, usuarioId, req);
    }

    @DeleteMapping("/cenarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id, @RequestHeader("X-Usuario-Id") Long usuarioId) {
        service.excluir(id, usuarioId);
    }
}
