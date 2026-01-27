package br.com.gabrielferreira.users.api.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProblemDetailType {

    BUSINESS_RULE_VIOLATION("Business Rule Violation", "business-rule-violation", "A business rule has been violated."),
    ENTITY_IN_USE("Entity In Use", "entity-in-use", "The entity is currently in use and cannot be modified or deleted."),
    RESOURCE_NOT_FOUND("Resource Not Found", "resource-not-found", "The requested resource could not be found."),
    INVALID_DATA("Invalid Data", "invalid-data", "One or more fields are invalid. Please correct them and try again."),
    INVALID_PARAMETER("Invalid Parameter", "invalid-parameter", "One or more request parameters are invalid."),
    SYSTEM_ERROR("System Error", "system-error", "An internal system error has occurred. Please try again later."),
    INVALID_HEADER("Invalid Header", "invalid-header", "One or more request headers are invalid."),
    MALFORMED_REQUEST("Malformed Request", "malformed-request", "The request body is malformed and cannot be processed."),
    PATCH_OPERATION_ERROR("Patch Operation Error", "patch-operation-error", "An error occurred while processing the patch operation.");

    private final String title;
    private final String uri;
    private final String message;
}
