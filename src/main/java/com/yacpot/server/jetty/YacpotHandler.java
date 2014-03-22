package com.yacpot.server.jetty;

import com.yacpot.server.auth.AuthenticationResource;
import com.yacpot.server.rest.*;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.CookieHandler;
import java.util.Map;

public class YacpotHandler extends AbstractHandler {

  JettyRequestFactory requestFactory;
  ApplicationMapping<Task> applicationMapping;

  public YacpotHandler() {
    this.requestFactory = new JettyRequestFactory();
    this.applicationMapping = new ApplicationMapping<>();
    this.applicationMapping.registerResource(new AuthenticationResource());
  }

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      JettyRequestTask task = requestFactory.getTask(baseRequest);
      TaskResult result = applicationMapping.resolve(task);

      for (Map.Entry<String, String> userAttributeEntry : result.getUserAttributes().entrySet()) {
        response.addCookie(new Cookie(userAttributeEntry.getKey(), userAttributeEntry.getValue()));
      }

      response.setContentType("application/json;charset=utf-8");
      response.setStatus(HttpServletResponse.SC_OK);
      baseRequest.setHandled(true);
      response.getWriter().println(result.getJson());

    } catch (MappingException | TaskException e) {
      throw new ServletException(e.getLocalizedMessage(), e);
    }
  }
}
