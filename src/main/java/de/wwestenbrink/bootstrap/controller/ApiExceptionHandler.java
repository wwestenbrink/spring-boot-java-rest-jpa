package de.wwestenbrink.bootstrap.controller;

import de.wwestenbrink.bootstrap.exception.AssetAlreadyExistsException;
import de.wwestenbrink.bootstrap.exception.AssetNotFoundException;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice()
public class ApiExceptionHandler {
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void handleUnknownException(Exception ex, WebRequest wr) {
    log.error("Unable to handle {}", wr.getDescription(false), ex);
  }

  @ExceptionHandler({
      HttpMessageNotReadableException.class,
      ServletRequestBindingException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleBindingExceptions(Exception ex, WebRequest wr) {
    log.error("Binding failed {}", wr.getDescription(false), ex);
  }

  @ExceptionHandler({
      MethodArgumentNotValidException.class,
      ConstraintViolationException.class,
      DataIntegrityViolationException.class
  })
  public ResponseEntity<?> handleValidationExceptions(Exception ex, WebRequest request) {
    return ResponseEntity.badRequest().build();
  }

  @ExceptionHandler({AssetNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  protected void handleAssetNotFound(Exception ex, WebRequest request) {
    log.warn("Asset not found {}", request.getDescription(false), ex);
  }

  @ExceptionHandler({AssetAlreadyExistsException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  protected void handleAssetAlreadyExists(Exception ex, WebRequest request) {
    log.warn("Asset already exists {}", request.getDescription(false), ex);
  }
}
