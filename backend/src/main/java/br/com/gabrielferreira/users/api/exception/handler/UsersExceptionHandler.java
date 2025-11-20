package br.com.gabrielferreira.users.api.exception.handler;

import br.com.gabrielferreira.users.api.exception.dtos.ProblemDetailDTO;
import br.com.gabrielferreira.users.api.exception.dtos.ProblemDetailFieldDTO;
import br.com.gabrielferreira.users.api.exception.enums.ProblemDetailType;
import br.com.gabrielferreira.users.api.exception.mappers.ProblemDetailMapper;
import br.com.gabrielferreira.users.domain.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.domain.exceptions.EntityInUseException;
import br.com.gabrielferreira.users.domain.exceptions.EntityNotFoundException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class UsersExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String GENERIC_USER_MESSAGE = "An unexpected internal system error has occurred. Please try again and if the problem persists, contact the system administrator.";
    private static final OffsetDateTime NOW = OffsetDateTime.now();

    private final MessageSource messageSource;

    @Value("${users.api.base-uri}")
    private String apiBaseUri;

    private final ProblemDetailMapper problemDetailMapper;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "System Error",
                                    value = """
                                            {
                                              "status": 500,
                                              "type": "https://api.example.com/problems/system-error",
                                              "title": "System Error",
                                              "detail": "An unexpected internal system error has occurred. Please try again and if the problem persists, contact the system administrator.",
                                              "message": "An unexpected internal system error has occurred. Please try again and if the problem persists, contact the system administrator.",
                                              "timestamp": "2024-06-01T12:00:00Z"
                                            }
                                            """
                            )
                    }
            )
    )
    public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemDetailType problemDetailType = ProblemDetailType.SYSTEM_ERROR;
        ProblemDetailDTO problemDetailDto = createProblemDetailDto(
                httpStatus,
                problemDetailType,
                GENERIC_USER_MESSAGE,
                null
        );
        return handleExceptionInternal(ex, problemDetailDto, new HttpHeaders(), httpStatus, request);
    }

    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Business Rule Violation",
                                    value = """
                                            {
                                              "status": 400,
                                              "type": "https://api.example.com/problems/business-rule-violation",
                                              "title": "Business Rule Violation",
                                              "detail": "The operation cannot be completed due to business rule violation.",
                                              "message": "The operation cannot be completed due to business rule violation.",
                                              "timestamp": "2024-06-01T12:00:00Z"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Invalid data",
                                    value = """
                                            {
                                              "status": 400,
                                              "type": "https://api.example.com/problems/invalid-data",
                                              "title": "Invalid Data",
                                              "detail": "One or more fields are invalid. Fill in correctly and try again.",
                                              "message": "One or more fields are invalid. Fill in correctly and try again.",
                                              "timestamp": "2024-06-01T12:00:00Z",
                                              "fields": [
                                                {
                                                  "name": "email",
                                                  "message": "The email must be a valid email address."
                                                },
                                                {
                                                  "name": "password",
                                                  "message": "The password must be at least 8 characters long."
                                                }
                                              ]
                                            }
                                            """
                            )
                    }
            )
    )
    public ResponseEntity<Object> handleBusinessRuleException(BusinessRuleException ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ProblemDetailType problemDetailType = ProblemDetailType.BUSINESS_RULE_VIOLATION;
        ProblemDetailDTO problemDetailDto = createProblemDetailDto(
                httpStatus,
                problemDetailType,
                ex.getMessage(),
                null
        );
        return handleExceptionInternal(ex, problemDetailDto, new HttpHeaders(), httpStatus, request);
    }

    @ExceptionHandler(EntityInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ApiResponse(
            responseCode = "409",
            description = "Conflict",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Entity In Use",
                                    value = """
                                            {
                                              "status": 409,
                                              "type": "https://api.example.com/problems/entity-in-use",
                                              "title": "Entity In Use",
                                              "detail": "The entity you are trying to delete is currently in use and cannot be removed.",
                                              "message": "The entity you are trying to delete is currently in use and cannot be removed.",
                                              "timestamp": "2024-06-01T12:00:00Z"
                                            }
                                            """
                            )
                    }
            )
    )
    public ResponseEntity<Object> handleEntityInUseException(EntityInUseException ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        ProblemDetailType problemDetailType = ProblemDetailType.ENTITY_IN_USE;
        ProblemDetailDTO problemDetailDto = createProblemDetailDto(
                httpStatus,
                problemDetailType,
                ex.getMessage(),
                null
        );
        return handleExceptionInternal(ex, problemDetailDto, new HttpHeaders(), httpStatus, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Entity Not Found",
                                    value = """
                                            {
                                              "status": 404,
                                              "type": "https://api.example.com/problems/resource-not-found",
                                              "title": "Resource Not Found",
                                              "detail": "The requested resource was not found.",
                                              "message": "The requested resource was not found.",
                                              "timestamp": "2024-06-01T12:00:00Z"
                                            }
                                            """
                            )
                    }
            )
    )
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ProblemDetailType problemDetailType = ProblemDetailType.RESOURCE_NOT_FOUND;
        ProblemDetailDTO problemDetailDto = createProblemDetailDto(
                httpStatus,
                problemDetailType,
                ex.getMessage(),
                null
        );
        return handleExceptionInternal(ex, problemDetailDto, new HttpHeaders(), httpStatus, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ProblemDetailType problemDetailType = ProblemDetailType.INVALID_DATA;
        ProblemDetailDTO problemDetailDto = createProblemDetailDto(
                httpStatus,
                problemDetailType,
                problemDetailType.getMessage(),
                toFields(ex.getBindingResult().getAllErrors())
        );
        return handleExceptionInternal(ex, problemDetailDto, new HttpHeaders(), httpStatus, request);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex instanceof MissingRequestHeaderException missingRequestHeaderException) {
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            ProblemDetailType problemDetailType = ProblemDetailType.INVALID_HEADER;
            String detail = String.format("The request header '%s' is missing. Please provide the required header and try again.",
                    missingRequestHeaderException.getHeaderName()
            );
            ProblemDetailDTO problemDetailDto = createProblemDetailDto(
                    httpStatus,
                    problemDetailType,
                    detail,
                    null
            );
            return handleExceptionInternal(ex, problemDetailDto, headers, status, request);
        }
        return super.handleServletRequestBindingException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex instanceof MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            ProblemDetailType problemDetailType = ProblemDetailType.INVALID_PARAMETER;
            String detail = String.format("The parameter '%s' received the value '%s', which is of an invalid type. Correct and provide a value compatible with the type %s.",
                    methodArgumentTypeMismatchException.getPropertyName(),
                    methodArgumentTypeMismatchException.getValue(),
                    methodArgumentTypeMismatchException.getParameter().getParameterType().getSimpleName()
            );
            ProblemDetailDTO problemDetailDto = createProblemDetailDto(
                    httpStatus,
                    problemDetailType,
                    detail,
                    null
            );
            return handleExceptionInternal(ex, problemDetailDto, headers, status, request);
        }
        return super.handleTypeMismatch(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ProblemDetailType problemDetailType = ProblemDetailType.RESOURCE_NOT_FOUND;
        String detail = String.format("The resource '%s' who you tried to access is not found.", ex.getResourcePath());
        ProblemDetailDTO problemDetailDto = createProblemDetailDto(
                httpStatus,
                problemDetailType,
                detail,
                null
        );
        return handleExceptionInternal(ex, problemDetailDto, new HttpHeaders(), httpStatus, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (Objects.nonNull(ex.getCause()) && ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            return handleInvalidFormatException(invalidFormatException, headers, status, request);
        }

        if (Objects.nonNull(ex.getCause()) && ex.getCause() instanceof PropertyBindingException propertyBindingException) {
            return handlePropertyBindingException(propertyBindingException, headers, status, request);
        }

        HttpStatus httpStatus = HttpStatus.valueOf(status.value());
        ProblemDetailType problemDetailType = ProblemDetailType.MALFORMED_REQUEST;
        String detail = "Your request body is malformed. Please correct it and try again.";
        ProblemDetailDTO problemDetailDto = createProblemDetailDto(
                httpStatus,
                problemDetailType,
                detail,
                null
        );
        return handleExceptionInternal(ex, problemDetailDto, new HttpHeaders(), httpStatus, request);
    }

    private ResponseEntity<Object> handlePropertyBindingException(PropertyBindingException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpStatus httpStatus =  HttpStatus.valueOf(status.value());
        ProblemDetailType problemDetailType = ProblemDetailType.MALFORMED_REQUEST;
        String detail = String.format("The property '%s' does not exist. Correct or remove this property and try again.",  getPath(ex.getPath()));
        ProblemDetailDTO problemDetailDto = createProblemDetailDto(
                httpStatus,
                problemDetailType,
                detail,
                null
        );
        return handleExceptionInternal(ex, problemDetailDto, headers, httpStatus, request);
    }

    private ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.valueOf(status.value());
        ProblemDetailType problemDetailType = ProblemDetailType.MALFORMED_REQUEST;
        String detail = String.format("The property '%s' received the value '%s', which is of an invalid type. Correct and provide a value compatible with the type %s.",
                getPath(ex.getPath()),
                ex.getValue(),
                ex.getTargetType().getSimpleName()
        );
        ProblemDetailDTO problemDetailDto = createProblemDetailDto(
                httpStatus,
                problemDetailType,
                detail,
                null
        );
        return handleExceptionInternal(ex, problemDetailDto, headers, httpStatus, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode.value());

        if (Objects.isNull(body)) {
            body = problemDetailMapper.toProblemDetailDto(
                    httpStatus.value(),
                    null,
                    httpStatus.getReasonPhrase(),
                    GENERIC_USER_MESSAGE,
                    GENERIC_USER_MESSAGE,
                    NOW,
                    null
            );
        } else if (body instanceof String s) {
            body = problemDetailMapper.toProblemDetailDto(
                    httpStatus.value(),
                    null,
                    s,
                    GENERIC_USER_MESSAGE,
                    GENERIC_USER_MESSAGE,
                    NOW,
                    null
            );
        }

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    private ProblemDetailDTO createProblemDetailDto(HttpStatus httpStatus, ProblemDetailType problemDetailType,
                                                                                        String detail,  List<ProblemDetailFieldDTO> fields) {
        return problemDetailMapper.toProblemDetailDto(
                httpStatus.value(),
                apiBaseUri.concat(problemDetailType.getUri()),
                problemDetailType.getTitle(),
                detail,
                problemDetailType.getMessage(),
                NOW,
                fields
        );
    }

    private String getPath(List<JsonMappingException.Reference> pathList) {
        return pathList.stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining("."));
    }

    private List<ProblemDetailFieldDTO> toFields(List<ObjectError> objectErrors) {
        if (CollectionUtils.isEmpty(objectErrors)) {
            return Collections.emptyList();
        }

        return objectErrors.stream()
                .map(objectError -> {
                    String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
                    String name = objectError.getObjectName();

                    if (objectError instanceof FieldError fieldError) {
                        name = fieldError.getField();
                    }

                    return problemDetailMapper.toProblemDetailFieldDto(name, message);
                })
                .toList();
    }
}
