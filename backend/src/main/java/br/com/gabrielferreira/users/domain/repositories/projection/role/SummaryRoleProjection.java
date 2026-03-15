package br.com.gabrielferreira.users.domain.repositories.projection.role;

import java.util.UUID;

public interface SummaryRoleProjection {

    UUID getRoleExternalId();

    String getAuthority();
}
