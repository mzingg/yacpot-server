package com.yacpot.server.auth;

import com.yacpot.server.model.AbstractGenericModel;
import com.yacpot.server.model.OrganisationUnit;
import com.yacpot.server.model.SecurityRole;
import com.yacpot.server.model.User;
import com.yacpot.server.persistence.PersistenceException;
import com.yacpot.server.persistence.UserPersistence;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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

  public AuthenticationValidation authenticate(UserPersistence userPersistence, String userId, Long timestamp, String authenticationCode) throws AuthenticationException {
    if (StringUtils.isBlank(userId) || timestamp == null || timestamp.longValue() < 0 || StringUtils.isBlank(authenticationCode)) {
      throw new AuthenticationException("Invalid authentication credentials.");
    }

    try {
      User authenticatedUser = userPersistence.findByEmail(userId);
      if (authenticatedUser == null) {
        throw new AuthenticationException("Invalid authentication credentials.");
      }

      String authCodeValidationInput = userId + timestamp;
      byte[] authCodeValidation = createHmac(authenticatedUser.userKeyBytes(), authCodeValidationInput);
      byte[] authCodeProvided = Hex.decodeHex(authenticationCode.toCharArray());

      if (!Arrays.equals(authCodeProvided, authCodeValidation)) {
        throw new AuthenticationException("Invalid authentication credentials.");
      }

      this.user = authenticatedUser;

      long validationTimestamp = System.currentTimeMillis();
      authCodeValidationInput = userId + validationTimestamp;
      authCodeValidation = createHmac(authenticatedUser.userKeyBytes(), authCodeValidationInput);

      return new AuthenticationValidation(validationTimestamp, Hex.encodeHexString(authCodeValidation));

    } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | DecoderException | PersistenceException e) {
      throw new AuthenticationException("Invalid authentication credentials.");
    }
  }

  public byte[] createHmac(byte[] key, String hashPayload) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
    final Mac shaHmac = Mac.getInstance("HmacSHA256");
    shaHmac.init(new SecretKeySpec(key, "HmacSHA256"));

    return shaHmac.doFinal(hashPayload.getBytes());
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
