package com.yacpot.server.model;

import com.yacpot.core.model.AbstractModel;
import com.yacpot.server.auth.AuthenticationException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class User extends AbstractModel<User> {

  private final static char KEY_INPUT_CONCAT_CHAR = ':';

  public final static User ANONYMOUS = new User().setLabel("Anonymous User");

  private String email;

  private String userKey;

  public User() {
    this.email = StringUtils.EMPTY;
  }

  public String getEmail() {
    return this.email;
  }

  public User setEmail(String email) {
    this.email = email;
    return this;
  }

  public User setUserKey(String userKey) {
    this.userKey = userKey;
    return this;
  }

  public String getUserKey() {
    return this.userKey;
  }

  public byte[] userKeyBytes() {
    if (StringUtils.isBlank(userKey)) {
      return new byte[0];
    }

    try {
      return Hex.decodeHex(userKey.toCharArray());
    } catch (DecoderException e) {
      return new byte[0];
    }
  }

  public User changePassword(String newPassword, String oldPassword) throws AuthenticationException {
    boolean oldPasswordRequired = StringUtils.isNotBlank(getUserKey());
    if (oldPasswordRequired) {
      if (StringUtils.isBlank(oldPassword)) {
        throw new IllegalArgumentException("Old password is required.");
      }
      try {
        byte[] oldUserKeyValidation = generateUserKey(oldPassword);
        if (!Arrays.equals(oldUserKeyValidation, userKey.getBytes())) {
          throw new IllegalArgumentException("Old password does not match.");
        }
      } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        throw new AuthenticationException("Could not create verification key: " + e.getLocalizedMessage(), e);
      }
    }

    try {
      this.userKey = Hex.encodeHexString(generateUserKey(newPassword));
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new AuthenticationException("Could not create new user key: " + e.getLocalizedMessage(), e);
    }
    return this;
  }

  private byte[] generateUserKey(String userPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
    String algorithm = "PBKDF2WithHmacSHA1";
    int derivedKeyLength = 256;
    int iterations = 500;

    byte[] salt = (getEmail() + userPassword).getBytes();

    KeySpec spec = new PBEKeySpec(userPassword.toCharArray(), salt, iterations, derivedKeyLength);

    SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

    return f.generateSecret(spec).getEncoded();
  }

}
