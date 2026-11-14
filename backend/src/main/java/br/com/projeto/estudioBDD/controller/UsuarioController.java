package br.com.projeto.estudioBDD.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
//import br.com.projeto.estudioBDD.database.repository.UsuarioRepo; 
import br.com.projeto.estudioBDD.dto.UsuarioDTO;
import br.com.projeto.estudioBDD.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
   // private final UsuarioRepo usuarioRepo; 

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO.Response criar(@Valid @RequestBody UsuarioDTO.Request req) {
        return service.criar(req);
    }

    @GetMapping
    public List<UsuarioDTO.Response> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public UsuarioDTO.Response buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

 /*   @GetMapping("/teste-archunit")
   public long testarViolacao() {
    return usuarioRepo.count();

    }*/
}