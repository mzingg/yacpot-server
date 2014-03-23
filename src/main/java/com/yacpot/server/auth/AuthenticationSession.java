package com.yacpot.server.auth;

import com.yacpot.server.model.AbstractGenericModel;
import com.yacpot.server.model.OrganisationUnit;
import com.yacpot.server.model.SecurityRole;
import com.yacpot.server.model.User;
import org.joda.time.LocalDateTime;

import java.util.*;

public class AuthenticationSession extends AbstractGenericModel<AuthenticationSession> {

  private LocalDateTime expiration;

  private User user;

  private List<SecurityRole> systemRoles;

  private Map<OrganisationUnit, List<SecurityRole>> organisationUnitsRoles;

  public AuthenticationSession() {
    super();
    this.user = User.ANONYMOUS;
    this.expiration = LocalDateTime.now().plusYears(10);
    this.systemRoles = new ArrayList<>();
    this.organisationUnitsRoles = new HashMap<>();
  }

  public LocalDateTime getExpiration() {
    return expiration;
  }

  public AuthenticationSession setExpiration(LocalDateTime expiration) {
    this.expiration = expiration;
    return this;
  }

  public User getUser() {
    return user;
  }

  public AuthenticationSession setUser(User user) {
    this.user = user;
    return this;
  }

  public List<SecurityRole> getSystemRoles() {
    return Collections.unmodifiableList(systemRoles);
  }

  public void setSystemRoles(List<SecurityRole> systemRoles) {
    this.systemRoles = systemRoles;
  }

  public AuthenticationSession addSystemRole(SecurityRole role) {
    this.systemRoles.add(role);
    return this;
  }

  public Map<OrganisationUnit, List<SecurityRole>> getOrganisationUnitsRoles() {
    Map<OrganisationUnit, List<SecurityRole>> result = new HashMap<>();
    for (Map.Entry<OrganisationUnit, List<SecurityRole>> entry : organisationUnitsRoles.entrySet()) {
      result.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
    }
    return Collections.unmodifiableMap(result);
  }

  public void setOrganisationUnitsRoles(Map<OrganisationUnit, List<SecurityRole>> organisationUnitsRoles) {
    this.organisationUnitsRoles = organisationUnitsRoles;
  }

  public void addRolesInOrganisationUnit(OrganisationUnit ou, SecurityRole role) {
    if (!organisationUnitsRoles.containsKey(ou)) {
      organisationUnitsRoles.put(ou, new ArrayList<>());
    }
    organisationUnitsRoles.get(ou).add(role);
  }

  public boolean isValid() {
    return user == User.ANONYMOUS || expiration.isAfter(LocalDateTime.now());
  }

}
