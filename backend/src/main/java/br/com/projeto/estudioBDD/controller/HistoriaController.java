package br.com.projeto.estudioBDD.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import br.com.projeto.estudioBDD.database.model.enums.Prioridade;
import br.com.projeto.estudioBDD.dto.HistoriaDto;
import br.com.projeto.estudioBDD.dto.HistoriaResumo;
import br.com.projeto.estudioBDD.service.ExportacaoService;
import br.com.projeto.estudioBDD.service.HistoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HistoriaController {

    private final HistoriaService service;
    private final ExportacaoService exportacaoService;

    @PostMapping("/projetos/{projetoId}/historias")
    @ResponseStatus(HttpStatus.CREATED)
    public HistoriaDto.Response criar(@PathVariable Long projetoId,
                                      @RequestHeader("X-Usuario-Id") Long usuarioId,
                                      @Valid @RequestBody HistoriaDto.Request req) {
        return service.criar(projetoId, usuarioId, req);
    }

    @GetMapping("/projetos/{projetoId}/historias")
    public List<HistoriaResumo> listar(@PathVariable Long projetoId,
                                       @RequestParam(required = false) Prioridade prioridade) {
        return service.listarPorProjeto(projetoId, prioridade);
    }

    @GetMapping("/historias/{id}")
    public HistoriaDto.Response buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/historias/{id}")
    public HistoriaDto.Response atualizar(@PathVariable Long id,
                                          @RequestHeader("X-Usuario-Id") Long usuarioId,
                                          @Valid @RequestBody HistoriaDto.Request req) {
        return service.atualizar(id, usuarioId, req);
    }

    @DeleteMapping("/historias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id, @RequestHeader("X-Usuario-Id") Long usuarioId) {
        service.excluir(id, usuarioId);
    }

    /** Exporta a história como arquivo .feature (Gherkin em português). */
    @GetMapping("/historias/{id}/feature")
    public ResponseEntity<String> exportar(@PathVariable Long id) {
        ExportacaoService.Arquivo arquivo = exportacaoService.exportar(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(arquivo.nome(), StandardCharsets.UTF_8).build().toString())
                .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                .body(arquivo.conteudo());
    }
}
