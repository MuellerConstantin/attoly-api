package de.x1c1b.attoly.api.web.v1.error;

import de.x1c1b.attoly.api.domain.EmailAlreadyInUseException;
import de.x1c1b.attoly.api.domain.EntityNotFoundException;
import de.x1c1b.attoly.api.domain.InvalidVerificationTokenException;
import de.x1c1b.attoly.api.web.v1.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
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

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exc,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<Object> details = exc.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationErrorDetails(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorDto dto = ErrorDto.builder()
                .message("The validation of the request body failed on a semantic level.")
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
            HttpStatus status,
            WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("A required query parameter was not specified for this request.")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new InvalidParameterErrorDetails(exc.getParameterName(), "Is required."))
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException exc,
                                                               HttpHeaders headers,
                                                               HttpStatus status,
                                                               WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("A required path variable was not specified for this request.")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new InvalidParameterErrorDetails(exc.getVariableName(), "Is required."))
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exc,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("The resource doesn't support the specified HTTP verb.")
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
            HttpStatus status,
            WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("The requested media type is not supported by the server.")
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
                                                                      HttpStatus status,
                                                                      WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("The requested media type is not supported by the server.")
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
                                                        HttpStatus status,
                                                        WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("The type of a body field is wrong.")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new InvalidParameterErrorDetails(exc.getPropertyName(),
                        "Should be of type %s.".formatted(Objects.requireNonNull(exc.getRequiredType()).getName())))
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exc,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("The specified body is not syntactically valid.")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException exc,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("The server encountered an internal error. Please retry the request.")
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
                                                                          HttpStatus status,
                                                                          WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("The server encountered an internal error. Please retry the request.")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        logger.error("Unexpected error occurred", exc);

        return handleExceptionInternal(exc, dto, headers, HttpStatus.valueOf(dto.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException exc,
                                                         HttpHeaders headers,
                                                         HttpStatus status,
                                                         WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("The server encountered an internal error. Please retry the request.")
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
                .message("The type of a request parameter is wrong.")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new InvalidParameterErrorDetails(exc.getParameter().getParameterName(),
                        "Should be of type %s.".formatted(Objects.requireNonNull(exc.getRequiredType()).getName())))
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<Object> handleEmailAlreadyInUser(EmailAlreadyInUseException exc,
                                                           WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("The validation of the request body failed on a semantic level.")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .detail(new ValidationErrorDetails("email",
                        messageSource.getMessage("de.x1c1b.attoly.api.web.v1.dto.validation.UniqueEmail.message",
                                new Object[]{}, LocaleContextHolder.getLocale())))
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException exc,
                                                       WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("The specified resource does not exist.")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<Object> handleInvalidVerificationToken(InvalidVerificationTokenException exc,
                                                                 WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("The verification token does not exist or is no longer valid.")
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
                .message("Username and/or password are invalid.")
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
                .message("Username and/or password are invalid.")
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
                .message("Access must be authenticated.")
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
                .message("Permissions for access are missing.")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDefault(Exception exc, WebRequest request) {

        ErrorDto dto = ErrorDto.builder()
                .message("The server encountered an internal error. Please retry the request.")
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(((ServletWebRequest) request).getRequest().getServletPath())
                .build();

        logger.error("Unexpected error occurred", exc);

        return new ResponseEntity<>(dto, new HttpHeaders(), HttpStatus.valueOf(dto.getStatus()));
    }
}
