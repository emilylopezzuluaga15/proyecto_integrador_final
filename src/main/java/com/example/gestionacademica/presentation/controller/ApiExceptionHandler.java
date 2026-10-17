package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Exceptions.AsignacionMateriaProfesorNotFoundException;
import com.example.gestionacademica.application.Exceptions.AsistenciaNotFoundException;
import com.example.gestionacademica.application.Exceptions.BusinessException;
import com.example.gestionacademica.application.Exceptions.CarreraNotFoundException;
import com.example.gestionacademica.application.Exceptions.EstudianteNotFoundException;
import com.example.gestionacademica.application.Exceptions.GrupoNotFoundException;
import com.example.gestionacademica.application.Exceptions.InscripcionNotFoundException;
import com.example.gestionacademica.application.Exceptions.MateriaNotFoundException;
import com.example.gestionacademica.application.Exceptions.MovimientoNotaNotFoundException;
import com.example.gestionacademica.application.Exceptions.NivelMateriaNotFoundException;
import com.example.gestionacademica.application.Exceptions.NivelNotFoundException;
import com.example.gestionacademica.application.Exceptions.NotaNotFoundException;
import com.example.gestionacademica.application.Exceptions.ProfesorNotFoundException;
import com.example.gestionacademica.application.Exceptions.CredencialesInvalidasException;
import com.example.gestionacademica.application.Exceptions.UsuarioNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler({
            CarreraNotFoundException.class,
            NivelNotFoundException.class,
            MateriaNotFoundException.class,
            NivelMateriaNotFoundException.class,
            ProfesorNotFoundException.class,
            GrupoNotFoundException.class,
            AsignacionMateriaProfesorNotFoundException.class,
            EstudianteNotFoundException.class,
            InscripcionNotFoundException.class,
            AsistenciaNotFoundException.class,
            NotaNotFoundException.class,
            MovimientoNotaNotFoundException.class,
            UsuarioNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildBody(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildBody(HttpStatus.CONFLICT, "Violación de integridad de datos: revisa las referencias (llaves foráneas) o campos únicos/obligatorios."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Campos inválidos u obligatorios faltantes");
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        body.put("errores", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildBody(HttpStatus.BAD_REQUEST, "Cuerpo de la petición inválido o mal formado (revisa tipos y valores de ENUM)."));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildBody(HttpStatus.BAD_REQUEST, "Falta el parámetro obligatorio: " + ex.getParameterName()));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredenciales(CredencialesInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildBody(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    // Lo lanza @PreAuthorize cuando el rol no alcanza
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildBody(HttpStatus.FORBIDDEN, "Tu rol no tiene permiso para esta operación"));
    }

    // El detalle queda en el log; al cliente no se le exponen mensajes internos
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Error no controlado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildBody(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno. Intenta de nuevo o contacta al administrador."));
    }

    private Map<String, Object> buildBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}
