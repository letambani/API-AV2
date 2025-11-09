package br.fmp.av2.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError noHandlerFound(NoHandlerFoundException ex, HttpServletRequest req) {
        return ApiError.from(404, "Não encontrado", "Endpoint não encontrado", req.getRequestURI());
    }

    @ExceptionHandler({AlunoNotFoundException.class, NotFoundException.class, EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(RuntimeException ex, HttpServletRequest req) {
        return ApiError.from(404, "Não encontrado", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldErrorItem> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldErrorItem(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return ApiError.from(400, "Bad Request", "Erro de validação", req.getRequestURI(), fields);
    }

    @ExceptionHandler({ConstraintViolationException.class, HttpMessageNotReadableException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequest(RuntimeException ex, HttpServletRequest req) {
        return ApiError.from(400, "Bad Request", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError badCredentials(AuthenticationException ex, HttpServletRequest req) {
        return ApiError.from(401, "Unauthorized", "Credenciais inválidas", req.getRequestURI());
    }

    @ExceptionHandler({CpfDuplicadoException.class, DuplicateException.class, DuplicateKeyException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflict(RuntimeException ex, HttpServletRequest req) {
        String message = ex.getMessage();
        if (ex instanceof CpfDuplicadoException) {
            message = ex.getMessage();
        } else if (ex instanceof DataIntegrityViolationException) {
            String errorMsg = ex.getMessage();
            if (errorMsg != null && errorMsg.contains("cpf")) {
                message = "Já existe uma pessoa com cpf";
            } else {
                message = "Violação de integridade de dados";
            }
        }
        return ApiError.from(409, "Conflict", message, req.getRequestURI());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiError methodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return ApiError.from(405, "Method Not Allowed", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError generic(Exception ex, HttpServletRequest req) {
        if (ex instanceof AuthenticationException) {
            return ApiError.from(401, "Unauthorized", "Credenciais inválidas", req.getRequestURI());
        }
        return ApiError.from(500, "Internal Server Error", ex.getMessage() != null ? ex.getMessage() : "Erro interno do servidor", req.getRequestURI());
    }
}
