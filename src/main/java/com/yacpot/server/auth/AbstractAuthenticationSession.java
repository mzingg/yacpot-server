package com.yacpot.server.auth;

import com.yacpot.server.model.OrganisationUnit;
import com.yacpot.server.model.SecurityRole;
import com.yacpot.server.model.User;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public abstract class AbstractAuthenticationSession implements AuthenticationSession {

  private UUID sessionId;

  private LocalDateTime expiration;

  private User user;

  public AbstractAuthenticationSession() {
    this.user = User.ANONYMOUS;
    this.expiration = LocalDateTime.now().plusYears(10);
    this.sessionId = UUID.randomUUID();
  }

  @Override
  public UUID getSessionId() {
    return sessionId;
  }

  @Override
  public LocalDateTime getExpiration() {
    return expiration;
  }

  @Override
  public User getUser() {
    return user;
  }

  @Override
  public Collection<SecurityRole> getSystemRoles() {
    return new ArrayList<>();
  }

  @Override
  public Collection<SecurityRole> getRolesInOrganisationUnit(OrganisationUnit ou) {
    return new ArrayList<>();
  }
}
