package br.com.projeto.estudioBDD.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.projeto.estudioBDD.database.model.Usuario;
import br.com.projeto.estudioBDD.database.repository.UsuarioRepo;
import br.com.projeto.estudioBDD.dto.UsuarioDTO;
import br.com.projeto.estudioBDD.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepo repository;

    @Transactional
    public UsuarioDTO.Response criar(UsuarioDTO.Request req) {
        String email = req.email().trim().toLowerCase();
        if (repository.existsByEmail(email)) {
            throw RegraNegocioException.conflito("Já existe um usuário com este e-mail.");
        }
        Usuario usuario = repository.save(Usuario.builder()
        .nome(req.nome().trim())
        .email(email)
        .build());
        return UsuarioDTO.Response.from(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO.Response> listar() {
        return repository.findAll().stream().map(UsuarioDTO.Response::from).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO.Response buscar(Long id) {
        return UsuarioDTO.Response.from(obterEntidade(id));
    }

    /** Usado pelos outros serviços. */
    public Usuario obterEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> RegraNegocioException.naoEncontrado("Usuário " + id + " não encontrado."));
    }

   
}
