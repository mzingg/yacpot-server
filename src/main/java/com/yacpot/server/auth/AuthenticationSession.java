package com.yacpot.server.auth;

import com.yacpot.server.model.OrganisationUnit;
import com.yacpot.server.model.SecurityRole;
import com.yacpot.server.model.User;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public interface AuthenticationSession extends Serializable {

  UUID getSessionId();

  LocalDateTime getExpiration();

  User getUser();

  Collection<SecurityRole> getSystemRoles();

  Collection<SecurityRole> getRolesInOrganisationUnit(OrganisationUnit ou);
}
