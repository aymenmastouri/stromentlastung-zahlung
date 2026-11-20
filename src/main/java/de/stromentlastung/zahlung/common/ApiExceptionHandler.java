package de.stromentlastung.zahlung.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NichtGefunden.class)
    public ResponseEntity<ApiError> nichtGefunden(NichtGefunden ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("NICHT_GEFUNDEN", ex.getMessage()));
    }

    @ExceptionHandler(RegelVerletzung.class)
    public ResponseEntity<ApiError> regelVerletzung(RegelVerletzung ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler({ZugriffVerweigert.class, AccessDeniedException.class})
    public ResponseEntity<ApiError> zugriffVerweigert(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError("ZUGRIFF_VERWEIGERT", ex.getMessage()));
    }

    @ExceptionHandler(DienstNichtErreichbar.class)
    public ResponseEntity<ApiError> dienstNichtErreichbar(DienstNichtErreichbar ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiError("DIENST_NICHT_ERREICHBAR", ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> unlesbar(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(new ApiError("EINGABE_UNGUELTIG", ex.getMostSpecificCause().getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validierung(MethodArgumentNotValidException ex) {
        Map<String, String> fehler = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> fehler.put(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest().body(fehler);
    }
}
