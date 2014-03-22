package com.yacpot.server.auth;

public interface AuthenticationSessionFactory<U> {

  AuthenticationSession getSession(U factoryParameter) throws AuthenticationException;

}
