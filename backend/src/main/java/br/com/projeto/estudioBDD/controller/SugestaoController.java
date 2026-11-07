package br.com.projeto.estudioBDD.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import br.com.projeto.estudioBDD.database.model.enums.DecisaoSugestao;
import br.com.projeto.estudioBDD.dto.SugestaoDto;
import br.com.projeto.estudioBDD.service.SugestaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SugestaoController {

    private final SugestaoService service;

    @PostMapping("/historias/{historiaId}/sugestoes")
    @ResponseStatus(HttpStatus.CREATED)
    public SugestaoDto.Response registrar(@PathVariable Long historiaId,
                                          @RequestHeader("X-Usuario-Id") Long usuarioId,
                                          @Valid @RequestBody SugestaoDto.Request req) {
        return service.registrar(historiaId, usuarioId, req);
    }

    @GetMapping("/historias/{historiaId}/sugestoes")
    public List<SugestaoDto.Response> listar(@PathVariable Long historiaId,
                                             @RequestParam(required = false) DecisaoSugestao decisao) {
        return service.listar(historiaId, decisao);
    }

    @PutMapping("/sugestoes/{id}/decisao")
    public SugestaoDto.Response decidir(@PathVariable Long id,
                                        @RequestHeader("X-Usuario-Id") Long usuarioId,
                                        @Valid @RequestBody SugestaoDto.DecisaoRequest req) {
        return service.decidir(id, usuarioId, req);
    }
}
