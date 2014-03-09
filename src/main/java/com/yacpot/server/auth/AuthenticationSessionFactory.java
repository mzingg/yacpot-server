package com.yacpot.server.auth;

public interface AuthenticationSessionFactory<T extends AuthenticationSession, U> {

  T getSession(U factoryParameter) throws AuthenticationException;

}
