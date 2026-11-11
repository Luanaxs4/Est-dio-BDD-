package br.com.projeto.estudioBDD.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class RegraNegocioException extends RuntimeException {

    private final HttpStatus status;

    private RegraNegocioException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    /** 404 - recurso não existe */
    public static RegraNegocioException naoEncontrado(String mensagem) {
        return new RegraNegocioException(HttpStatus.NOT_FOUND, mensagem);
    }

    /** 409 - conflito (duplicidade, estado que não permite alteração) */
    public static RegraNegocioException conflito(String mensagem) {
        return new RegraNegocioException(HttpStatus.CONFLICT, mensagem);
    }

    /** 403 - usuário sem permissão */
    public static RegraNegocioException proibido(String mensagem) {
        return new RegraNegocioException(HttpStatus.FORBIDDEN, mensagem);
    }

    /** 422 - regra de negócio violada */
    public static RegraNegocioException regra(String mensagem) {
        return new RegraNegocioException(HttpStatus.UNPROCESSABLE_CONTENT, mensagem);
    }
}
