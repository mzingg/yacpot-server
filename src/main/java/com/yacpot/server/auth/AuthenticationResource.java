package com.yacpot.server.auth;

import com.yacpot.server.rest.*;

@Resource
public class AuthenticationResource {

  @ResourceMapping(pattern = "^/login$", anonymousAllowed = true, supportsOperations = Operation.CREATE)
  public TaskResult login(Task task) {
    // email & passwort auslesen
    // passwort prüfen
    // session erstellen
    // sessionId in Cookie schreiben

    return new TaskResult().setJson("{login: true}").addUserAttribute("test", "hallo");
  }

  @ResourceMapping(pattern = "^/logout$")
  public TaskResult logout(Task task) {
    // sessionId aus Cookie auslesen
    // session löschen
    // sessionId Cookie löschen

    return new TaskResult().setJson("{logout: true}");
  }
}
