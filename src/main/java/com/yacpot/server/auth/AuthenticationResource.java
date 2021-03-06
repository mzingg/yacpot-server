package com.yacpot.server.auth;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.yacpot.core.persistence.mongodb.MongoDbApplication;
import com.yacpot.server.persistence.UserPersistence;
import com.yacpot.server.rest.*;

import java.io.IOException;
import java.io.StringWriter;

@Resource
public class AuthenticationResource {

  private final MongoDbApplication application;

  public AuthenticationResource(MongoDbApplication application) {
    this.application = application;
  }

  @ResourceMapping(pattern = "^/login$", anonymousAllowed = true, supportsOperations = Operation.CREATE)
  public TaskResult login(Task<?> task) {
    // email & passwort auslesen
    // passwort prüfen
    // session erstellen
    // sessionId in Cookie schreiben

    try {
      String userId = task.getAttribute("userId", String.class);
      Long timestamp = Long.parseLong(task.getAttribute("timestamp", String.class));
      String authCode = task.getAttribute("authCode", String.class);

      AuthenticationSession session = new AuthenticationSession();
      AuthenticationValidation validation = session.authenticate(new UserPersistence(application), userId, timestamp, authCode);

      task.setAuthenticationSession(session);

      StringWriter w = new StringWriter();

      JsonFactory factory = new JsonFactory();
      try {
        JsonGenerator generator = factory.createGenerator(w);
        generator.writeStartObject();
        generator.writeBooleanField("login", true);
        generator.writeStringField("validationCode", validation.getValidationCode());
        generator.writeNumberField("timestamp", validation.getTimestamp());
        generator.writeEndObject();

        generator.flush();

      } catch (IOException ignored) {
      }

      return new TaskResult().setJson(w.getBuffer().toString());

    } catch (AuthenticationException e) {
      return new TaskResult().setJson("{login: false}");
    }
  }

  @ResourceMapping(pattern = "^/logout$")
  public TaskResult logout(Task<?> task) {
    // sessionId aus Cookie auslesen
    // session löschen
    // sessionId Cookie löschen

    return new TaskResult().setJson("{logout: true}");
  }
}
