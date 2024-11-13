package de.x1c1b.attoly.api.web.v1.error;

import de.x1c1b.attoly.api.domain.exception.*;
import de.x1c1b.attoly.api.web.v1.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestErrorHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(RestErrorHandler.class);

    private final MessageSource messageSource;

    @Autowired
    public RestErrorHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected String getMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exc,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<Object> details = exc.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationErrorDetails(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorDto dto = ErrorDto.builder()
                .error("ValidationError")
                .message(getMessage("ValidationError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .details(details)
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exc,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("MissingQueryParameterError")
                .message(getMessage("MissingQueryParameterError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new InvalidParameterErrorDetails(exc.getParameterName(),
                        getMessage("javax.validation.constraints.NotNull.message", null)))
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException exc,
                                                               HttpHeaders headers,
                                                               HttpStatusCode status,
                                                               WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("MissingPathVariableError")
                .message(getMessage("MissingPathVariableError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new InvalidParameterErrorDetails(exc.getVariableName(),
                        getMessage("javax.validation.constraints.NotNull.message", null)))
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exc,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("HttpRequestMethodNotSupportedError")
                .message(getMessage("HttpRequestMethodNotSupportedError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        headers.setAllow(Objects.requireNonNull(exc.getSupportedHttpMethods()));

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exc,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("HttpMediaTypeNotSupportedError")
                .message(getMessage("HttpMediaTypeNotSupportedError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        headers.setAccept(Objects.requireNonNull(exc.getSupportedMediaTypes()));

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException exc,
                                                                      HttpHeaders headers,
                                                                      HttpStatusCode status,
                                                                      WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("HttpMediaTypeNotSupportedError")
                .message(getMessage("HttpMediaTypeNotSupportedError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        headers.setAccept(Objects.requireNonNull(exc.getSupportedMediaTypes()));

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException exc,
                                                        HttpHeaders headers,
                                                        HttpStatusCode status,
                                                        WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("TypeMismatchError")
                .message(getMessage("TypeMismatchError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new InvalidParameterErrorDetails(exc.getPropertyName(),
                        getMessage("de.x1c1b.attoly.api.web.v1.dto.validation.Type.message", new Object[]{exc.getRequiredType().getName()})))
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exc,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InvalidPayloadFormatError")
                .message(getMessage("InvalidPayloadFormatError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException exc,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InternalError")
                .message(getMessage("InternalError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        logger.error("Unexpected error occurred", exc);

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException exc,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InternalError")
                .message(getMessage("InternalError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        logger.error("Unexpected error occurred", exc);

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exc,
                                                                   WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("TypeMismatchError")
                .message(getMessage("TypeMismatchError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new InvalidParameterErrorDetails(exc.getParameter().getParameterName(),
                        getMessage("de.x1c1b.attoly.api.web.v1.dto.validation.Type.message", new Object[]{exc.getRequiredType().getName()})))
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<Object> handleEmailAlreadyInUse(EmailAlreadyInUseException exc,
                                                          WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("ValidationError")
                .message(getMessage("ValidationError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new ValidationErrorDetails("email",
                        getMessage("de.x1c1b.attoly.api.web.v1.dto.validation.UniqueEmail.message", null)))
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException exc,
                                                       WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("NotFoundError")
                .message(getMessage("NotFoundError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(MustBeAdministrableException.class)
    public ResponseEntity<Object> handleMustBeAdministrable(MustBeAdministrableException exc,
                                                            WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("MustBeAdministrableError")
                .message(getMessage("MustBeAdministrableError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<Object> handleInvalidVerificationToken(InvalidVerificationTokenException exc,
                                                                 WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InvalidVerificationTokenError")
                .message(getMessage("InvalidVerificationTokenError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.GONE.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(InvalidResetTokenException.class)
    public ResponseEntity<Object> handleInvalidResetToken(InvalidResetTokenException exc,
                                                          WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InvalidResetTokenError")
                .message(getMessage("InvalidResetTokenError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.GONE.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFound(UsernameNotFoundException exc,
                                                         WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InvalidCredentialsError")
                .message(getMessage("InvalidCredentialsError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Object> handleDisabled(DisabledException exc,
                                                         WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("AccountDisabledError")
                .message(getMessage("AccountDisabledError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Object> handleLocked(LockedException exc,
                                                         WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("AccountLockedError")
                .message(getMessage("AccountLockedError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException exc,
                                                       WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InvalidCredentialsError")
                .message(getMessage("InvalidCredentialsError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthentication(AuthenticationException exc,
                                                       WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("AuthenticationError")
                .message(getMessage("AuthenticationError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException exc,
                                                     WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InsufficientPermissionsError")
                .message(getMessage("InsufficientPermissionsError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDefault(Exception exc, WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .error("InternalError")
                .message(getMessage("InternalError.message", null))
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        logger.error("Unexpected error occurred", exc);

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }
}
