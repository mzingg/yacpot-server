package com.yacpot.server.auth;

import com.yacpot.server.model.OrganisationUnit;
import com.yacpot.server.model.SecurityRole;
import com.yacpot.server.model.User;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class AnonymousSession implements AuthenticationSession {

  private UUID sessionId;

  private LocalDateTime expiration;

  private User user;

  public AnonymousSession() {
    this.user = User.ANONYMOUS;
    this.expiration = LocalDateTime.now().plusYears(1);
    this.sessionId = UUID.randomUUID();
  }

  @Override
  public UUID sessionId() {
    return sessionId;
  }

  @Override
  public LocalDateTime expiration() {
    return expiration;
  }

  @Override
  public User user() {
    return user;
  }

  @Override
  public Collection<SecurityRole> systemRoles() {
    return new ArrayList<>();
  }

  @Override
  public Collection<SecurityRole> rolesInOrganisationUnit(OrganisationUnit ou) {
    return new ArrayList<>();
  }
}
