package com.yacpot.server.jetty;

import com.yacpot.server.Application;
import com.yacpot.server.auth.AuthenticationResource;
import com.yacpot.server.rest.*;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class YacpotHandler extends AbstractHandler {

  private Application application;
  private JettyRequestFactory requestFactory;
  private ApplicationMapping<Task> applicationMapping;

  public YacpotHandler(Application application) {
    this.application = application;
    this.requestFactory = new JettyRequestFactory();
    this.applicationMapping = new ApplicationMapping<>();
    this.applicationMapping.registerResource(new AuthenticationResource(application));
  }

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      response.setHeader("Access-Control-Allow-Origin", "*");
      response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE");

      response.setContentType("application/json;charset=utf-8");

      if (HttpMethod.OPTIONS.asString().equals(baseRequest.getMethod())) {
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
      } else {
        JettyRequestTask task = requestFactory.getTask(baseRequest);
        TaskResult result = applicationMapping.resolve(task);

        for (Map.Entry<String, String> userAttributeEntry : result.getUserAttributes().entrySet()) {
          response.addCookie(new Cookie(userAttributeEntry.getKey(), userAttributeEntry.getValue()));
        }

        response.setStatus(HttpServletResponse.SC_OK); // TODO: Derive Response Status from Task state
        baseRequest.setHandled(true);
        response.getWriter().println(result.getJson());
      }

    } catch (MappingException | TaskException e) {
      throw new ServletException(e.getLocalizedMessage(), e);
    }
  }
}
