package com.yacpot.server.auth;

import com.yacpot.server.model.OrganisationUnit;
import com.yacpot.server.model.SecurityRole;
import com.yacpot.server.model.User;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public interface AuthenticationSession extends Serializable {

  UUID sessionId();

  LocalDateTime expiration();

  User user();

  Collection<SecurityRole> systemRoles();

  Collection<SecurityRole> rolesInOrganisationUnit(OrganisationUnit ou);
}
