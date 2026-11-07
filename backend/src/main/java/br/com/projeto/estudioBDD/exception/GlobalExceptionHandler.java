package br.com.projeto.estudioBDD.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ProblemDetail> negocio(RegraNegocioException e) {
        return resposta(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> validacao(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Dados inválidos.");
        pd.setProperty("campos", campos);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ProblemDetail> cabecalhoAusente(MissingRequestHeaderException e) {
        return resposta(HttpStatus.BAD_REQUEST, "Informe o cabeçalho " + e.getHeaderName() + ".");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> corpoInvalido(HttpMessageNotReadableException e) {
        return resposta(HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido. Verifique o JSON e os valores dos campos.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return resposta(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro '" + e.getName() + "'.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> integridade(DataIntegrityViolationException e) {
        return resposta(HttpStatus.CONFLICT, "Operação viola uma restrição de unicidade ou integridade.");
    }

    private ResponseEntity<ProblemDetail> resposta(HttpStatus status, String detalhe) {
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detalhe));
    }
}
