package com.yacpot.server.jetty;

import com.yacpot.server.auth.AuthenticationSession;
import com.yacpot.server.auth.AuthenticationSessionFactory;
import com.yacpot.server.rest.Operation;
import com.yacpot.server.rest.TaskException;
import com.yacpot.server.rest.TaskFactory;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class JettyRequestFactory implements TaskFactory<JettyRequestTask, Request>, AuthenticationSessionFactory<Request> {

  private final static Operation DEFAULT_OPERATION = Operation.READ;
  private final static Map<String, Operation> HTTP_TO_OPERATION_MAP = new HashMap<>();

  static {
    HTTP_TO_OPERATION_MAP.put("put", Operation.CREATE);
    HTTP_TO_OPERATION_MAP.put("get", Operation.READ);
    HTTP_TO_OPERATION_MAP.put("post", Operation.UPDATE);
    HTTP_TO_OPERATION_MAP.put("delete", Operation.DELETE);
  }

  @Override
  public JettyRequestTask getTask(@NotNull Request jettyRequest) throws TaskException {
    String path = jettyRequest.getUri().getPath();
    String contextPath = jettyRequest.getContextPath();
    Operation operation = mapMethodToOperation(jettyRequest.getMethod());

    if (StringUtils.isNotBlank(contextPath) && !path.startsWith(contextPath)) {
      throw new TaskException("When contextPath is set all paths must start with this contextpath (" + contextPath + ") for resolving.");
    }
    if (StringUtils.isNotBlank(contextPath) && path.startsWith(contextPath)) {
      path = StringUtils.substringAfter(path, contextPath);
    }

    return new JettyRequestTask(path, jettyRequest.getParameterMap()).setOperation(operation);
  }

  protected Operation mapMethodToOperation(String method) {
    String key = method.toLowerCase();
    if (HTTP_TO_OPERATION_MAP.containsKey(key)) {
      return HTTP_TO_OPERATION_MAP.get(key);
    }

    return DEFAULT_OPERATION;
  }

  @Override
  public AuthenticationSession getSession(@NotNull Request jettyRequest) {
    jettyRequest.getRemoteUser();

    return new AuthenticationSession();
  }
}
