package taskManager.exception;

import taskManager.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TarefaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleTarefaNaoEncontrada(TarefaNaoEncontradaException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Tarefa não encontrada",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IdNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleIdNaoEncontrado(IdNaoEncontradoException ex){
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Id não encontrada",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TransicaoStatusInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleTransicaoInvalida(TransicaoStatusInvalidaException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Transição de status inválida",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + (e.getDefaultMessage() != null ? e.getDefaultMessage() : "valor inválido"))
                .collect(Collectors.joining("; "));
        return badRequest("Dados inválidos", mensagem);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleParametroInvalido(MethodArgumentTypeMismatchException ex) {
        return badRequest("Parâmetro inválido", "Valor inválido para o parâmetro '" + ex.getName() + "': " + ex.getValue());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleCorpoInvalido(HttpMessageNotReadableException ex) {
        return badRequest("Requisição inválida", "Corpo da requisição inválido ou com valores não reconhecidos (verifique status, prioridade e datas)");
    }

    private ResponseEntity<ErrorResponse> badRequest(String titulo, String mensagem) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                titulo,
                mensagem
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
