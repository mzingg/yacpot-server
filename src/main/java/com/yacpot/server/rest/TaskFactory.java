package com.yacpot.server.rest;

public interface TaskFactory<T extends Task, U> {

  T getTask(U factoryParameter) throws TaskException;

}
